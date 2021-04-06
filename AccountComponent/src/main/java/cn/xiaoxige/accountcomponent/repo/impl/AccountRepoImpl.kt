package cn.xiaoxige.accountcomponent.repo.impl

import cn.xiaoxige.accountcomponent.repo.IAccountRepo
import cn.xiaoxige.serviceassistantannotation.NeedInjected

/**
 * @author xiaoxige
 * @date 4/6/21 11:49 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
@NeedInjected
class AccountRepoImpl : IAccountRepo {

    override fun getAccountData(): String {
        return "account data"
    }

}