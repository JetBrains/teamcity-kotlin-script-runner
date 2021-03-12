/*
 * Copyright 2000-2021 JetBrains s.r.o.
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