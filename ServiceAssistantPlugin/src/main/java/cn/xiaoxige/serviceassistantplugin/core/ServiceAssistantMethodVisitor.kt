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

        val label0 = Label()
        val label1 = Label()
        val label2 = Label()
        mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception")
        val label3 = Label()
        mv.visitLabel(label3)

        mv.visitInsn(ACONST_NULL)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitFieldInsn(
            GETFIELD,
            visitorClassName,
            isAutoInitFieldName,
            "Ljava/lang/Boolean;"
        )
        mv.visitJumpInsn(IF_ACMPEQ, label0)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitFieldInsn(
            GETFIELD,
            visitorClassName,
            isAutoInitFieldName,
            "Ljava/lang/Boolean;"
        )
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/Boolean",
            "booleanValue",
            "()Z",
            false
        )
        val label4 = Label()
        mv.visitJumpInsn(IFNE, label4)
        mv.visitLabel(label0)

        fieldInfo.keys.forEach {

            val signInterface = fieldInfo[it]
                ?: throw RuntimeException("Injection target interface signature error")

            val targetInterfaceProducer = ServiceAssistantConstant.getInjectedProducerClassFullName(
                signInterface.substring(
                    1,
                    signInterface.length - 1
                )
            )

            mv.visitFrame(F_SAME, 0, null, 0, null)
            mv.visitLdcInsn(targetInterfaceProducer.replace("/", "."))
            mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/Class",
                "forName",
                "(Ljava/lang/String;)Ljava/lang/Class;",
                false
            )
            mv.visitInsn(POP)
            val label5 = Label()
            mv.visitLabel(label5)
            mv.visitVarInsn(ALOAD, 0)
            mv.visitMethodInsn(
                INVOKESTATIC,
                targetInterfaceProducer,
                "getInstance",
                "()$signInterface",
                false
            )
            mv.visitFieldInsn(
                PUTFIELD,
                visitorClassName,
                it,
                signInterface
            )
            mv.visitLabel(label1)
            val label6 = Label()
            mv.visitJumpInsn(GOTO, label6)
            mv.visitLabel(label2)
            mv.visitFrame(F_SAME1, 0, null, 1, arrayOf<Any>("java/lang/Exception"))
            mv.visitVarInsn(ASTORE, 1)
            mv.visitLabel(label6)

        }

        mv.visitFrame(F_SAME, 0, null, 0, null)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitInsn(ICONST_1)
        mv.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Boolean",
            "valueOf",
            "(Z)Ljava/lang/Boolean;",
            false
        )
        mv.visitFieldInsn(
            PUTFIELD,
            visitorClassName,
            isAutoInitFieldName,
            "Ljava/lang/Boolean;"
        )
        mv.visitLabel(label4)

    }

}