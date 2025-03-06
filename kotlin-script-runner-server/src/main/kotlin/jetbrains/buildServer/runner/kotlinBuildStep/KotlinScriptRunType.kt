

package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.serverSide.*
import jetbrains.buildServer.util.positioning.PositionAware
import jetbrains.buildServer.web.openapi.PluginDescriptor

class KotlinScriptRunType(val pluginDescriptor: PluginDescriptor, runTypeRegistry: RunTypeRegistry) : RunType() {

    private val myDefaults = mapOf(RunnerParamNames.KOTLIN_PATH to DEFAULT_KOTLIN_PATH_REF)

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

    override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
        return PropertiesProcessor { params ->
            val errors: MutableList<InvalidProperty> = ArrayList()
            val scriptType = notBlank(params, RunnerParamNames.SCRIPT_TYPE, "Script type is not specified", errors)
            if (scriptType == ScriptTypes.FILE) {
                notBlank(params, RunnerParamNames.SCRIPT_FILE, "Script file path is not specified", errors)
                params.remove(RunnerParamNames.SCRIPT_CONTENT)
            } else if (scriptType == ScriptTypes.CUSTOM) {
                notBlank(params, RunnerParamNames.SCRIPT_CONTENT, "Custom script content is not provided", errors)
                params.remove(RunnerParamNames.SCRIPT_FILE)
            }
            errors
        }
    }

    private fun notBlank(params: Map<String, String>, paramName: String, message: String, errors: MutableList<InvalidProperty>): String? {
        val paramValue = params[paramName]
        if (paramValue.isNullOrBlank())
            errors.add(InvalidProperty(paramName, message))
        return paramValue
    }

    override fun getEditRunnerParamsJspFilePath(): String {
        return pluginDescriptor.getPluginResourcesPath("kotlinScriptRunnerParams.jsp")
    }

    override fun getViewRunnerParamsJspFilePath(): String {
        return pluginDescriptor.getPluginResourcesPath("viewKotlinScriptRunnerParams.jsp")
    }

    override fun getDefaultRunnerProperties(): Map<String, String>? {
        return myDefaults;
    }

    override fun describeParameters(parameters: Map<String?, String?>): String {
        if (parameters[RunnerParamNames.SCRIPT_TYPE] == ScriptTypes.FILE) {
            return "Script file: " + (parameters[RunnerParamNames.SCRIPT_FILE] ?: "") + " " + (parameters["ktsArgs"] ?: "")
        } else {
            return "Custom script: " + customScriptDescription(parameters[RunnerParamNames.SCRIPT_CONTENT])
        }
    }

    fun customScriptDescription(scriptContent: String?):String {
        when(scriptContent) {
           null, "" -> {
               return "<empty>"
           }
           else -> {
               val scriptLines = scriptContent.lines()
               return when(scriptLines.size) {
                   0 -> "<empty>"
                   1 -> scriptLines[0]
                   else -> scriptLines[0] + " (and ${scriptLines.size - 1} more lines)"
               }
           }
        }
    }

    override fun getTags(): MutableSet<String> {
        return mutableSetOf("script", "Kotlin")
    }

    companion object {
        const val TYPE = "kotlinScript"
        const val DISPLAY_NAME = "Kotlin Script"
        const val DESCRIPTION = "Kotlin Script runner"
        private const val DOCKER_WRAPPER = "dockerWrapper"
        private const val DOCKER_SUPPORT_ENABLED = "teamcity.plugin.kotlinScript.dockerSupport.enabled"
    }

    override fun getIconUrl(): String {
        return this.pluginDescriptor.getPluginResourcesPath("kotlin_script.svg")
    }

    override fun supports(runTypeExtension: RunTypeExtension): Boolean {
        if (TeamCityProperties.getBooleanOrTrue(DOCKER_SUPPORT_ENABLED) &&
            runTypeExtension is PositionAware &&
            DOCKER_WRAPPER == (runTypeExtension as PositionAware).orderId
        ) {
            return true
        }
        return super.supports(runTypeExtension)
    }
}