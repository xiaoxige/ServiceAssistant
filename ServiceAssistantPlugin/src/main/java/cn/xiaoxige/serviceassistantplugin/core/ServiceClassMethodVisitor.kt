package cn.xiaoxige.serviceassistantplugin.core

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
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
    AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)

        needInsertInfo.forEach {
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitLdcInsn(it.first)
            mv.visitTypeInsn(Opcodes.NEW, it.second)
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                it.second,
                ServiceAssistantConstant.DESC_INIT,
                ServiceAssistantConstant.DESC_SIGNATURE_CONSTRUCTORS,
                false
            )
            mv.visitTypeInsn(Opcodes.CHECKCAST, ServiceAssistantConstant.PATH_I_SERVICE)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                ServiceAssistantConstant.PATH_SERVICE_REFERENCE,
                ServiceAssistantConstant.NAME_METHOD_INSERT_CODE_CALL,
                ServiceAssistantConstant.DESC_I_SERVICE,
                false
            )
        }

    }

}