package cn.xiaoxige.serviceassistantcore

/**
 * @author xiaoxige
 * @date 3/27/21 11:06 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 核心
 */
object Service {

    private val mServiceRelation = mutableMapOf<String, Any?>()

    init {
        autoRegisterFromAnnotation()
    }

    private fun autoRegisterFromAnnotation() {
        // auto write when building, like this
        // registerService(key, instance)
    }

    private fun registerService(key: String, service: IService<*>) {
        mServiceRelation.remove(key)
        mServiceRelation[key] = service.getService()
    }

    fun <T> getService(clazz: Class<T>): T? {
        println(clazz.name)
        return try {
            @Suppress("UNCHECKED_CAST")
            mServiceRelation[clazz.name] as T?
        } catch (ex: Exception) {
            null
        }
    }
}