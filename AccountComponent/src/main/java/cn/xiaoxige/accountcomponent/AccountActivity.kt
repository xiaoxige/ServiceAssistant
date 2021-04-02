package cn.xiaoxige.accountcomponent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.xiaoxige.appapi.IAppAbilityApi
import cn.xiaoxige.loginapi.IUserInfoApi
import cn.xiaoxige.serviceassistantcore.Service

/**
 * @author xiaoxige
 * @date 4/3/21 12:34 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
class AccountActivity : AppCompatActivity() {

    private val tvUserInfo by lazy {
        findViewById<TextView>(R.id.tvUserInfo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_activity_account)

        registerListener()
        updateUserInfo()
    }

    private fun registerListener() {
        findViewById<Button>(R.id.btnAbout).setOnClickListener {
            Service.getService(IAppAbilityApi::class.java)?.toAbout(this)
        }
    }

    private fun updateUserInfo() {
        tvUserInfo.text = "未登录"
        Service.getService(IUserInfoApi::class.java)?.let {
            tvUserInfo.text = if (it.isLogin()) "userId: ${it.getUserId()}" else "未登录"
        }
    }

    companion object {

        fun showActivity(context: Context) {
            context.startActivity(Intent(context, AccountActivity::class.java))
        }
    }
}