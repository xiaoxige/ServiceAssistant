package cn.xiaoxige.serviceassistantprocessor

import cn.xiaoxige.serviceassistantannotation.Injected
import cn.xiaoxige.serviceassistantannotation.NeedInjected
import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * @author xiaoxige
 * @date 4/4/21 9:35 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 注解处理
 */
@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {

    private lateinit var mElementUtils: Elements
    private lateinit var mFiler: Filer
    private lateinit var mMessage: Messager
    private lateinit var mTypeUtils: Types

    private val mNeedInjectedInfo = mutableMapOf<String, Pair<String, Boolean>>()
    private val mInjectedInfo = mutableMapOf<String, Boolean>()

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        this.mElementUtils = p0!!.elementUtils
        this.mFiler = p0.filer
        this.mMessage = p0.messager
        this.mTypeUtils = p0.typeUtils
        i("Service Assistant AnnotationProcessor init")
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            NeedInjected::class.java.canonicalName,
            Injected::class.java.canonicalName
        )
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        if (p0 == null || p0.isEmpty()) return false
        p0.forEach { element ->
            if (element.qualifiedName.contentEquals(NeedInjected::class.java.canonicalName)) {
                // NeedInjected
                p1?.getElementsAnnotatedWith(NeedInjected::class.java)?.forEach {
                    // NeedInjected 必须是类
                    if (!it.kind.isClass) {
                        e("NeedInjected use in a not class")
                        return false
                    }

                    // NeedInjected 的类不能是抽象和私有的和可继承的
                    val modifiers = it.modifiers
                    if (modifiers.contains(Modifier.ABSTRACT) || modifiers.contains(Modifier.PRIVATE) || modifiers.contains(
                            Modifier.PROTECTED
                        )
                    ) {
                        e("NeedInjected class can not abstract or private or protected.")
                        return false
                    }

                    if (handleNeedInjected(it as TypeElement).not()) return false
                }
            } else if (element.qualifiedName.contentEquals(Injected::class.java.canonicalName)) {
                // Injected
                p1?.getElementsAnnotatedWith(Injected::class.java)?.forEach {
                    // Injected 必须是属性
                    if (!it.kind.isField) {
                        e("Injected must is Field.")
                    }

                    if (handleInjected(it).not()) return false
                }
            }
        }

        mNeedInjectedInfo.keys.forEach {
            val value = mNeedInjectedInfo[it] ?: return@forEach
            i("NeedInjected: $it -> ${value.first} --> ${value.second}")
        }

        mInjectedInfo.keys.forEach {
            i("Injected: $it -> ${mInjectedInfo[it]}")
        }

        // 作必要的检查
        mInjectedInfo.keys.forEach {
            if (mInjectedInfo[it] != false) {
                if (mNeedInjectedInfo[it] == null) {
                    e(
                        "The injection relation is wrong. The injection request is forced injection, but the corresponding relation is not scanned. " +
                                "Make sure there is an implementation class or set no mandatory injection." +
                                " ---> $it"
                    )
                }
            }
        }

        mInjectedInfo.keys.forEach {
            val needInjectedInfo = mNeedInjectedInfo[it]
            AutoWriteInjectedInfoProducer(
                it,
                needInjectedInfo,
                mFiler
            ).write()
        }

        // 写完进行清理下
        mNeedInjectedInfo.clear()
        mInjectedInfo.clear()

        return true
    }

    private fun handleNeedInjected(
        needInjected: TypeElement
    ): Boolean {
        val interfaces = needInjected.interfaces
        if (interfaces.isEmpty() || interfaces.size > 1) {
            e("Currently, only one interface injection is supported")
        }
        val interfacePath = interfaces[0].toString()
        val annotation = needInjected.getAnnotation(NeedInjected::class.java)
        mNeedInjectedInfo[interfacePath] =
            Pair(needInjected.qualifiedName.toString(), annotation.isSingleCase)
        return true
    }

    private fun handleInjected(
        injected: Element
    ): Boolean {
        val annotation = injected.getAnnotation(Injected::class.java)
        mInjectedInfo[injected.asType().toString()] = annotation.isForce
        return true
    }

    private fun i(msg: String) {
        mMessage.printMessage(Diagnostic.Kind.NOTE, "Service Assistant Processor >>> $msg")
    }

    private fun w(msg: String) {
        mMessage.printMessage(
            Diagnostic.Kind.MANDATORY_WARNING,
            "Service Assistant Processor >>> $msg"
        )
    }

    private fun e(msg: String) {
        mMessage.printMessage(Diagnostic.Kind.ERROR, "Service Assistant Processor >>> $msg")
    }
}