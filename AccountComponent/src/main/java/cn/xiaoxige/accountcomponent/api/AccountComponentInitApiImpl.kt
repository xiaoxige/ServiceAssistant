package cn.xiaoxige.accountcomponent.api

import cn.xiaoxige.accountapi.IAccountComponentInitApi
import cn.xiaoxige.accountcomponent.AsApplicationAccountApplication
import cn.xiaoxige.serviceassistantcore.IService

/**
 * @author xiaoxige
 * @date 3/27/21 10:16 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 账户组价初始化
 */
class AccountComponentInitApiImpl : IService<IAccountComponentInitApi>, IAccountComponentInitApi {
    /**
     * 使用方提供
     */
    override fun getService(): IAccountComponentInitApi {
        return AccountComponentInitApiImpl()
    }

    /**
     * 账户组价初始化
     */
    override fun componentInit() {
        AsApplicationAccountApplication.componentInit()
    }

}