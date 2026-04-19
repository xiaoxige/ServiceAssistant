package cn.xiaoxige.serviceassistantplugin.core

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class ServiceAssistantMethodVisitor(
    methodVisitor: MethodVisitor,
    private val visitorClassName: String,
    private val isAutoInitFieldName: String,
    private val fieldInfo: Map<String, Pair<String, String>>,
    access: Int,
    name: String?,
    desc: String?
) : AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, desc) {

    override fun onMethodEnter() {
        super.onMethodEnter()

        val labelSkip = Label()
        val labelInject = Label()

        // if (mIsAutoInit == null) goto inject
        mv.visitInsn(Opcodes.ACONST_NULL)
        loadThis()
        mv.visitFieldInsn(
            Opcodes.GETFIELD,
            visitorClassName,
            isAutoInitFieldName,
            ServiceAssistantConstant.SIGNATURE_BOOLEAN
        )
        mv.visitJumpInsn(Opcodes.IF_ACMPEQ, labelInject)

        // if (mIsAutoInit.booleanValue()) goto skip
        loadThis()
        mv.visitFieldInsn(
            Opcodes.GETFIELD,
            visitorClassName,
            isAutoInitFieldName,
            ServiceAssistantConstant.SIGNATURE_BOOLEAN
        )
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            ServiceAssistantConstant.PATH_BOOLEAN,
            ServiceAssistantConstant.NAME_BOOLEAN,
            ServiceAssistantConstant.DESC_RETURN_BOOLEAN,
            false
        )
        mv.visitJumpInsn(Opcodes.IFNE, labelSkip)

        mv.visitLabel(labelInject)

        fieldInfo.keys.forEach {
            val value = fieldInfo[it]
                ?: throw RuntimeException("Injection target interface signature error")
            insertInjectedProducer(it, value)
        }

        // this.mIsAutoInit = Boolean.valueOf(true)
        loadThis()
        mv.visitInsn(Opcodes.ICONST_1)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            ServiceAssistantConstant.PATH_BOOLEAN,
            ServiceAssistantConstant.DESC_VALUE_OF,
            ServiceAssistantConstant.DESC_RETURN_BOOLEAN_FULL,
            false
        )
        mv.visitFieldInsn(
            Opcodes.PUTFIELD,
            visitorClassName,
            isAutoInitFieldName,
            ServiceAssistantConstant.SIGNATURE_BOOLEAN
        )

        mv.visitLabel(labelSkip)
    }

    private fun insertInjectedProducer(name: String, info: Pair<String, String>) {
        val injectedInterface = info.first
        val targetInterfaceProducer = ServiceAssistantConstant.getInjectedProducerClassFullName(
            injectedInterface.substring(1, injectedInterface.length - 1)
        )

        val tryStart = Label()
        val tryEnd = Label()
        val catchStart = Label()
        val afterCatch = Label()

        mv.visitTryCatchBlock(
            tryStart, tryEnd, catchStart,
            ServiceAssistantConstant.PATH_EXCEPTION
        )
        mv.visitLabel(tryStart)

        // Class.forName(producerClassName)
        mv.visitLdcInsn(targetInterfaceProducer.replace("/", "."))
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            ServiceAssistantConstant.PATH_CLASS,
            ServiceAssistantConstant.DESC_FOR_NAME,
            ServiceAssistantConstant.SIGNATURE_STRING_CLASS,
            false
        )
        mv.visitInsn(Opcodes.POP)

        // this.field = XxxProducer.getInstance(sign)
        loadThis()
        mv.visitLdcInsn(info.second)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            targetInterfaceProducer,
            ServiceAssistantConstant.NAME_GET_TARGET_INSTANCE_METHOD,
            "(Ljava/lang/String;)$injectedInterface",
            false
        )
        mv.visitFieldInsn(
            Opcodes.PUTFIELD,
            visitorClassName,
            name,
            injectedInterface
        )

        mv.visitLabel(tryEnd)
        mv.visitJumpInsn(Opcodes.GOTO, afterCatch)

        // catch (Exception e) { /* ignore */ }
        mv.visitLabel(catchStart)
        mv.visitFrame(
            Opcodes.F_SAME1, 0, null,
            1, arrayOf<Any>(ServiceAssistantConstant.PATH_EXCEPTION)
        )
        val exceptionLocal = newLocal(Type.getType("Ljava/lang/Exception;"))
        mv.visitVarInsn(Opcodes.ASTORE, exceptionLocal)

        mv.visitLabel(afterCatch)
    }
}
