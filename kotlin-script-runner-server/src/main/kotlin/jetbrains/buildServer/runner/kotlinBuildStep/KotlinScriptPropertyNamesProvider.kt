package jetbrains.buildServer.runner.kotlinBuildStep

class KotlinScriptPropertyNamesProvider {
    val scriptType = RunnerParamNames.SCRIPT_TYPE
    val scriptContent = RunnerParamNames.SCRIPT_CONTENT
    val scriptFile = RunnerParamNames.SCRIPT_FILE
    val ktsArgs = RunnerParamNames.KTS_ARGS
    val kotlinPath = RunnerParamNames.KOTLIN_PATH

    val typeFile = ScriptTypes.FILE
    val typeCustom = ScriptTypes.CUSTOM
}