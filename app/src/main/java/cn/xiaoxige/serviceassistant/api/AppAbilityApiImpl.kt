package cn.xiaoxige.serviceassistant.api

import android.content.Context
import cn.xiaoxige.appapi.IAppAbilityApi
import cn.xiaoxige.serviceassistant.AboutActivity
import cn.xiaoxige.serviceassistantcore.IService
import cn.xiaoxige.serviceassistantcore.annotation.Service

/**
 * @author xiaoxige
 * @date 3/27/21 11:04 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
@Service
class AppAbilityApiImpl : IService<IAppAbilityApi>, IAppAbilityApi {
    /**
     * 使用方提供
     */
    override fun getService(): IAppAbilityApi {
        return AppAbilityApiImpl()
    }

    override fun toAbout(context: Context) {
        AboutActivity.showActivity(context)
    }

}