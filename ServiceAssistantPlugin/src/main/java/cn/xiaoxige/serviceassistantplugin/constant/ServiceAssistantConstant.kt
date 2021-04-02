package cn.xiaoxige.serviceassistantplugin.constant

import com.android.tools.r8.Keep

/**
 * @author xiaoxige
 * @date 4/2/21 10:27 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 常量
 */
@Keep
object ServiceAssistantConstant {

    const val SIGNATURE_SERVICE_ANNOTATION =
        "Lcn/xiaoxige/serviceassistantcore/annotation/Service"

    const val SIGNATURE_I_SERVICE = "Lcn/xiaoxige/serviceassistantcore/IService"

    const val PATH_I_SERVICE = "cn/xiaoxige/serviceassistantcore/IService"

    const val DESC_I_SERVICE = "(Ljava/lang/String;Lcn/xiaoxige/serviceassistantcore/IService;)V"

    const val PATH_SERVICE_REFERENCE = "cn/xiaoxige/serviceassistantcore/Service"

    const val PATH_SERVICE_CLASS = "cn/xiaoxige/serviceassistantcore/Service.class"

    const val NAME_METHOD_INSERT_CODE_CALL = "registerService"

    const val NAME_NEED_INSERT_METHOD = "autoRegisterFromAnnotation"

    const val DESC_INIT = "<init>"

    const val DESC_SIGNATURE_CONSTRUCTORS = "()V"
}