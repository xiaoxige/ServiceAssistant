package cn.xiaoxige.loginapi

/**
 * @author xiaoxige
 * @date 3/27/21 9:53 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 用户信息的 api
 */
interface IUserInfoApi {

    /**
     * 是否登录
     */
    fun isLogin(): Boolean

    /**
     * 获取用户 id
     */
    fun getUserId(): String
}