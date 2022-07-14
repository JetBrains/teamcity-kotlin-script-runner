/*
 * Copyright 2000-2022 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
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
    }
}