package cn.xiaoxige.serviceassistantplugin.core

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @author xiaoxige
 * @date 4/5/21 12:16 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 方法访问
 */
class ServiceAssistantMethodVisitor(
    methodVisitor: MethodVisitor,
    private val visitorClassName: String,
    private val isAutoInitFieldName: String,
    private val fieldInfo: Map<String, String>,
    access: Int,
    name: String?,
    desc: String?
) : AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, desc) {

    override fun onMethodEnter() {
        super.onMethodEnter()

        mv.visitInsn(ACONST_NULL)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitFieldInsn(
            GETFIELD,
            visitorClassName,
            isAutoInitFieldName,
            ServiceAssistantConstant.SIGNATURE_BOOLEAN
        )
        val label1 = Label()
        mv.visitJumpInsn(IF_ACMPEQ, label1)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitFieldInsn(
            GETFIELD,
            visitorClassName,
            isAutoInitFieldName,
            ServiceAssistantConstant.SIGNATURE_BOOLEAN
        )
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            ServiceAssistantConstant.PATH_BOOLEAN,
            ServiceAssistantConstant.NAME_BOOLEAN,
            ServiceAssistantConstant.DESC_RETURN_BOOLEAN,
            false
        )
        val label2 = Label()
        mv.visitJumpInsn(IFNE, label2)
        mv.visitLabel(label1)

        fieldInfo.keys.forEach {
            val value = fieldInfo[it]
                ?: throw RuntimeException("Injection target interface signature error")
            insertInjectedProducer(it, value)
        }

        mv.visitVarInsn(ALOAD, 0)
        mv.visitInsn(ICONST_1)
        mv.visitMethodInsn(
            INVOKESTATIC,
            ServiceAssistantConstant.PATH_BOOLEAN,
            ServiceAssistantConstant.DESC_VALUE_OF,
            ServiceAssistantConstant.DESC_RETURN_BOOLEAN_FULL,
            false
        )
        mv.visitFieldInsn(
            PUTFIELD,
            visitorClassName,
            isAutoInitFieldName,
            ServiceAssistantConstant.SIGNATURE_BOOLEAN
        )
        mv.visitLabel(label2)

    }

    private fun insertInjectedProducer(name: String, injectedInterface: String) {

        val targetInterfaceProducer = ServiceAssistantConstant.getInjectedProducerClassFullName(
            injectedInterface.substring(
                1,
                injectedInterface.length - 1
            )
        )

        val label0 = Label()
        val label1 = Label()
        val label2 = Label()
        mv.visitTryCatchBlock(label0, label1, label2, ServiceAssistantConstant.PATH_EXCEPTION)
        mv.visitLabel(label0)
        mv.visitLineNumber(33, label0)
        mv.visitLdcInsn(targetInterfaceProducer.replace("/", "."))
        mv.visitMethodInsn(
            INVOKESTATIC,
            ServiceAssistantConstant.PATH_CLASS,
            ServiceAssistantConstant.DESC_FOR_NAME,
            ServiceAssistantConstant.SIGNATURE_STRING_CLASS,
            false
        )
        mv.visitInsn(POP)
        val label3 = Label()
        mv.visitLabel(label3)
        mv.visitLineNumber(34, label3)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(
            INVOKESTATIC,
            targetInterfaceProducer,
            ServiceAssistantConstant.NAME_GET_TARGET_INSTANCE_METHOD,
            "()$injectedInterface",
            false
        )
        mv.visitFieldInsn(
            PUTFIELD,
            visitorClassName,
            name,
            injectedInterface
        )
        mv.visitLabel(label1)
        mv.visitLineNumber(36, label1)
        val label4 = Label()
        mv.visitJumpInsn(GOTO, label4)
        mv.visitLabel(label2)
        mv.visitLineNumber(35, label2)
        mv.visitFrame(F_SAME1, 0, null, 1, arrayOf<Any>(ServiceAssistantConstant.PATH_EXCEPTION))
        mv.visitVarInsn(ASTORE, 1)
        mv.visitLabel(label4)
    }

}