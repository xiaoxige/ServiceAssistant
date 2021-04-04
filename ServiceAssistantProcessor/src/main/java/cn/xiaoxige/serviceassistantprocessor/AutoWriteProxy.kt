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

        val field = createField()
        val methods = createMethodProducer()

        val autoClass = TypeSpec.classBuilder(NAME_AUTO_WRITE_CLASS)
            .addJavadoc("This class is a Service Assistant Processor transfer center class.\n which is automatically generated. Please do not make any changes.\n")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(field)
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
        return FieldSpec.builder(mapType, "sMap", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .addJavadoc("The injected object of singleton automatically exists here.\n")
            .initializer("""new ${'$'}T()""", LinkedHashMap::class.java)
            .build()
    }

    private fun createMethodProducer(): List<MethodSpec> {
        val methods = mutableListOf<MethodSpec>()

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

        if (isSingleCase) {
            methodSpecBuilder.addStatement("""return ($needInjectedInterface)sMap.get("$needInjectedInterface")""")
        } else {
            methodSpecBuilder.addStatement(
                """return new ${'$'}T()""",
                ClassName.get(
                    getPackage(needInjectedFullClass),
                    getClassName(needInjectedFullClass)
                )
            )
        }
        return methodSpecBuilder.build()
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