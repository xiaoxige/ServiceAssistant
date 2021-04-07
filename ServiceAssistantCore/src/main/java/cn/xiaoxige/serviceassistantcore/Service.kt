package cn.xiaoxige.serviceassistantcore

/**
 * @author xiaoxige
 * @date 3/27/21 11:06 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 核心
 */
object Service {

    private val sServiceRelation = mutableMapOf<String, Any?>()
    private val sLock = Any()

    @JvmStatic
    fun <T> getService(clazz: Class<T>): T? {
        return null
    }
}