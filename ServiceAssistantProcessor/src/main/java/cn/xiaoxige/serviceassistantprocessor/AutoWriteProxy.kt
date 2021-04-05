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
        // 属性
        val field = createField()
        val lockField = createLockField()
        // 方法
        val methods = createMethodProducer()

        val autoClass = TypeSpec.classBuilder(NAME_AUTO_WRITE_CLASS)
            .addJavadoc("This class is a Service Assistant Processor transfer center class.\n which is automatically generated. Please do not make any changes.\n")
            .addAnnotation(annotation)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(field)
            .addField(lockField)
            .addMethods(methods)
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
        return FieldSpec.builder(mapType, "sMap", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .addJavadoc("The injected object of singleton automatically exists here.\n")
            .initializer("""new ${'$'}T()""", LinkedHashMap::class.java)
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
            methodSpecBuilder.addStatement("""Object result = sMap.get("$needInjectedInterface")""")
            methodSpecBuilder.beginControlFlow("if(result != null)")
            methodSpecBuilder.addStatement("""return ($needInjectedInterface)result""")
            methodSpecBuilder.endControlFlow()
            // 如果没有找到（即还没有初始化), 进行初始化并记录
            methodSpecBuilder.beginControlFlow("synchronized(sLock)")

            // 再次获取, 如果没有进行初始化
            methodSpecBuilder.addStatement("""result = sMap.get("$needInjectedInterface")""")
            methodSpecBuilder.beginControlFlow("if(result != null)")
            methodSpecBuilder.addStatement("""return ($needInjectedInterface)result""")
            methodSpecBuilder.endControlFlow()

            methodSpecBuilder.addStatement(
                """Object instance = new ${'$'}T()""", ClassName.get(
                    getPackage(needInjectedFullClass),
                    getClassName(needInjectedFullClass)
                )
            )
            // 进行初始化工作
            methodSpecBuilder.addStatement(
                """sMap.put("$needInjectedInterface", instance)"""
            )
            methodSpecBuilder.addStatement("""return ($needInjectedInterface)instance""")

            methodSpecBuilder.endControlFlow()
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