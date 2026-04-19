package cn.xiaoxige.serviceassistantplugin.core

import cn.xiaoxige.serviceassistantplugin.constant.ServiceAssistantConstant
import org.objectweb.asm.*

/**
 * @author xiaoxige
 * @date 4/4/21 11:40 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 属性访问
 */
class ServiceAssistantFieldVisitor(
    fieldVisitor: FieldVisitor,
    private val targetAnnotationBack: (String) -> Unit
) : FieldVisitor(Opcodes.ASM7, fieldVisitor) {

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val av = super.visitAnnotation(descriptor, visible)
        if (ServiceAssistantConstant.SIGNATURE_INJECTED_ANNOTATION == descriptor) {
            return object : AnnotationVisitor(Opcodes.ASM7, av) {
                // 默认值，如果注解没写 sign 就取这个
                private var sign = "default"
                override fun visit(name: String?, value: Any?) {
                    if ("sign" == name && value is String) {
                        sign = value
                    }
                    super.visit(name, value)
                }

                override fun visitEnd() {
                    // 注解扫描完毕，把 sign 回调出去
                    targetAnnotationBack.invoke(sign)
                    super.visitEnd()
                }
            }
        }

        return av
    }
}