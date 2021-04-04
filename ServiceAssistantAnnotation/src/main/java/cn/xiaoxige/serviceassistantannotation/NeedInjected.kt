package cn.xiaoxige.serviceassistantannotation

/**
 * @author xiaoxige
 * @date 4/4/21 12:02 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 需要注入类的注解
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NeedInjected(val isSingleCase: Boolean = true)