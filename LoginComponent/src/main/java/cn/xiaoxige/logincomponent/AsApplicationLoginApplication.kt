package cn.xiaoxige.logincomponent

import android.app.Application

/**
 * @author xiaoxige
 * @date 3/27/21 10:02 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
class AsApplicationLoginApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        componentInit()

    }

    companion object {
        fun componentInit() {

        }
    }
}