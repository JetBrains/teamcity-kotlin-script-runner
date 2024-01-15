

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