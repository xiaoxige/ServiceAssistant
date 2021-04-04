package cn.xiaoxige.serviceassistantprocessor

import javax.annotation.processing.Filer

/**
 * @author xiaoxige
 * @date 4/4/21 6:31 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 自动写入的代码
 * 考虑到使用方可能会使用 java , 为了方便直接使用 javapoet 而非 kotlinpoet
 */

class AutoWriteProxy(
    private val needInjectedInfo: Map<String, Pair<String, Boolean>>,
    private val injectedInfo: Map<String, Boolean>,
    private val filer: Filer
) {
    fun write() {

    }

    companion object {
        private val PATH_AUTO_WRITE_CLASS =
            "cn.xiaoxige.serviceassistantprocessor.AutoInjectedProducer"
    }
}