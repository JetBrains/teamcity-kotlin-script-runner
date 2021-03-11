package jetbrains.buildServer.runner.kotlinBuildStep


import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.ToolCannotBeFoundException
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

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val scriptFile = getOrCreateScript()
        return createCommandLine(scriptFile)
    }

    protected fun createCommandLine(script: String): ProgramCommandLine {
        val lib = File(getToolPath(), LIB_DIR)
        return JavaCommandLineBuilder()
                .withJavaHome(getRunnerParameters().get(JavaRunnerConstants.TARGET_JDK_HOME), getRunnerContext().isVirtualContext())
                .withBaseDir(getCheckoutDirectory().getAbsolutePath())
                .withEnvVariables(getEnvironmentVariables())
                .withJvmArgs(JavaRunnerUtil.extractJvmArgs(getRunnerParameters()))
                .withClassPath(getClasspath(lib))
                .withMainClass("org.jetbrains.kotlin.preloading.Preloader")
                .withProgramArgs(getProgramArgs(script, lib))
                .withWorkingDir(getWorkingDirectory().getAbsolutePath())
                .build()
    }

    private fun getToolPath(): String {
        return runnerParameters[Constants.PARAM_KOTLIN_PATH]
                ?: throw ToolCannotBeFoundException("Kotlin compiler tool path is missing in the runner settings")
    }

    private fun getProgramArgs(script: String, lib: File): List<String> {
        val scriptArgs = listOf("-cp", File(lib, "kotlin-compiler.jar").canonicalPath, "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler", "-script", script)
        val ktsArgs = getRunnerParameters().get(PARAM_KTS_ARGS)
        if (ktsArgs == null || ktsArgs.isEmpty())
            return scriptArgs
        else
            return scriptArgs + CommandLineArgumentsUtil.extractArguments(ktsArgs)
    }

    private fun getClasspath(lib: File): String {
        return File(lib, "kotlin-preloader.jar").canonicalPath
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