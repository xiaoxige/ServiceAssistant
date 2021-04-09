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

    const val PATH_SERVICE_REFERENCE = "cn/xiaoxige/serviceassistantcore/Service"

    const val PATH_SERVICE_CLASS = "cn/xiaoxige/serviceassistantcore/Service.class"

    const val PATH_BOOLEAN = "java/lang/Boolean"

    const val PATH_MAP = "java/util/Map"

    const val PATH_CLASS = "java/lang/Class"

    const val PATH_STRING = "java/lang/String"

    const val PATH_OBJECT = "java/lang/Object"

    const val PATH_THROWABLE = "java/lang/Throwable"

    const val PATH_EXCEPTION = "java/lang/Exception"

    const val SIGNATURE_INJECTED_ANNOTATION = "Lcn/xiaoxige/serviceassistantannotation/Injected;"

    const val SIGNATURE_BOOLEAN = "Ljava/lang/Boolean;"

    const val SIGNATURE_MAP = "Ljava/util/Map;"

    const val SIGNATURE_CLASS = "Ljava/lang/Class;"

    const val SIGNATURE_OBJECT = "Ljava/lang/Object;"

    const val SIGNATURE_STRING = "Ljava/lang/String;"

    const val SIGNATURE_OBJECT_OBJECT = "(Ljava/lang/Object;)Ljava/lang/Object;"

    const val SIGNATURE_OBJECT_OBJECT_OBJECT =
        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"

    const val SIGNATURE_STRING_CLASS = "(Ljava/lang/String;)Ljava/lang/Class;"

    const val SIGNATURE_OBJECT_BOOLEAN = "(Ljava/lang/Object;)Z"

    const val SIGNATURE_CLASS_T_T = "Ljava/lang/Class<TT;>;"

    const val NAME_BOOLEAN = "booleanValue"

    const val NAME_NEED_INSERT_METHOD = "getService"

    const val NAME_GET_TARGET_INSTANCE_METHOD = "getInstance"

    const val DESC_INIT = "<init>"

    const val DESC_SIGNATURE_CONSTRUCTORS = "()V"

    const val DESC_RETURN_BOOLEAN = "()Z"

    const val DESC_RETURN_BOOLEAN_FULL = "(Z)Ljava/lang/Boolean;"

    const val DESC_RETURN_STRING_FULL = "()Ljava/lang/String;"

    const val DESC_VALUE_OF = "valueOf"

    const val DESC_GET_NAME = "getName"

    const val DESC_FOR_NAME = "forName"

    const val DESC_GET = "get"

    const val DESC_PUT = "put"

    const val DESC_EQUALS = "equals"

    const val DESC_NAME = "name"

    const val DESC_SERVICE = "service"

    const val DESC_S_SERVICE_RELATION = "sServiceRelation"

    const val DESC_S_LOCK = "sLock"

    const val DESC_CLAZZ = "clazz"

    fun getInjectedProducerClassFullName(injectedInterface: String): String =
        "${injectedInterface}Producer"
}