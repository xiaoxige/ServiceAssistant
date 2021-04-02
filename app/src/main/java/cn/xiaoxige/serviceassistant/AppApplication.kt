package cn.xiaoxige.serviceassistant

import android.app.Application
import cn.xiaoxige.accountapi.IAccountComponentInitApi
import cn.xiaoxige.loginapi.ILoginComponentInitApi
import cn.xiaoxige.serviceassistantcore.Service

/**
 * @author xiaoxige
 * @date 4/3/21 12:22 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        sApplication = this

        // 进行登录组件的初始化
        Service.getService(ILoginComponentInitApi::class.java)?.componentInit()
        // 进行账户组件的初始化
        Service.getService(IAccountComponentInitApi::class.java)?.componentInit()
    }

    companion object {
        lateinit var sApplication: Application
    }
}