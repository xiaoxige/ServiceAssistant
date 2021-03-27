package cn.xiaoxige.logincomponent.api

import cn.xiaoxige.loginapi.ILoginComponentInitApi
import cn.xiaoxige.logincomponent.AsApplicationLoginApplication
import cn.xiaoxige.serviceassistantcore.IService

/**
 * @author xiaoxige
 * @date 3/27/21 10:03 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 初始化
 */
class LoginComponentInitApiImpl : IService<ILoginComponentInitApi>, ILoginComponentInitApi {
    /**
     * 使用方提供
     */
    override fun getService(): ILoginComponentInitApi {
        return LoginComponentInitApiImpl()
    }

    /**
     * 登录组件初始化
     */
    override fun componentInit() {
        AsApplicationLoginApplication.componentInit()
    }

}