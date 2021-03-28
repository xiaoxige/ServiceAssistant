package cn.xiaoxige.accountcomponent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.xiaoxige.accountcomponent.ktx.requestLoginAbilityApi
import cn.xiaoxige.loginapi.ILoginAbilityApi
import cn.xiaoxige.serviceassistantcore.Service

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_activity_main)

        requestLoginAbilityApi()?.toLogin(this)
    }
}