package cn.xiaoxige.serviceassistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author xiaoxige
 * @date 4/3/21 12:49 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)
    }

    companion object {

        fun showActivity(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}