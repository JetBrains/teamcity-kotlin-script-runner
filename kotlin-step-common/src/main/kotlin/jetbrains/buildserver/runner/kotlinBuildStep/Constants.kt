package jetbrains.buildServer.runner.kotlinBuildStep

object Constants {

    val RUNNER_TYPE = "kotlinScript"

    val TOOL_TYPE = "kotlin-compiler"
    val TOOL_DISPLAY_NAME = "Kotlin compiler"

    val PARAM_SCRIPT_TYPE = "scriptType"
    val PARAM_SCRIPT_CONTENT = "script.content"
    val PARAM_SCRIPT_FILE = "script.file"

    val SCRIPT_TYPE_CUSTOM = "customScript"
    val SCRIPT_TYPE_FILE = "file"
}