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
    const val PATH_INTRINSICS = "kotlin/jvm/internal/Intrinsics"

    const val DESC_I_SERVICE = "(Ljava/lang/String;Lcn/xiaoxige/serviceassistantcore/IService;)V"

    const val PATH_SERVICE_REFERENCE = "cn/xiaoxige/serviceassistantcore/Service"

    const val PATH_SERVICE_CLASS = "cn/xiaoxige/serviceassistantcore/Service.class"

    const val PATH_BOOLEAN = "java/lang/Boolean"

    const val SIGNATURE_INJECTED_ANNOTATION = "Lcn/xiaoxige/serviceassistantannotation/Injected;"

    const val SIGNATURE_BOOLEAN = "Ljava/lang/Boolean;"

    const val NAME_BOOLEAN = "booleanValue"

    const val NAME_THROW_NPE = "throwNpe"

    const val NAME_METHOD_INSERT_CODE_CALL = "registerService"

    const val NAME_NEED_INSERT_METHOD = "autoRegisterFromAnnotation"

    const val NAME_GET_TARGET_INSTANCE_METHOD = "getInstance"

    const val DESC_INIT = "<init>"

    const val DESC_SIGNATURE_CONSTRUCTORS = "()V"

    const val DESC_RETURN_BOOLEAN = "()Z"

    const val DESC_RETURN_BOOLEAN_FULL = "(Z)Ljava/lang/Boolean;"

    const val DESC_VALUE_OF = "valueOf"

    fun getInjectedProducerClassFullName(injectedInterface: String): String =
        "${injectedInterface}Producer"
}