package cn.xiaoxige.serviceassistantplugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.internal.impldep.org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author xiaoxige
 * @date 3/28/21 10:36 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: transform
 */
class ServiceAssistantTransform : Transform() {
    /**
     * Returns the unique name of the transform.
     *
     *
     * This is associated with the type of work that the transform does. It does not have to be
     * unique per variant.
     */
    override fun getName(): String {
        return "service-assistant-transform"
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
    }

    private fun handleDirInput(
        dirInput: DirectoryInput?,
        outputProvider: TransformOutputProvider
    ) {
        if (dirInput == null) return

        val dirFile = dirInput.file ?: return
        if (dirFile.isDirectory) {
            dirFile.listFiles()?.forEach { file ->
                println(file.name)
            }
        }

        val outFile = outputProvider.getContentLocation(
            dirInput.name, dirInput.contentTypes, dirInput.scopes,
            Format.DIRECTORY
        )
        FileUtils.copyDirectory(dirInput.file, outFile)
    }

    private fun handleJarInput(
        jarInput: JarInput?,
        outputProvider: TransformOutputProvider
    ) {
        if (jarInput == null) return
        val file = jarInput.file ?: return
        if (!file.absolutePath.endsWith(".jar")) return
        val name = DigestUtils.md5Hex(file.absolutePath)
        val tempFileName = "$name-temp"
        val tempFile = File("${file.parent}${File.separator}${tempFileName}")

        val jarFile = JarFile(file)
        val entries = jarFile.entries()
        val jos = JarOutputStream(FileOutputStream(tempFile))
        jos.use { jos ->
            while (entries.hasMoreElements()) {
                val jarEntity = entries.nextElement()
                val jarName = jarEntity.name
                val zipEntity = ZipEntry(jarName)
                
            }
        }

    }

}