package cn.xiaoxige.serviceassistantplugin.core

import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

/**
 * @author xiaoxige
 * @date 4/2/21 9:20 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: Service 方法（完全替换原方法体，丢弃旧的 return null）
 */
private class AsmGenerator(
    mv: MethodVisitor, access: Int, name: String?, descriptor: String?
) : GeneratorAdapter(Opcodes.ASM7, mv, access, name, descriptor)

class ServiceClassMethodVisitor(
    private val needInsertInfo: List<Pair<String, String>>,
    mv: MethodVisitor,
    private val access: Int,
    private val name: String?,
    private val descriptor: String?
) : MethodVisitor(Opcodes.ASM7, mv) {

    companion object {
        private val CLASS_TYPE = Type.getType(Class::class.java)
        private val STRING_TYPE = Type.getType(String::class.java)
        private val OBJECT_TYPE = Type.getType(Any::class.java)
        private val MAP_TYPE = Type.getType(Map::class.java)
        private val THROWABLE_TYPE = Type.getType(Throwable::class.java)

        private val SERVICE_TYPE =
            Type.getObjectType("cn/xiaoxige/serviceassistantcore/Service")

        private val GET_NAME = Method.getMethod("String getName()")
        private val MAP_GET = Method.getMethod("Object get(Object)")
        private val MAP_PUT = Method.getMethod("Object put(Object, Object)")
        private val EQUALS = Method.getMethod("boolean equals(Object)")
        private val INIT = Method.getMethod("void <init>()")
    }

    override fun visitCode() {
        mv.visitCode()

        // 临时用 GeneratorAdapter 辅助生成指令，输出直接写给底层 mv
        val gen = AsmGenerator(mv, access, name, descriptor)

        val methodStart = gen.newLabel()
        gen.mark(methodStart)

        val nameVar = 1
        val serviceVar = 2
        val lockVar = 3
        val exVar = 4

        // String name = clazz.getName()
        gen.loadArg(0)
        gen.invokeVirtual(CLASS_TYPE, GET_NAME)
        gen.storeLocal(nameVar, STRING_TYPE)

        // Object service = sServiceRelation.get(name)
        gen.getStatic(SERVICE_TYPE, "sServiceRelation", MAP_TYPE)
        gen.loadLocal(nameVar)
        gen.invokeInterface(MAP_TYPE, MAP_GET)
        gen.storeLocal(serviceVar, OBJECT_TYPE)

        // if (service != null) return service
        gen.loadLocal(serviceVar)
        val syncEntry = gen.newLabel()
        gen.ifNull(syncEntry)
        gen.loadLocal(serviceVar)
        gen.returnValue()

        // synchronized (sLock)
        gen.mark(syncEntry)
        gen.getStatic(SERVICE_TYPE, "sLock", OBJECT_TYPE)
        gen.dup()
        gen.storeLocal(lockVar, OBJECT_TYPE)
        gen.monitorEnter()

        val tryStart = gen.newLabel()
        val tryEnd = gen.newLabel()
        val catchStart = gen.newLabel()

        gen.visitTryCatchBlock(tryStart, tryEnd, catchStart, null)
        gen.mark(tryStart)

        // 第二次检查
        gen.getStatic(SERVICE_TYPE, "sServiceRelation", MAP_TYPE)
        gen.loadLocal(nameVar)
        gen.invokeInterface(MAP_TYPE, MAP_GET)
        gen.storeLocal(serviceVar, OBJECT_TYPE)

        gen.loadLocal(serviceVar)
        val createEntry = gen.newLabel()
        gen.ifNull(createEntry)
        gen.loadLocal(serviceVar)
        gen.loadLocal(lockVar)
        gen.monitorExit()
        gen.returnValue()

        // if-else 链创建实例
        gen.mark(createEntry)

        needInsertInfo.forEach { (interfaceName, implName) ->
            val nextCheck = gen.newLabel()

            gen.push(interfaceName)
            gen.loadLocal(nameVar)
            gen.invokeVirtual(STRING_TYPE, EQUALS)
            gen.ifZCmp(GeneratorAdapter.EQ, nextCheck)

            // service = new Impl()
            val implType = Type.getObjectType(implName.replace(".", "/"))
            gen.newInstance(implType)
            gen.dup()
            gen.invokeConstructor(implType, INIT)
            gen.storeLocal(serviceVar, OBJECT_TYPE)

            // sServiceRelation.put(name, service)
            gen.getStatic(SERVICE_TYPE, "sServiceRelation", MAP_TYPE)
            gen.loadLocal(nameVar)
            gen.loadLocal(serviceVar)
            gen.invokeInterface(MAP_TYPE, MAP_PUT)
            gen.pop()

            gen.mark(nextCheck)
        }

        // 正常返回
        gen.loadLocal(serviceVar)
        gen.loadLocal(lockVar)
        gen.monitorExit()
        gen.mark(tryEnd)
        gen.returnValue()

        // 异常处理：保证释放锁
        gen.mark(catchStart)
        gen.storeLocal(exVar, THROWABLE_TYPE)
        gen.loadLocal(lockVar)
        gen.monitorExit()
        gen.loadLocal(exVar)
        gen.throwException()

        // 局部变量调试信息
        val methodEnd = gen.newLabel()
        gen.mark(methodEnd)

        gen.visitLocalVariable(
            "clazz", "Ljava/lang/Class;", "Ljava/lang/Class<TT;>;",
            methodStart, methodEnd, 0
        )
        gen.visitLocalVariable(
            "name", "Ljava/lang/String;", null,
            methodStart, methodEnd, nameVar
        )
        gen.visitLocalVariable(
            "service", "Ljava/lang/Object;", null,
            methodStart, methodEnd, serviceVar
        )

        mv.visitMaxs(0, 0)
        mv.visitEnd()
    }

    // 拦截原方法体（return null）的所有指令，防止重复写入
    override fun visitInsn(opcode: Int) {}
    override fun visitIntInsn(opcode: Int, operand: Int) {}
    override fun visitVarInsn(opcode: Int, `var`: Int) {}
    override fun visitTypeInsn(opcode: Int, type: String?) {}
    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {}
    override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, descriptor: String?, isInterface: Boolean) {}
    override fun visitInvokeDynamicInsn(name: String?, descriptor: String?, bootstrapMethodHandle: Handle?, vararg bootstrapMethodArguments: Any?) {}
    override fun visitJumpInsn(opcode: Int, label: Label?) {}
    override fun visitLabel(label: Label?) {}
    override fun visitLdcInsn(value: Any?) {}
    override fun visitIincInsn(`var`: Int, increment: Int) {}
    override fun visitTableSwitchInsn(min: Int, max: Int, dflt: Label?, vararg labels: Label?) {}
    override fun visitLookupSwitchInsn(dflt: Label?, keys: IntArray?, labels: Array<out Label>?) {}
    override fun visitMultiANewArrayInsn(descriptor: String?, numDimensions: Int) {}
    override fun visitFrame(type: Int, numLocal: Int, local: Array<out Any>?, numStack: Int, stack: Array<out Any>?) {}
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {}
    override fun visitEnd() {}
}
