package cn.xiaoxige.serviceassistantprocessor

import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author xiaoxige
 * @date 4/4/21 6:31 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 自动写入的代码
 * 考虑到使用方可能会使用 java , 为了方便直接使用 javapoet 而非 kotlinpoet
 */
class AutoWriteProxy(
    private val needInjectedInfo: Map<String, Pair<String, Boolean>>,
    private val injectedInfo: Map<String, Boolean>,
    private val filer: Filer
) {
    fun write() {

        // 注解
        val annotation =
            AnnotationSpec.builder(ClassName.get("androidx.annotation", "Keep")).build()
        // 构造方法
        val constructorMethod = createInitMethod()
        // 属性
        val field = createField()
        // 方法
        val methods = createMethodProducer()

        val autoClass = TypeSpec.classBuilder(NAME_AUTO_WRITE_CLASS)
            .addJavadoc("This class is a Service Assistant Processor transfer center class.\n which is automatically generated. Please do not make any changes.\n")
            .addAnnotation(annotation)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(constructorMethod)
            .addField(field)
            .addMethods(methods)
            .addStaticBlock(CodeBlock.of("init();\n"))
            .build()

        JavaFile.builder(NAME_PACKAGE_AUTO_WRITE_CLASS, autoClass)
            .build().writeTo(filer)
    }

    private fun createField(): FieldSpec {
        val mapType = ParameterizedTypeName.get(
            ClassName.get(Map::class.java),
            ClassName.get(String::class.java),
            ClassName.get(Any::class.java)
        )
        return FieldSpec.builder(mapType, "sMap", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .addJavadoc("The injected object of singleton automatically exists here.\n")
            .initializer("""new ${'$'}T()""", LinkedHashMap::class.java)
            .build()
    }

    private fun createInitMethod(): MethodSpec {
        val methodSpecBuilder = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .addJavadoc("It mainly initializes the injection implementation of single instance.\n")

        needInjectedInfo.keys.forEach {
            val value = needInjectedInfo[it] ?: return@forEach
            // 如果是单例的话, 直接加入 sMap 中
            if (value.second) {
                val full = value.first
                methodSpecBuilder.addStatement(
                    """sMap.put("$it", new ${'$'}T())""",
                    ClassName.get(getPackage(full), getClassName(full))
                )
            }
        }

        return methodSpecBuilder.build()
    }

    private fun createMethodProducer(): List<MethodSpec> {
        val methods = mutableListOf<MethodSpec>()

        // 自动生成注入获取实现的方法
        needInjectedInfo.keys.forEach {
            val needInjected = needInjectedInfo[it] ?: return@forEach
            methods.add(
                createMethod(
                    getAutoMethodName(it),
                    it,
                    needInjected.first,
                    needInjected.second
                )
            )
        }

        // 防止手动注册了没有实现的注入内容
        injectedInfo.filter {
            needInjectedInfo[it.key] == null
        }.forEach { entity ->
            if (entity.value) {
                throw RuntimeException("No corresponding injection implementation was found --> ${entity.key}")
            }
            createMethod(getAutoMethodName(entity.key), entity.key, "", true)
        }

        return methods
    }

    private fun createMethod(
        autoMethodName: String,
        needInjectedInterface: String,
        needInjectedFullClass: String,
        isSingleCase: Boolean
    ): MethodSpec {
        val methodSpecBuilder = MethodSpec.methodBuilder(autoMethodName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .addJavadoc("$needInjectedInterface ---> $needInjectedFullClass\n")
            .returns(
                ClassName.get(
                    getPackage(needInjectedInterface),
                    getClassName(needInjectedInterface)
                )
            )

        if (needInjectedFullClass.isEmpty()) {
            return methodSpecBuilder
                .addStatement("return null")
                .build()
        }

        return if (isSingleCase) {
            methodSpecBuilder.addStatement("""return ($needInjectedInterface)sMap.get("$needInjectedInterface")""")
        } else {
            methodSpecBuilder.addStatement(
                """return new ${'$'}T()""",
                ClassName.get(
                    getPackage(needInjectedFullClass),
                    getClassName(needInjectedFullClass)
                )
            )
        }.build()
    }

    private fun getAutoMethodName(path: String): String {
        val names = path.split(".")
        if (names.isEmpty()) throw RuntimeException("Auto get build method name error.")
        val name = names[names.size - 1]
        return "get$name"
    }

    private fun getPackage(path: String): String {
        val index = path.lastIndexOf(".")
        return path.substring(0, index)
    }

    private fun getClassName(path: String): String {
        val startIndex = path.lastIndexOf(".")
        return path.substring(startIndex + 1, path.length)
    }

    companion object {
        private const val NAME_PACKAGE_AUTO_WRITE_CLASS = "cn.xiaoxige.serviceassistantprocessor"
        private const val NAME_AUTO_WRITE_CLASS =
            "AutoInjectedProducer"

    }
}