package cn.xiaoxige.accountcomponent

import android.app.Application

/**
 * @author xiaoxige
 * @date 3/27/21 10:13 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
class AsApplicationAccountApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        componentInit()

    }

    companion object {
        fun componentInit() {

        }
    }
}