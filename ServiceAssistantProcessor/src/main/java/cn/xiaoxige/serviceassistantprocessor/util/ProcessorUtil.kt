package cn.xiaoxige.serviceassistantprocessor.util

/**
 * @author xiaoxige
 * @date 4/6/21 9:36 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 注解处理器的工具类
 */

fun String.getPackageAndClassName(): Pair<String, String> {
    val index = lastIndexOf(".")
    return Pair(substring(0, index), substring(index + 1, length))
}