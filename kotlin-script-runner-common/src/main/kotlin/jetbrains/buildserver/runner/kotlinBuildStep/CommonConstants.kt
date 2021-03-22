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

const val KOTLIN_RUNNER_TYPE = "kotlinScript"
const val KOTLIN_COMPILER_TOOL_TYPE = "kotlin.compiler"
const val KOTLIN_COMPILER_TOOL_DISPLAY_NAME = "Kotlin compiler"
const val KOTLIN_COMPILER_TOOL_DESCRIPTION = "Is used in Kotlin Script build steps."

object RunnerParamNames {
    const val SCRIPT_TYPE = "scriptType"
    const val SCRIPT_CONTENT = "scriptContent"
    const val SCRIPT_FILE = "scriptFile"
    const val KOTLIN_PATH = "kotlinPath"
    const val KOTLIN_ARGS = "kotlinArgs"
}

object ScriptTypes {
    const val CUSTOM = "customScript"
    const val FILE = "file"
}