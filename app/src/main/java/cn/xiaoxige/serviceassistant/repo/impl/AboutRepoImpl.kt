package cn.xiaoxige.serviceassistant.repo.impl

import cn.xiaoxige.serviceassistant.repo.IAboutRepo
import cn.xiaoxige.serviceassistantannotation.NeedInjected

/**
 * @author xiaoxige
 * @date 4/4/21 12:06 AM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc:
 */
@NeedInjected
class AboutRepoImpl : IAboutRepo {

    override fun getAboutInfo(): String {
        return "通过自动注入调用."
    }

}