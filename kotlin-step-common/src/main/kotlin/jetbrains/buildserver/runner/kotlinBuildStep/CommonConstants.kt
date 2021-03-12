package jetbrains.buildServer.runner.kotlinBuildStep

const val KOTLIN_RUNNER_TYPE = "kotlinScript"
const val KOTLIN_COMPILER_TOOL_TYPE = "kotlin-compiler"
const val KOTLIN_COMPILER_TOOL_DISPLAY_NAME = "Kotlin compiler"

object RunnerParamNames {
    const val SCRIPT_TYPE = "scriptType"
    const val SCRIPT_CONTENT = "script.content"
    const val SCRIPT_FILE = "script.file"
    const val KOTLIN_PATH = "kotlin.path"
}

object ScriptTypes {
    const val CUSTOM = "customScript"
    const val FILE = "file"
}