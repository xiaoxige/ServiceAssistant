package cn.xiaoxige.serviceassistantplugin.transform

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import cn.xiaoxige.serviceassistantplugin.core.ServiceAssistantClassVisitor
import cn.xiaoxige.serviceassistantplugin.core.ServiceClassVisitor
import cn.xiaoxige.serviceassistantplugin.util.Logger
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import org.apache.commons.io.IOUtils
import java.util.zip.ZipEntry

/**
 * @author xiaoxige
 * @date 3/28/21 10:36 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: transform
 */
class ServiceAssistantTransform : Transform() {

    private val mNeedScanClassInfo = mutableListOf<Pair<String, String>>()
    private var mServiceFile: File? = null

    /**
     * Returns the unique name of the transform.
     *
     *
     * This is associated with the type of work that the transform does. It does not have to be
     * unique per variant.
     */
    override fun getName(): String {
        return "ServiceAssistantTransform"
    }

    /**
     * Returns the type(s) of data that is consumed by the Transform. This may be more than
     * one type.
     *
     * **This must be of type [QualifiedContent.DefaultContentType]**
     */
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * Returns whether the Transform can perform incremental work.
     *
     *
     * If it does, then the TransformInput may contain a list of changed/removed/added files, unless
     * something else triggers a non incremental run.
     */
    override fun isIncremental(): Boolean {
        return false
    }

    /**
     * Returns the scope(s) of the Transform. This indicates which scopes the transform consumes.
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val startTime = System.currentTimeMillis()
        Logger.i("Service Assistant start run")

        val inputs = transformInvocation?.inputs ?: return
        val outputProvider = transformInvocation.outputProvider

        // 如果存在输出文件, 先全部删除。
        outputProvider?.deleteAll()

        inputs.forEach {

            it.jarInputs.forEach { jarInput ->
                handleJarInput(jarInput, outputProvider)
            }

            it.directoryInputs.forEach { dirInput ->
                handleDirInput(dirInput, outputProvider)
            }
        }

        Logger.i("scan info: ")
        mNeedScanClassInfo.forEach {
            Logger.i("${it.first} -> ${it.second}")
        }

        if (mServiceFile == null || !mServiceFile!!.name.endsWith(".jar")) {
            Logger.w("can not find Service, Is this what you want？ or dependence cn.xiaoxige.serviceassistantplugin:core:xxx")
        } else {
            // insert code
            handleServiceInsertCode(mServiceFile!!)
        }

        Logger.i("Service Assistant finish, current cost time: ${System.currentTimeMillis() - startTime} ms")
    }

    private fun handleDirInput(
        dirInput: DirectoryInput?,
        outputProvider: TransformOutputProvider
    ) {
        if (dirInput == null) return

        // 输入文件
        val dirFile = dirInput.file ?: return
        // 输出文件
        val outFile = outputProvider.getContentLocation(
            dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
        )

        if (dirFile.isDirectory) {
            depthTraversalDir(dirFile) {
                val name = it.name
                if (!name.endsWith(".class")
                    || name.startsWith("R\$")
                    || name == "R.class"
                    || name == "BuildConfig.class"
                ) {
                    return@depthTraversalDir
                }
                val byte = ServiceAssistantClassVisitor(it.readBytes(), {
                }) { targetInterface, targetClass ->
                    mNeedScanClassInfo.add(Pair(targetInterface, targetClass))
                }.visitor()
                FileOutputStream(it).use { fos ->
                    fos.write(byte)
                }
            }
        }

        FileUtils.copyDirectory(dirInput.file, outFile)
    }

    private fun handleJarInput(
        jarInput: JarInput?,
        outputProvider: TransformOutputProvider
    ) {
        if (jarInput == null) return
        val file = jarInput.file ?: return
        if (!file.absolutePath.endsWith(".jar")) return
        var name = jarInput.name
        if (name.endsWith(".jar")) {
            name = name.substring(0, name.length - 4)
        }
        val md5Name = DigestUtils.md5Hex(file.absolutePath)

        // 输出文件
        val dest = outputProvider.getContentLocation(
            "$md5Name$name", jarInput.contentTypes, jarInput.scopes, Format.JAR
        )

        val tempFileName = "${md5Name}Tmp"
        val tempFile = File("${file.parent}${File.separator}${tempFileName}")

        val jarFile = JarFile(file)
        val entries = jarFile.entries()
        val outputStream = JarOutputStream(FileOutputStream(tempFile))
        outputStream.use { jos ->
            while (entries.hasMoreElements()) {
                val jarEntity = entries.nextElement()
                val jarName = jarEntity.name
                val zipEntity = ZipEntry(jarName)
                val inputStream = jarFile.getInputStream(zipEntity)
                jos.putNextEntry(zipEntity)
                inputStream.use {
                    val byte = if (jarName.endsWith(".class")
                        && !jarName.startsWith("R\$")
                        && jarName != "R.class"
                        && jarName != "BuildConfig.class"
                    ) {
                        ServiceAssistantClassVisitor(IOUtils.toByteArray(it), {
                            mServiceFile = dest
                        }) { targetInterface, targetClass ->
                            mNeedScanClassInfo.add(Pair(targetInterface, targetClass))
                        }.visitor()
                    } else {
                        IOUtils.toByteArray(it)
                    }
                    jos.write(byte)
                }
                jos.closeEntry()
            }
        }

        jarFile.close()

        FileUtils.copyFile(tempFile, dest)
        tempFile.delete()
    }

    private fun handleServiceInsertCode(serviceFile: File) {
        val tempFile = File(serviceFile.parent, "${serviceFile.name}.temp")
        if (tempFile.exists()) tempFile.delete()

        val jarFile = JarFile(serviceFile)
        val entries = jarFile.entries()
        val outputStream = JarOutputStream(FileOutputStream(tempFile))
        outputStream.use { jos ->
            while (entries.hasMoreElements()) {
                val jarEntity = entries.nextElement()
                val jarName = jarEntity.name
                val zipEntity = ZipEntry(jarName)
                val inputStream = jarFile.getInputStream(zipEntity)
                jos.putNextEntry(zipEntity)
                inputStream.use {
                    val byte = if (jarName == ServiceAssistantConstant.PATH_SERVICE_CLASS) {
                        ServiceClassVisitor(IOUtils.toByteArray(it), mNeedScanClassInfo).visitor()
                    } else {
                        IOUtils.toByteArray(it)
                    }
                    jos.write(byte)
                }
                jos.closeEntry()
            }
        }
        jarFile.close()

        mServiceFile?.delete()
        FileUtils.renameTo(tempFile, mServiceFile)
    }

    private fun depthTraversalDir(file: File, back: (File) -> Unit) {
        if (file.isFile) {
            back.invoke(file)
            return
        }
        file.listFiles()?.forEach {
            depthTraversalDir(it, back)
        }
    }
}