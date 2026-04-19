package cn.xiaoxige.serviceassistantplugin.task

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import cn.xiaoxige.serviceassistantplugin.core.ServiceAssistantClassVisitor
import cn.xiaoxige.serviceassistantplugin.core.ServiceClassVisitor
import cn.xiaoxige.serviceassistantplugin.util.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * AGP 8.0+ 兼容的 Transform Task，替换旧的 Transform API。
 * 使用 ScopedArtifacts API 接收所有类文件（jar + directory），
 * 扫描 @Service 和 @Injected 注解，输出单个 jar。
 */
abstract class ServiceAssistantTransformTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val outputJar: RegularFileProperty

    @TaskAction
    fun execute() {
        val startTime = System.currentTimeMillis()
        Logger.i("Service Assistant start run")

        val outputFile = outputJar.get().asFile
        outputFile.parentFile?.mkdirs()

        val tempDir = File(outputFile.parentFile, "${outputFile.nameWithoutExtension}_temp")
        tempDir.deleteRecursively()
        tempDir.mkdirs()

        val needScanClassInfo = mutableListOf<Pair<String, String>>()
        var serviceFile: File? = null

        // Step 1: 合并所有输入到临时目录
        allDirectories.get().forEach { dir ->
            mergeDirectory(dir.asFile, tempDir)
        }
        allJars.get().forEach { jar ->
            mergeJar(jar.asFile, tempDir)
        }

        // Step 2: 扫描并转换 class 文件（@Injected 注入 + @Service 收集）
        tempDir.walkTopDown().forEach { file ->
            if (!file.isFile || !shouldProcessClass(file.name)) return@forEach

            val bytes = ServiceAssistantClassVisitor(file.readBytes(), {
                serviceFile = file
            }) { targetInterface, targetClass ->
                needScanClassInfo.add(Pair(targetInterface, targetClass))
            }.visitor()
            file.writeBytes(bytes)
        }

        Logger.i("scan info: ")
        needScanClassInfo.forEach {
            Logger.i("${it.first} -> ${it.second}")
        }

        // Step 3: 将 @Service 映射注入到 Service.class
        if (serviceFile == null || !serviceFile!!.name.endsWith(".class")) {
            Logger.w("can not find Service, Is this what you want？ or dependence cn.xiaoxige.serviceassistantplugin:core:xxx")
        } else {
            val bytes = ServiceClassVisitor(serviceFile!!.readBytes(), needScanClassInfo).visitor()
            serviceFile!!.writeBytes(bytes)
        }

        // Step 4: 打包成输出 jar
        packToJar(tempDir, outputFile)
        tempDir.deleteRecursively()

        Logger.i("Service Assistant finish, current cost time: ${System.currentTimeMillis() - startTime} ms")
    }

    private fun mergeDirectory(inputDir: File, outputDir: File) {
        if (!inputDir.isDirectory) return
        inputDir.copyRecursively(outputDir, overwrite = true)
    }

    private fun mergeJar(jarFile: File, outputDir: File) {
        if (!jarFile.exists() || !jarFile.name.endsWith(".jar")) return
        JarFile(jarFile).use { jar ->
            jar.entries().asSequence().forEach { entry ->
                val outFile = File(outputDir, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                    return@forEach
                }
                outFile.parentFile?.mkdirs()
                jar.getInputStream(entry).use { input ->
                    outFile.writeBytes(input.readBytes())
                }
            }
        }
    }

    private fun packToJar(sourceDir: File, outputJar: File) {
        JarOutputStream(FileOutputStream(outputJar)).use { jos ->
            sourceDir.walkTopDown().forEach { file ->
                if (!file.isFile) return@forEach
                val entryName = file.relativeTo(sourceDir).path.replace("\\", "/")
                val entry = JarEntry(entryName)
                jos.putNextEntry(entry)
                file.inputStream().use { it.copyTo(jos) }
                jos.closeEntry()
            }
        }
    }

    private fun shouldProcessClass(name: String): Boolean {
        return name.endsWith(".class")
                && !name.startsWith("R\$")
                && name != "R.class"
                && name != "BuildConfig.class"
    }
}
