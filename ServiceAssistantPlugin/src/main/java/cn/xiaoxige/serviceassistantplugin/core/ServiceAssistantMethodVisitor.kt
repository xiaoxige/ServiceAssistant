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

        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(
            Opcodes.GETFIELD,
            visitorClassName,
            isAutoInitFieldName,
            ServiceAssistantConstant.SIGNATURE_BOOLEAN
        )
        val label1 = Label()
        mv.visitJumpInsn(Opcodes.IFNULL, label1)
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitFieldInsn(
            Opcodes.GETFIELD,
            visitorClassName,
            isAutoInitFieldName,
            ServiceAssistantConstant.SIGNATURE_BOOLEAN
        )
        mv.visitInsn(Opcodes.DUP)
        val label2 = Label()
        mv.visitJumpInsn(Opcodes.IFNONNULL, label2)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            ServiceAssistantConstant.PATH_INTRINSICS,
            ServiceAssistantConstant.NAME_THROW_NPE,
            ServiceAssistantConstant.DESC_SIGNATURE_CONSTRUCTORS,
            false
        )
        mv.visitLabel(label2)
        mv.visitFrame(
            Opcodes.F_SAME1,
            0,
            null,
            1,
            arrayOf<Any>(ServiceAssistantConstant.PATH_BOOLEAN)
        )
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            ServiceAssistantConstant.PATH_BOOLEAN,
            ServiceAssistantConstant.NAME_BOOLEAN,
            ServiceAssistantConstant.DESC_RETURN_BOOLEAN,
            false
        )
        val label3 = Label()
        mv.visitJumpInsn(Opcodes.IFNE, label3)
        mv.visitLabel(label1)

        fieldInfo.keys.forEach {

            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            val signInterface = fieldInfo[it]
                ?: throw RuntimeException("Injection target interface signature error")
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                ServiceAssistantConstant.getInjectedProducerClassFullName(
                    signInterface.substring(
                        1,
                        signInterface.length - 1
                    )
                ),
                ServiceAssistantConstant.NAME_GET_TARGET_INSTANCE_METHOD,
                "()$signInterface",
                false
            )
            mv.visitFieldInsn(
                Opcodes.PUTFIELD,
                visitorClassName,
                it,
                signInterface
            )
            val label4 = Label()
            mv.visitLabel(label4)

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
        mv.visitLabel(label3)
    }

    private fun findAutoCreateMethodName(name: String): String {
        val desc = name.substring(1, name.length - 1).split("/")
        if (desc.isEmpty()) {
            throw RuntimeException("find auto create method name error.")
        }
        return "get${desc[desc.size - 1]}"
    }
}