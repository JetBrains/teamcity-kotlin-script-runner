package jetbrains.buildServer.runner.kotlinBuildStep


import com.intellij.util.PathUtil
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.ToolCannotBeFoundException
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.JavaCommandLineBuilder
import jetbrains.buildServer.agent.runner.JavaRunnerUtil
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.runner.CommandLineArgumentsUtil
import jetbrains.buildServer.runner.JavaRunnerConstants
import jetbrains.buildServer.util.FileUtil
import org.jetbrains.teamcity.TeamCityScriptDefinition
import java.io.File
import java.util.*

class KotlinStepRunnerService : BuildServiceAdapter() {

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val scriptFile = getOrCreateScript()
        return createCommandLine(scriptFile)
    }

    protected fun createCommandLine(script: String): ProgramCommandLine {
        val lib = File(getToolPath(), LIB_DIR)
        return JavaCommandLineBuilder()
            .withJavaHome(
                getRunnerParameters().get(JavaRunnerConstants.TARGET_JDK_HOME),
                getRunnerContext().isVirtualContext()
            )
            .withBaseDir(getCheckoutDirectory().getAbsolutePath())
            .withEnvVariables(getEnvironmentVariables())
            .withJvmArgs(
                Arrays.asList("-Dkotlin.main.kts.compiled.scripts.cache.dir=") + JavaRunnerUtil.extractJvmArgs(
                    getRunnerParameters()
                )
            )
            .withClassPath(getClasspath(lib))
            .withMainClass("org.jetbrains.kotlin.preloading.Preloader")
            .withProgramArgs(getProgramArgs(script, lib))
            .withWorkingDir(getWorkingDirectory().getAbsolutePath())
            .build()
    }

    private fun getToolPath(): String {
        return runnerParameters[RunnerParamNames.KOTLIN_PATH]
            ?: throw ToolCannotBeFoundException("Kotlin compiler tool path is missing in the runner settings")
    }

    private fun getProgramArgs(script: String, lib: File): List<String> {
        val scriptArgs = mutableListOf(
            "-cp",
            File(lib, "kotlin-compiler.jar").canonicalPath,
            "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler",
            "-script",
            script
        )
        if (script.endsWith("teamcity.buildstep.kts")) {
            scriptArgs.addAll(
                listOf(
                    "-cp",
                    "${File(PathUtil.getJarPathForClass(TeamCityScriptDefinition::class.java)).canonicalPath}:" +
                            File(lib, "kotlin-main-kts.jar").canonicalPath,
                    "-script-templates",
                    TeamCityScriptDefinition::class.java.canonicalName
                )
            )
        }
        val ktsArgs = getRunnerParameters()[RunnerParamNames.KOTLIN_ARGS]
        return if (ktsArgs.isNullOrBlank()) scriptArgs
        else if (ktsArgs.startsWith("-- ")) scriptArgs + CommandLineArgumentsUtil.extractArguments(ktsArgs)
        else scriptArgs + CommandLineArgumentsUtil.extractArguments("-- " + ktsArgs)
    }

    private fun getClasspath(lib: File): String {
        return File(lib, "kotlin-preloader.jar").canonicalPath
    }


    private fun getOrCreateScript(): String {
        val scriptType = runnerParameters[RunnerParamNames.SCRIPT_TYPE]
        val scriptFile = if (scriptType.equals(ScriptTypes.FILE)) {
            val scriptFileName = runnerParameters[RunnerParamNames.SCRIPT_FILE]
            if (scriptFileName.isNullOrBlank())
                throw IllegalArgumentException("No script file name provided")
            File(checkoutDirectory, scriptFileName)
        } else {
            val scriptContent = getScriptContent()
            if (scriptContent.isNullOrBlank())
                throw IllegalArgumentException("No script provided")
            val scriptFile = File.createTempFile("script", ".main.kts", getAgentTempDirectory())
            FileUtil.writeFile(scriptFile, scriptContent, "UTF-8");
            scriptFile
        }
        return scriptFile.getAbsolutePath();
    }

    protected fun getScriptContent(): String {
        return getRunnerContext().getRunnerParameters().get(RunnerParamNames.SCRIPT_CONTENT)
            ?: throw RunBuildException("Kotlin script content is not specified")
    }
}