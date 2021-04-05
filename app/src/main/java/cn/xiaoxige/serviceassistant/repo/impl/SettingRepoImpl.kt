package cn.xiaoxige.serviceassistant.repo.impl

import cn.xiaoxige.serviceassistant.repo.ISettingRepo
import cn.xiaoxige.serviceassistantannotation.NeedInjected

/**
 * @author xiaoxige
 * @date 4/5/21 9:43 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
@NeedInjected(false)
class SettingRepoImpl : ISettingRepo {

    override fun getSettingInfo(): String {
        return "网络请求得到的 setting 结果"
    }

}