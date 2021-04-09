package cn.xiaoxige.serviceassistantplugin.core

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import org.objectweb.asm.*

/**
 * @author xiaoxige
 * @date 4/2/21 9:12 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: Service
 */
class ServiceClassVisitor(
    private val byteArray: ByteArray,
    private val needInsertInfo: List<Pair<String, String>>
) :
    ClassVisitor(Opcodes.ASM7) {

    fun visitor(): ByteArray {
        val classReader = ClassReader(byteArray)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        cv = classWriter
        classReader.accept(this, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return if (ServiceAssistantConstant.NAME_NEED_INSERT_METHOD == name) {
            ServiceClassMethodVisitor(
                needInsertInfo,
                super.visitMethod(
                    access,
                    name,
                    descriptor,
                    signature,
                    exceptions
                )
                , access, name, descriptor
            )
        } else {
            super.visitMethod(access, name, descriptor, signature, exceptions)
        }
    }
}