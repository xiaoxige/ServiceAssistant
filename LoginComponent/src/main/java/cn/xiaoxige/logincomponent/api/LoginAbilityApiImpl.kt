package cn.xiaoxige.logincomponent.api

import android.content.Context
import cn.xiaoxige.loginapi.ILoginAbilityApi
import cn.xiaoxige.logincomponent.LoginActivity
import cn.xiaoxige.serviceassistantcore.IService

/**
 * @author xiaoxige
 * @date 3/27/21 9:46 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 登录输出能力的实现
 */
class LoginAbilityApiImpl : IService<ILoginAbilityApi>, ILoginAbilityApi {

    /**
     * 使用方提供
     */
    override fun getService(): ILoginAbilityApi = LoginAbilityApiImpl()

    /**
     * 登录
     */
    override fun toLogin(context: Context) {
        LoginActivity.showActivity(context)
    }

}