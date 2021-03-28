package cn.xiaoxige.serviceassistantplugin

import cn.xiaoxige.serviceassistantplugin.transform.ServiceAssistantTransform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.transform.Transformer

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

        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(ServiceAssistantTransform())

        println("service assistant plugin end.")

    }

}