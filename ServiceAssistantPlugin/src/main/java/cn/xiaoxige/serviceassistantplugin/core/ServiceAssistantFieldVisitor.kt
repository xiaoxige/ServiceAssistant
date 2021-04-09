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
    private val targetAnnotationBack: () -> Unit
) :
    FieldVisitor(Opcodes.ASM7, fieldVisitor) {

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        if (ServiceAssistantConstant.SIGNATURE_INJECTED_ANNOTATION == descriptor) {
            targetAnnotationBack.invoke()
        }
        return super.visitAnnotation(descriptor, visible)
    }

}