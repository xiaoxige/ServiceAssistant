package cn.xiaoxige.serviceassistantplugin.core

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import cn.xiaoxige.serviceassistantplugin.util.Logger
import org.apache.commons.codec.digest.DigestUtils
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
) : ClassVisitor(Opcodes.ASM7) {

    private lateinit var mVisitorClassName: String
    private lateinit var mVisitorClassSignature: String
    private var mIsInsertInitField = false
    private var mIsAutoInitFieldName: String? = null
    private val mFieldInfo = mutableMapOf<String, String>()

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
        this.mIsInsertInitField = false
        this.mIsAutoInitFieldName = "is${DigestUtils.md5Hex(this.mVisitorClassName)}"
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

        return ServiceAssistantFieldVisitor(
            super.visitField(
                access,
                name,
                descriptor,
                signature,
                value
            )
        ) {
            if (name == null) throw  RuntimeException("Failed to get injection variable name.")
            if (descriptor == null) throw RuntimeException("Failed to get injection variable type.")
            this.mFieldInfo[name] = descriptor
            Logger.i("injected: $name -> $descriptor")

            if (!this.mIsInsertInitField) {
                cv.visitField(
                    Opcodes.ACC_VOLATILE or Opcodes.ACC_PRIVATE,
                    this.mIsAutoInitFieldName,
                    ServiceAssistantConstant.SIGNATURE_BOOLEAN,
                    null,
                    false
                ).visitEnd()
                this.mIsInsertInitField = true
            }
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return if (name == null || name != ServiceAssistantConstant.DESC_INIT || this.mFieldInfo.isEmpty()) {
            super.visitMethod(access, name, descriptor, signature, exceptions)
        } else {
            ServiceAssistantMethodVisitor(
                super.visitMethod(access, name, descriptor, signature, exceptions),
                this.mVisitorClassName,
                this.mIsAutoInitFieldName!!,
                this.mFieldInfo,
                access, name, descriptor
            )
        }

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

    override fun visitEnd() {
        this.mIsInsertInitField = false
        this.mIsAutoInitFieldName = null
        super.visitEnd()
    }

}