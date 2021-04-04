package cn.xiaoxige.serviceassistantannotation

/**
 * @author xiaoxige
 * @date 4/4/21 12:21 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 注入的注解
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Injected(val isForce: Boolean = true)