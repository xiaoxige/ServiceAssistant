package cn.xiaoxige.logincomponent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_main)

        registerListener()
    }

    private fun registerListener() {
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            LoginActivity.showActivity(this)
        }
    }
}