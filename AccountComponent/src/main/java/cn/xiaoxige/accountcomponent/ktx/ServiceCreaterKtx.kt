package cn.xiaoxige.accountcomponent.ktx

import cn.xiaoxige.loginapi.ILoginAbilityApi
import cn.xiaoxige.serviceassistantcore.Service

/**
 * @author xiaoxige
 * @date 3/28/21 3:11 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */

fun requestLoginAbilityApi(): ILoginAbilityApi? {
    return Service.getService(ILoginAbilityApi::class.java) ?: return null
}