package cn.xiaoxige.serviceassistantcore

/**
 * @author xiaoxige
 * @date 3/27/21 11:06 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 核心
 */
object Service {

    init {
        autoRegisterFromAnnotation()
    }

    private fun autoRegisterFromAnnotation() {
        // auto write when building
    }

    inline fun <reified T> registerService(service: IService<T>) {
        val key = T::class.java.name

    }

    fun <T> getService(): T {
        TODO()
    }
}