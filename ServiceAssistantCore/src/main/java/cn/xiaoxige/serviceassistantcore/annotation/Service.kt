package cn.xiaoxige.serviceassistantcore.annotation

/**
 * @author xiaoxige
 * @date 3/27/21 8:44 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 助手服务的注解
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Service(val isSingleCase: Boolean = true) {
}