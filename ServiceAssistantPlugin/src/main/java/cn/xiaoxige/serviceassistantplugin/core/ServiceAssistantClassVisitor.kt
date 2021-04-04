package cn.xiaoxige.serviceassistantplugin.core

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import org.objectweb.asm.*

/**
 * @author xiaoxige
 * @date 3/29/21 11:00 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 类访问
 */
class ServiceAssistantClassVisitor(
    private val byteArray: ByteArray,
    private val serviceTargetFindBack: () -> Unit,
    private val needScanClassInfoBack: (String, String) -> Unit
) : ClassVisitor(Opcodes.ASM5) {

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
        super.visit(version, access, name, signature, superName, interfaces)
        this.mVisitorClassName = name ?: ""
        this.mVisitorClassSignature = signature ?: ""
        if (this.mVisitorClassName == ServiceAssistantConstant.PATH_SERVICE_REFERENCE) {
            // is service
            serviceTargetFindBack.invoke()
        }
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
//        if (mVisitorClassName.indexOf("MainActivity") >= 0) {
//            println("name: $name, desc: $descriptor, sign: $signature, value: ${value?.toString()}")
//        }
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        descriptor?.let {

            if (it.indexOf(ServiceAssistantConstant.SIGNATURE_SERVICE_ANNOTATION) < 0) return@let
            if (mVisitorClassSignature.indexOf(ServiceAssistantConstant.SIGNATURE_I_SERVICE) < 0) {
                throw RuntimeException("find service annotation, but it is parent is not IService")
            }
            val matchResult = "${ServiceAssistantConstant.SIGNATURE_I_SERVICE}<L[^>]*;>".toRegex()
                .find(mVisitorClassSignature)
                ?: throw RuntimeException("not find target interface in $mVisitorClassName")
            val matchGroup = matchResult.groupValues
            if (matchGroup.size > 1) {
                throw RuntimeException("find more then 1 interface in $mVisitorClassName")
            }
            val target = matchGroup[0]
            val startIndex = ServiceAssistantConstant.SIGNATURE_I_SERVICE.length + 2
            val endIndex = target.length - 2
            val targetInterface = target.substring(startIndex, endIndex)

            needScanClassInfoBack.invoke(
                targetInterface.replace("/", "."),
                mVisitorClassName
            )
        }
        return super.visitAnnotation(descriptor, visible)
    }

}