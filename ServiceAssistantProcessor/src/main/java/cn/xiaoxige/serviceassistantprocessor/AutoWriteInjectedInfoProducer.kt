package cn.xiaoxige.serviceassistantprocessor

import cn.xiaoxige.serviceassistantprocessor.util.getPackageAndClassName
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author xiaoxige
 * @date 4/6/21 9:29 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 自动生成注入信息
 */
class AutoWriteInjectedInfoProducer(
    private val injectedInterface: String,
    private val needInjectedInfo: Pair<String, Boolean>?,
    private val filer: Filer
) {

    fun write() {

        // 生成类相关的信息
        val injectedInfoProducerFullClass = getInjectedProducerClassFullName()
        val injectedInfoProducerFullClassInfo =
            injectedInfoProducerFullClass.getPackageAndClassName()

        // 目标接口信息
        val injectedInterfaceInfo = injectedInterface.getPackageAndClassName()

        // 注解
        val annotation =
            AnnotationSpec.builder(ClassName.get("androidx.annotation", "Keep")).build()

        // 属性
        val field = createField(
            injectedInterfaceInfo.first,
            injectedInterfaceInfo.second
        )
        val lockField = createLockField()

        // 方法
        val method = createMethod(injectedInterfaceInfo)

        val autoClass = TypeSpec.classBuilder(injectedInfoProducerFullClassInfo.second)
            .addJavadoc("This class is a Service Assistant Processor transfer center class.\n which is automatically generated. Please do not make any changes.\n")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(annotation)
            .addField(lockField)
            .addField(field)
            .addMethod(method)
            .build()

        JavaFile.builder(injectedInfoProducerFullClassInfo.first, autoClass)
            .build().writeTo(filer)
    }

    private fun createField(packageInfo: String, className: String): FieldSpec {
        return FieldSpec.builder(ClassName.get(packageInfo, className), NAME_TARGET_INSTANCE)
            .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
            .addJavadoc("target entity class")
            .initializer("null")
            .build()
    }

    private fun createLockField(): FieldSpec {
        return FieldSpec.builder(
            Any::class.java,
            "sLock",
            Modifier.PRIVATE,
            Modifier.FINAL,
            Modifier.STATIC
        )
            .addJavadoc("Changed mainly for lock guarantee instance\n")
            .initializer("""new ${'$'}T()""", Any::class.java)
            .build()
    }

    private fun createMethod(injectedInterfaceInfo: Pair<String, String>): MethodSpec {

        val methodSpaceBuilder = MethodSpec
            .methodBuilder(NAME_GET_TARGET_INSTANCE_METHOD)
            .addJavadoc("How to get the target instance")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get(injectedInterfaceInfo.first, injectedInterfaceInfo.second))

        // 如果未发现, 那么直接返回 null
        if (needInjectedInfo == null) {
            return methodSpaceBuilder.addStatement("return null").build()
        }

        // 生成目标对象的信息
        val needInjectedInterfaceInfo = needInjectedInfo.first.getPackageAndClassName()
        // 如果为非单例, 那么每次都会产生一个新对象
        if (!needInjectedInfo.second) {
            return methodSpaceBuilder.addStatement(
                """return new ${'$'}T()""",
                ClassName.get(needInjectedInterfaceInfo.first, needInjectedInterfaceInfo.second)
            ).build()
        }

        // 单例模式
        methodSpaceBuilder.beginControlFlow("if($NAME_TARGET_INSTANCE != null)")
        methodSpaceBuilder.addStatement("return $NAME_TARGET_INSTANCE")
        methodSpaceBuilder.endControlFlow()

        methodSpaceBuilder.beginControlFlow("synchronized(sLock)")

        // 再次判断是否为空
        methodSpaceBuilder.beginControlFlow("if($NAME_TARGET_INSTANCE != null)")
        methodSpaceBuilder.addStatement("return $NAME_TARGET_INSTANCE")
        methodSpaceBuilder.endControlFlow()

        methodSpaceBuilder.addStatement(
            """$NAME_TARGET_INSTANCE = new ${'$'}T()""",
            ClassName.get(needInjectedInterfaceInfo.first, needInjectedInterfaceInfo.second)
        )
        methodSpaceBuilder.addStatement("return $NAME_TARGET_INSTANCE")

        methodSpaceBuilder.endControlFlow()

        return methodSpaceBuilder.build()
    }

    private fun getInjectedProducerClassFullName(): String = "${injectedInterface}Producer"

    companion object {
        private const val NAME_TARGET_INSTANCE = "sInstance"
        private const val NAME_GET_TARGET_INSTANCE_METHOD = "getInstance"
    }
}