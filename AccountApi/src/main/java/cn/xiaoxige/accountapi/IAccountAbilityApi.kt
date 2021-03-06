package cn.xiaoxige.accountapi

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * @author xiaoxige
 * @date 4/3/21 12:35 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
interface IAccountAbilityApi {

    fun toAccount(context: Context)

    fun getAccountFragment(): Fragment
}