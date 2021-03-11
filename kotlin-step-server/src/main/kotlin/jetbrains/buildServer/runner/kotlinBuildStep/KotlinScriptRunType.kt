package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.web.openapi.PluginDescriptor

class KotlinScriptRunType(val pluginDescriptor: PluginDescriptor, runTypeRegistry: RunTypeRegistry) : RunType() {

    init {
        runTypeRegistry.registerRunType(this)
    }

    override fun getType(): String {
        return TYPE
    }

    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun getDescription(): String {
        return DESCRIPTION
    }

    override fun getRunnerPropertiesProcessor(): PropertiesProcessor? {
        return null
    }

    override fun getEditRunnerParamsJspFilePath(): String? {
        return pluginDescriptor.getPluginResourcesPath("kotlinScriptRunnerParams.jsp")
    }

    override fun getViewRunnerParamsJspFilePath(): String? {
        return pluginDescriptor.getPluginResourcesPath("viewKotlinScriptRunnerParams.jsp")
    }

    override fun getDefaultRunnerProperties(): Map<String, String>? {
        return null
    }

    companion object {
        const val TYPE = "kotlinScript"
        const val DISPLAY_NAME = "Kotlin Script"
        const val DESCRIPTION = "Build step written in Kotlin Script"
    }
}