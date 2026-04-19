package cn.xiaoxige.serviceassistantplugin.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * 自定义 ClassWriter，在 COMPUTE_FRAMES 模式下类加载失败时回退到 java/lang/Object。
 * Android 构建环境中某些类（如 androidx）可能不在 plugin 的 classpath 中。
 */
class SafeClassWriter(classReader: ClassReader, flags: Int) : ClassWriter(classReader, flags) {
    override fun getCommonSuperClass(type1: String, type2: String): String {
        return try {
            super.getCommonSuperClass(type1, type2)
        } catch (e: Exception) {
            "java/lang/Object"
        }
    }
}
