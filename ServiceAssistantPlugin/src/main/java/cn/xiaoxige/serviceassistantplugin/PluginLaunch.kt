package cn.xiaoxige.serviceassistantplugin

import cn.xiaoxige.serviceassistantplugin.task.ServiceAssistantTransformTask
import cn.xiaoxige.serviceassistantplugin.util.Logger
import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author xiaoxige
 * @date 3/27/21 11:45 PM
 * -
 * email: xiaoxigexiaoan@outlook.com
 * desc: 插件启动（AGP 8.0+ 兼容，使用 ScopedArtifacts 替换 Transform API）
 */
class PluginLaunch : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw RuntimeException("service assistant plugin only use in application.")
        }

        // 初始化日志工具
        Logger.make(project)

        Logger.i("service assistant plugin install. -> ${project.name}")

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            val taskProvider = project.tasks.register(
                "${variant.name}ServiceAssistantTransform",
                ServiceAssistantTransformTask::class.java
            )
            variant.artifacts
                .forScope(ScopedArtifacts.Scope.ALL)
                .use(taskProvider)
                .toTransform(
                    ScopedArtifact.CLASSES,
                    ServiceAssistantTransformTask::allJars,
                    ServiceAssistantTransformTask::allDirectories,
                    ServiceAssistantTransformTask::outputJar
                )
        }
    }
}
