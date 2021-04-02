package cn.xiaoxige.serviceassistantplugin.util

import org.gradle.api.Project

/**
 * @author xiaoxige
 * @date 4/2/21 11:56 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 打印日志的类
 */
object Logger {

    private var sLogger: org.gradle.api.logging.Logger? = null

    fun make(project: Project) {
        sLogger = project.logger
    }

    fun i(msg: String) {
        sLogger?.info("ServiceAssistant::Plugin >>> $msg")
    }

    fun e(msg: String) {
        sLogger?.error("ServiceAssistant::Plugin >>> $msg")
    }

    fun w(msg: String) {
        sLogger?.warn("ServiceAssistant::Plugin >>> $msg")
    }
}