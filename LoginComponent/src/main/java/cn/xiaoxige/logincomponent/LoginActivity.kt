package cn.xiaoxige.logincomponent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.xiaoxige.logincomponent.api.LoginAbilityApiImpl

/**
 * @author xiaoxige
 * @date 3/27/21 9:28 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 登录界面
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_login)


        updateBtnVisibility()

        registerListener()
    }

    private fun registerListener() {
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            with(UserInfo) {
                isLogin = true
                userId = "123456"
            }

            LoginAbilityApiImpl.notifyLoginState(true)

            updateBtnVisibility()

            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            with(UserInfo) {
                isLogin = false
                userId = ""
            }

            LoginAbilityApiImpl.notifyLoginState(false)

            updateBtnVisibility()

            Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBtnVisibility() {
        findViewById<Button>(R.id.btnLogin).visibility =
            if (UserInfo.isLogin) View.GONE else View.VISIBLE

        findViewById<Button>(R.id.btnLogout).visibility =
            if (UserInfo.isLogin) View.VISIBLE else View.GONE
    }

    companion object {
        fun showActivity(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}