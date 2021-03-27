package cn.xiaoxige.logincomponent.api

import cn.xiaoxige.loginapi.IUserInfoApi
import cn.xiaoxige.logincomponent.UserInfo
import cn.xiaoxige.serviceassistantcore.IService
import cn.xiaoxige.serviceassistantcore.annotation.Service

/**
 * @author xiaoxige
 * @date 3/27/21 9:55 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 用户信息
 */
@Service
class UserInfoApiImpl : IService<IUserInfoApi>, IUserInfoApi {
    /**
     * 使用方提供
     */
    override fun getService(): IUserInfoApi {
        return UserInfoApiImpl()
    }

    /**
     * 是否登录
     */
    override fun isLogin(): Boolean {
        return UserInfo.isLogin
    }

    /**
     * 获取用户 id
     */
    override fun getUserId(): String {
        return UserInfo.userId
    }

}