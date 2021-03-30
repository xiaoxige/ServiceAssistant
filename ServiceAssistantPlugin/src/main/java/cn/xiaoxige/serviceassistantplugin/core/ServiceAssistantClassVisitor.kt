package cn.xiaoxige.serviceassistantplugin.core

import org.objectweb.asm.*

/**
 * @author xiaoxige
 * @date 3/29/21 11:00 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 类访问
 */
class ServiceAssistantClassVisitor(private val byteArray: ByteArray) : ClassVisitor(Opcodes.ASM5) {

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
        println("version: $version, assess: $access, name: $name, signature: $signature, superName: $superName, interfaces: ${interfaces?.toString()}")
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        println("descriptor: $descriptor, visible: $visible")
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        println("typeRef: $typeRef, typePath: ${typePath.toString()}, descriptor: $descriptor, visible: $visible")
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
    }
}