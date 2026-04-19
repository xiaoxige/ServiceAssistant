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
    private val needInjectedInfos: List<Triple<String, Boolean, String>>?,
    private val filer: Filer
) {

    fun write() {

        // 生成类相关的信息
        // cn.xiaoxige.xxx.IXxxProducer
        val injectedInfoProducerFullClass = getInjectedProducerClassFullName()
        // pair<"cn.xiaoxige.xxx", "IXxxProducer">
        val injectedInfoProducerFullClassInfo =
            injectedInfoProducerFullClass.getPackageAndClassName()

        // 目标接口信息
        // pair<"cn.xiaoxige.xxx", "IXxx">
        val injectedInterfaceInfo = injectedInterface.getPackageAndClassName()

        // 注解
        // @androidx.annotation.Keep
        val annotation =
            AnnotationSpec.builder(ClassName.get("androidx.annotation", "Keep")).build()

        // 属性
        // cn.xiaoxige.xxx.IXxx fieldXxx
        // cn.xiaoxige.xxx.IXxx fieldXxxx
        val fields = mutableListOf<FieldSpec>()
        needInjectedInfos?.forEach {
            fields.add(
                createField(
                    injectedInterfaceInfo.first,
                    injectedInterfaceInfo.second,
                    it
                )
            )
        }

        // lock 属性
        // Object cLock
        val lockField = createLockField()

        // 方法
        // getInstance()
        val getInstanceMethod = createGetInstanceMethod(injectedInterfaceInfo)
        // getInstance(sign: String)
        val getInstanceBySignMethod = createGetInstanceBySignMethod(injectedInterfaceInfo)

        // createXxxx()
        val createTargetMethods = mutableListOf<MethodSpec>()
        needInjectedInfos?.forEach {
            createTargetMethods.add(createTargetMethod(injectedInterfaceInfo, it))
        }

        val autoClassBuilder = TypeSpec.classBuilder(injectedInfoProducerFullClassInfo.second)
            .addJavadoc("This class is a Service Assistant Processor transfer center class.\n which is automatically generated. Please do not make any changes.\n")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(annotation)
            .addField(lockField)
            .addMethod(getInstanceMethod)
            .addMethod(getInstanceBySignMethod)

        fields.forEach {
            autoClassBuilder.addField(it)
        }

        createTargetMethods.forEach {
            autoClassBuilder.addMethod(it)
        }

        val autoClass = autoClassBuilder.build()
        JavaFile.builder(injectedInfoProducerFullClassInfo.first, autoClass)
            .build().writeTo(filer)
    }

    private fun createField(
        packageInfo: String,
        className: String,
        target: Triple<String, Boolean, String>
    ): FieldSpec {
        return FieldSpec.builder(
            ClassName.get(packageInfo, className),
            getTargetInstanceFiledName(target.third)
        )
            .addModifiers(Modifier.STATIC, Modifier.PRIVATE, Modifier.VOLATILE)
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

    private fun createGetInstanceMethod(injectedInterfaceInfo: Pair<String, String>): MethodSpec {
        val methodSpaceBuilder = MethodSpec
            .methodBuilder(NAME_GET_TARGET_INSTANCE_METHOD)
            .addJavadoc("How to get the target instance")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(ClassName.get(injectedInterfaceInfo.first, injectedInterfaceInfo.second))

        methodSpaceBuilder.addStatement("return ${NAME_GET_TARGET_INSTANCE_METHOD}(\"default\")")

        return methodSpaceBuilder.build()
    }

    private fun createGetInstanceBySignMethod(injectedInterfaceInfo: Pair<String, String>): MethodSpec {

        val methodSpaceBuilder = MethodSpec
            .methodBuilder(NAME_GET_TARGET_INSTANCE_METHOD)
            .addJavadoc("How to get the target instance")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(ClassName.get("java.lang", "String"), "sign")
            .returns(ClassName.get(injectedInterfaceInfo.first, injectedInterfaceInfo.second))

        // 如果未发现, 那么直接返回 null
        if (needInjectedInfos == null) {
            return methodSpaceBuilder.addStatement("return null").build()
        }

        needInjectedInfos.forEach { needInjectedInfo ->
            val needInjectedInfoSign = needInjectedInfo.third
            methodSpaceBuilder.beginControlFlow("if(sign == \"$needInjectedInfoSign\")")
            methodSpaceBuilder.addStatement("return ${getCreateTargetMethodName(needInjectedInfoSign)}()")
            methodSpaceBuilder.endControlFlow()
        }

        methodSpaceBuilder.addStatement("return null")
        return methodSpaceBuilder.build()
    }

    fun createTargetMethod(
        injectedInterfaceInfo: Pair<String, String>,
        targetInfo: Triple<String, Boolean, String>
    ): MethodSpec {
        val targetClassFullName = targetInfo.first
        val isSingleCase = targetInfo.second
        val sign = targetInfo.third

        val methodSpaceBuilder = MethodSpec
            .methodBuilder(getCreateTargetMethodName(sign))
            .addJavadoc("create target instance")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .returns(ClassName.get(injectedInterfaceInfo.first, injectedInterfaceInfo.second))


        // 生成目标对象的信息
        val needInjectedInterfaceInfo = targetClassFullName.getPackageAndClassName()
        // 如果为非单例, 那么每次都会产生一个新对象
        if (!isSingleCase) {
            return methodSpaceBuilder.addStatement(
                """return new ${'$'}T()""",
                ClassName.get(needInjectedInterfaceInfo.first, needInjectedInterfaceInfo.second)
            ).build()
        }

        // 单例模式
        val filer = getTargetInstanceFiledName(sign)
        methodSpaceBuilder.beginControlFlow("if(${filer} != null)")
        methodSpaceBuilder.addStatement("return $filer")
        methodSpaceBuilder.endControlFlow()

        methodSpaceBuilder.beginControlFlow("synchronized(sLock)")

        // 再次判断是否为空
        methodSpaceBuilder.beginControlFlow("if($filer != null)")
        methodSpaceBuilder.addStatement("return $filer")
        methodSpaceBuilder.endControlFlow()

        methodSpaceBuilder.addStatement(
            """$filer = new ${'$'}T()""",
            ClassName.get(needInjectedInterfaceInfo.first, needInjectedInterfaceInfo.second)
        )
        methodSpaceBuilder.addStatement("return $filer")

        methodSpaceBuilder.endControlFlow()

        return methodSpaceBuilder.build()
    }

    private fun getInjectedProducerClassFullName(): String = "${injectedInterface}Producer"

    private fun getTargetInstanceFiledName(sign: String) = "${NAME_TARGET_INSTANCE_PREFIX}${sign.replaceFirstChar(Char::titlecase)}"

    private fun getCreateTargetMethodName(sign: String) = "${NAME_CREATE_TARGET_METHOD_PREFIX}${sign.replaceFirstChar(Char::titlecase)}"

    companion object {
        private const val NAME_TARGET_INSTANCE_PREFIX = "sInstance"
        private const val NAME_GET_TARGET_INSTANCE_METHOD = "getInstance"
        private const val NAME_CREATE_TARGET_METHOD_PREFIX = "createTarget"
    }
}