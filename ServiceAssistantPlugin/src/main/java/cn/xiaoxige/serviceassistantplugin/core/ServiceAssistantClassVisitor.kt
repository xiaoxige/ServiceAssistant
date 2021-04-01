package cn.xiaoxige.serviceassistantplugin.core

import org.objectweb.asm.*

/**
 * @author xiaoxige
 * @date 3/29/21 11:00 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 类访问
 */
class ServiceAssistantClassVisitor(private val byteArray: ByteArray) : ClassVisitor(Opcodes.ASM5) {

    private lateinit var mVisitorClassName: String
    private lateinit var mVisitorClassSignature: String

    fun visitor(): ByteArray {
        val classReader = ClassReader(byteArray)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        cv = classWriter
        classReader.accept(this, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
//        println("version: $version, assess: $access, name: $name, signature: $signature, superName: $superName, interfaces: ${interfaces?.toString()}")
        super.visit(version, access, name, signature, superName, interfaces)
        this.mVisitorClassName = name ?: ""
        this.mVisitorClassSignature = signature ?: ""
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
//        println("descriptor: $descriptor, visible: $visible")
        descriptor?.let {
            // 等于空直接返回
            if (mVisitorClassSignature.isEmpty()) return@let

            if (it.indexOf(SIGNATURE_SERVICE_ANNOTATION) < 0) return@let
            if (mVisitorClassSignature.indexOf(SIGNATURE_I_SERVICE) < 0) {
                throw RuntimeException("find service annotation, but it is parent is not IService")
            }
            val matchResult = "$SIGNATURE_I_SERVICE<L[^>]*;>".toRegex().find(mVisitorClassSignature)
                ?: throw RuntimeException("not find target interface in $mVisitorClassName")
            val matchGroup = matchResult.groupValues
            if (matchGroup.size > 1) {
                throw RuntimeException("find more then 1 interface in $mVisitorClassName")
            }
            val target = matchGroup[0]
            val startIndex = SIGNATURE_I_SERVICE.length + 2
            val endIndex = target.length - 2
            val targetInterface = target.substring(startIndex, endIndex)
            println(targetInterface)
        }
        return super.visitAnnotation(descriptor, visible)
    }

    companion object {

        private const val SIGNATURE_SERVICE_ANNOTATION =
            "Lcn/xiaoxige/serviceassistantcore/annotation/Service"

        private const val SIGNATURE_I_SERVICE = "Lcn/xiaoxige/serviceassistantcore/IService"
    }
}