package cn.xiaoxige.serviceassistant.api

import cn.xiaoxige.appapi.IAppAbilityApi
import cn.xiaoxige.serviceassistantcore.IService

/**
 * @author xiaoxige
 * @date 3/27/21 11:04 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
class AppAbilityApiImpl : IService<IAppAbilityApi>, IAppAbilityApi {
    /**
     * 使用方提供
     */
    override fun getService(): IAppAbilityApi {
        return AppAbilityApiImpl()
    }

}