package cn.xiaoxige.serviceassistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import cn.xiaoxige.accountapi.IAccountAbilityApi
import cn.xiaoxige.loginapi.ILoginAbilityApi
import cn.xiaoxige.loginapi.IUserInfoApi
import cn.xiaoxige.serviceassistant.ktx.requestLoginAbilityApi
import cn.xiaoxige.serviceassistantcore.Service

class MainActivity : AppCompatActivity() {

    private val tvDesc by lazy {
        findViewById<TextView>(R.id.tvDesc)
    }

    private val mLoginStateListener = LoginStateChangeBack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDesc.text = "未登录"
        if (Service.getService(IUserInfoApi::class.java)?.isLogin() == true) {
            tvDesc.text = "已登录"
        }

        registerListener()
    }

    private fun registerListener() {

        Service.getService(ILoginAbilityApi::class.java)
            ?.addLoginStateChangedListener(mLoginStateListener)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            requestLoginAbilityApi()?.toLogin(this)
        }

        findViewById<Button>(R.id.btnAccount).setOnClickListener {
            val accountAbilityApi = Service.getService(IAccountAbilityApi::class.java)
            if (accountAbilityApi == null) {
                Toast.makeText(this, "未发现账户组件", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            accountAbilityApi.toAccount(this)
        }

    }

    override fun onDestroy() {
        Service.getService(ILoginAbilityApi::class.java)
            ?.removeLoginStateChangeListener(mLoginStateListener)
        super.onDestroy()
    }

    private inner class LoginStateChangeBack : ILoginAbilityApi.ILoginStateChangedListener {
        override fun change(state: Boolean) {
            tvDesc.text = "登录组件的回调 --> 登录状态: $state"
        }

    }

}