package cn.xiaoxige.accountcomponent.api

import android.content.Context
import cn.xiaoxige.accountapi.IAccountAbilityApi
import cn.xiaoxige.accountcomponent.AccountActivity
import cn.xiaoxige.serviceassistantcore.IService
import cn.xiaoxige.serviceassistantcore.annotation.Service

/**
 * @author xiaoxige
 * @date 4/3/21 12:38 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
@Service
class AccountAbilityApiImpl : IService<IAccountAbilityApi>, IAccountAbilityApi {
    /**
     * 使用方提供
     */
    override fun getService(): IAccountAbilityApi {
        return AccountAbilityApiImpl()
    }

    override fun toAccount(context: Context) {
        AccountActivity.showActivity(context)
    }

}