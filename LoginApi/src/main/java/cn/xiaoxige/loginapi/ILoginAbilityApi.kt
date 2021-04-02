package cn.xiaoxige.loginapi

import android.content.Context

/**
 * @author xiaoxige
 * @date 3/27/21 9:03 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 登录组件的一个能力输出 api
 */
interface ILoginAbilityApi {

    /**
     * 登录
     */
    fun toLogin(context: Context)

    fun addLoginStateChangedListener(listener: ILoginStateChangedListener)

    fun removeLoginStateChangeListener(listener: ILoginStateChangedListener)

    interface ILoginStateChangedListener {
        fun change(state: Boolean)
    }
}