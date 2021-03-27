package cn.xiaoxige.serviceassistantplugin

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author xiaoxige
 * @date 3/27/21 11:45 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 插件启动
 */
class PluginLaunch : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw RuntimeException("service assistant plugin only use in application.")
        }

        println("service assistant plugin run.")
    }

}