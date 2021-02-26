package jetbrains.buildServer.runner.kotlinBuildStep


import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.JavaCommandLineBuilder
import jetbrains.buildServer.agent.runner.JavaRunnerUtil
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.runner.CommandLineArgumentsUtil
import jetbrains.buildServer.runner.JavaRunnerConstants
import jetbrains.buildServer.util.FileUtil
import jetbrains.buildServer.util.StringUtil
import java.io.File

class KotlinStepRunnerService: BuildServiceAdapter() {
    companion object {
        const val LIB_DIR = "lib"
        const val PARAM_KTS_ARGS = "ktsArgs"
        final val LIB_JARS = listOf("kotlin-compiler.jar", "kotlin-reflect.jar", "kotlin-script-runtime.jar", "kotlin-stdlib.jar")
    }

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val scriptFile = getOrCreateScript()
        return createCommandLine(scriptFile)
    }

    protected fun createCommandLine(script: String): ProgramCommandLine {
        return JavaCommandLineBuilder()
                .withJavaHome(getRunnerParameters().get(JavaRunnerConstants.TARGET_JDK_HOME), getRunnerContext().isVirtualContext())
                .withBaseDir(getCheckoutDirectory().getAbsolutePath())
                .withEnvVariables(getEnvironmentVariables())
                .withJvmArgs(JavaRunnerUtil.extractJvmArgs(getRunnerParameters()))
                .withClassPath(getClasspath())
                .withMainClass("org.jetbrains.kotlin.cli.jvm.K2JVMCompiler")
                .withProgramArgs(getProgramArgs(script))
                .withWorkingDir(getWorkingDirectory().getAbsolutePath())
                .build()
    }

    private fun getProgramArgs(script: String): List<String> {
        val scriptArgs = listOf("-script", script)
        val ktsArgs = getRunnerParameters().get(PARAM_KTS_ARGS)
        if (ktsArgs == null || ktsArgs.isEmpty())
            return scriptArgs
        else
            return scriptArgs + CommandLineArgumentsUtil.extractArguments(ktsArgs)
    }

    private fun getClasspath(): String {
        val lib = File(getToolPath(KotlinToolProvider.TOOL_NAME), LIB_DIR)
        return lib.listFiles()?.map { FileUtil.getCanonicalFile(it).path }?.joinToString(File.pathSeparator) ?: ""
    }


    private fun getOrCreateScript(): String {
        val scriptType = runnerParameters[Constants.PARAM_SCRIPT_TYPE]
        val scriptFile = if (scriptType.equals(Constants.SCRIPT_TYPE_FILE)) {
            val scriptFileName = runnerParameters[Constants.PARAM_SCRIPT_FILE]
            if (scriptFileName == null || scriptFileName.isEmpty())
                throw IllegalArgumentException("No script file name provided")
            File(checkoutDirectory, scriptFileName)
        } else {
            val scriptContent = getScriptContent()
            if (StringUtil.isEmpty(scriptContent))
                throw IllegalArgumentException("No script provided")
            val scriptFile = File.createTempFile("script", ".main.kts", getAgentTempDirectory())
            FileUtil.writeFile(scriptFile, scriptContent, "UTF-8");
            scriptFile
        }
        return scriptFile.getAbsolutePath();
    }

    protected fun getScriptContent(): String {
        return getRunnerContext().getRunnerParameters().get(Constants.PARAM_SCRIPT_CONTENT)
                ?: throw RunBuildException("Kotlin script content is not specified")
    }
}