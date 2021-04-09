package cn.xiaoxige.serviceassistantplugin.core

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * @author xiaoxige
 * @date 4/2/21 9:20 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: Service 方法
 */
class ServiceClassMethodVisitor(
    private val needInsertInfo: List<Pair<String, String>>,
    methodVisitor: MethodVisitor, access: Int, name: String?, desc: String?
) :
    AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, desc) {

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)

        mv.visitCode()
        val label0 = Label()
        val label1 = Label()
        val label2 = Label()
        mv.visitTryCatchBlock(label0, label1, label2, null)
        val label3 = Label()
        val label4 = Label()
        mv.visitTryCatchBlock(label3, label4, label2, null)
        val label5 = Label()
        mv.visitTryCatchBlock(label2, label5, label2, null)
        val label6 = Label()
        mv.visitLabel(label6)
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            ServiceAssistantConstant.PATH_CLASS,
            ServiceAssistantConstant.DESC_GET_NAME,
            ServiceAssistantConstant.DESC_RETURN_STRING_FULL,
            false
        )
        mv.visitVarInsn(Opcodes.ASTORE, 1)
        val label7 = Label()
        mv.visitLabel(label7)
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            ServiceAssistantConstant.PATH_SERVICE_REFERENCE,
            ServiceAssistantConstant.DESC_S_SERVICE_RELATION,
            ServiceAssistantConstant.SIGNATURE_MAP
        )
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            ServiceAssistantConstant.PATH_MAP,
            ServiceAssistantConstant.DESC_GET,
            ServiceAssistantConstant.SIGNATURE_OBJECT_OBJECT,
            true
        )
        mv.visitVarInsn(Opcodes.ASTORE, 2)
        val label8 = Label()
        mv.visitLabel(label8)
        mv.visitVarInsn(Opcodes.ALOAD, 2)
        val label9 = Label()
        mv.visitJumpInsn(Opcodes.IFNULL, label9)
        val label10 = Label()
        mv.visitLabel(label10)
        mv.visitVarInsn(Opcodes.ALOAD, 2)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitLabel(label9)
        mv.visitFrame(
            Opcodes.F_APPEND,
            2,
            arrayOf<Any>(
                ServiceAssistantConstant.PATH_STRING,
                ServiceAssistantConstant.PATH_OBJECT
            ),
            0,
            null
        )
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            ServiceAssistantConstant.PATH_SERVICE_REFERENCE,
            ServiceAssistantConstant.DESC_S_LOCK,
            ServiceAssistantConstant.SIGNATURE_OBJECT
        )
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ASTORE, 3)
        mv.visitInsn(Opcodes.MONITORENTER)
        mv.visitLabel(label0)
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            ServiceAssistantConstant.PATH_SERVICE_REFERENCE,
            ServiceAssistantConstant.DESC_S_SERVICE_RELATION,
            ServiceAssistantConstant.SIGNATURE_MAP
        )
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            ServiceAssistantConstant.PATH_MAP,
            ServiceAssistantConstant.DESC_GET,
            ServiceAssistantConstant.SIGNATURE_OBJECT_OBJECT,
            true
        )
        mv.visitVarInsn(Opcodes.ASTORE, 2)
        val label11 = Label()
        mv.visitLabel(label11)
        mv.visitVarInsn(Opcodes.ALOAD, 2)
        mv.visitJumpInsn(Opcodes.IFNULL, label3)
        val label12 = Label()
        mv.visitLabel(label12)
        mv.visitVarInsn(Opcodes.ALOAD, 2)
        mv.visitVarInsn(Opcodes.ALOAD, 3)
        mv.visitInsn(Opcodes.MONITOREXIT)
        mv.visitLabel(label1)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitLabel(label3)

        needInsertInfo.forEach {
            mv.visitFrame(
                Opcodes.F_APPEND,
                1,
                arrayOf<Any>(ServiceAssistantConstant.PATH_OBJECT),
                0,
                null
            )
            mv.visitLdcInsn(it.first)
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                ServiceAssistantConstant.PATH_STRING,
                ServiceAssistantConstant.DESC_EQUALS,
                ServiceAssistantConstant.SIGNATURE_OBJECT_BOOLEAN,
                false
            )
            val label13 = Label()
            mv.visitJumpInsn(Opcodes.IFEQ, label13)
            val label14 = Label()
            mv.visitLabel(label14)
            mv.visitTypeInsn(
                Opcodes.NEW,
                it.second.replace(".", "/")
            )
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                it.second.replace(".", "/"),
                ServiceAssistantConstant.DESC_INIT,
                ServiceAssistantConstant.DESC_SIGNATURE_CONSTRUCTORS,
                false
            )
            mv.visitVarInsn(Opcodes.ASTORE, 2)
            val label15 = Label()
            mv.visitLabel(label15)
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                ServiceAssistantConstant.PATH_SERVICE_REFERENCE,
                ServiceAssistantConstant.DESC_S_SERVICE_RELATION,
                ServiceAssistantConstant.SIGNATURE_MAP
            )
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitVarInsn(Opcodes.ALOAD, 2)
            mv.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                ServiceAssistantConstant.PATH_MAP,
                ServiceAssistantConstant.DESC_PUT,
                ServiceAssistantConstant.SIGNATURE_OBJECT_OBJECT_OBJECT,
                true
            )
            mv.visitInsn(Opcodes.POP)
            mv.visitLabel(label13)
        }

        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
        mv.visitVarInsn(Opcodes.ALOAD, 2)
        mv.visitVarInsn(Opcodes.ALOAD, 3)
        mv.visitInsn(Opcodes.MONITOREXIT)
        mv.visitLabel(label4)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitLabel(label2)
        mv.visitFrame(
            Opcodes.F_SAME1,
            0,
            null,
            1,
            arrayOf<Any>(ServiceAssistantConstant.PATH_THROWABLE)
        )
        mv.visitVarInsn(Opcodes.ASTORE, 4)
        mv.visitVarInsn(Opcodes.ALOAD, 3)
        mv.visitInsn(Opcodes.MONITOREXIT)
        mv.visitLabel(label5)
        mv.visitVarInsn(Opcodes.ALOAD, 4)
        mv.visitInsn(Opcodes.ATHROW)
        val label16 = Label()
        mv.visitLabel(label16)
        mv.visitLocalVariable(
            ServiceAssistantConstant.DESC_CLAZZ,
            ServiceAssistantConstant.SIGNATURE_CLASS,
            ServiceAssistantConstant.SIGNATURE_CLASS_T_T,
            label6,
            label16,
            0
        )
        mv.visitLocalVariable(
            ServiceAssistantConstant.DESC_NAME,
            ServiceAssistantConstant.SIGNATURE_STRING,
            null,
            label7,
            label16,
            1
        )
        mv.visitLocalVariable(
            ServiceAssistantConstant.DESC_SERVICE,
            ServiceAssistantConstant.SIGNATURE_OBJECT,
            null,
            label8,
            label16,
            2
        )
        mv.visitMaxs(3, 5)
        mv.visitEnd()

    }

}