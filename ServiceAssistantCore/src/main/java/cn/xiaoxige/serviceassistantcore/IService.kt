package cn.xiaoxige.serviceassistantcore

/**
 * @author xiaoxige
 * @date 3/27/21 8:55 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 服务接口
 */
interface IService<T> {

    /**
     * 使用方提供
     */
    fun getService(): T
}