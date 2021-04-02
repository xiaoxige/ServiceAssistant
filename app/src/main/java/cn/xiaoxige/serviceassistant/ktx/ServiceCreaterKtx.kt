package cn.xiaoxige.serviceassistant.ktx

import android.widget.Toast
import cn.xiaoxige.loginapi.ILoginAbilityApi
import cn.xiaoxige.serviceassistant.AppApplication
import cn.xiaoxige.serviceassistantcore.Service

/**
 * @author xiaoxige
 * @date 4/3/21 1:12 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */

fun requestLoginAbilityApi(): ILoginAbilityApi? {
    val loginAbilityApi = Service.getService(ILoginAbilityApi::class.java)
    if (loginAbilityApi == null) {
        Toast.makeText(AppApplication.sApplication, "未发现登录组件", Toast.LENGTH_SHORT).show()
        return null
    }

    return loginAbilityApi
}