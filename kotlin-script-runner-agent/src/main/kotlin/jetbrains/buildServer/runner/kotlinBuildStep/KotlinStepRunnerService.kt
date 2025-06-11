package jetbrains.buildServer.runner.kotlinBuildStep


import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.ToolCannotBeFoundException
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.JavaCommandLineBuilder
import jetbrains.buildServer.agent.runner.JavaRunnerUtil
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine
import jetbrains.buildServer.runner.CommandLineArgumentsUtil
import jetbrains.buildServer.runner.JavaRunnerConstants
import jetbrains.buildServer.util.FileUtil
import jetbrains.buildServer.util.OSType
import java.io.File

class KotlinStepRunnerService : BuildServiceAdapter() {

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val scriptFile = getOrCreateScript()
        return createCommandLine(scriptFile)
    }

    private fun createCommandLine(script: String): ProgramCommandLine {
        val lib = File(getToolPath(), LIB_DIR)
        val cmd = JavaCommandLineBuilder()
            .withJavaHome(
                runnerParameters[JavaRunnerConstants.TARGET_JDK_HOME],
                runnerContext.isVirtualContext
            )
            .withBaseDir(checkoutDirectory.absolutePath)
            .withEnvVariables(environmentVariables)
            .withJvmArgs(
                listOf("-Dkotlin.main.kts.compiled.scripts.cache.dir=") + JavaRunnerUtil.extractJvmArgs(
                    runnerParameters
                )
            )
            .withClassPath(getClasspath(lib))
            .withMainClass("org.jetbrains.kotlin.preloading.Preloader")
            .withProgramArgs(getProgramArgs(script, lib))
            .withWorkingDir(workingDirectory.absolutePath)
            .build()

        if (runnerContext.isVirtualContext) {
            return getVirtualContextCommandLine(cmd)
        }

        return cmd
    }

    private fun getToolPath(): String {
        return runnerParameters[RunnerParamNames.KOTLIN_PATH]
            ?: throw ToolCannotBeFoundException("Kotlin compiler tool path is missing in the runner settings")
    }

    private fun getProgramArgs(script: String, lib: File): List<String> {
        val scriptArgs = listOf(
            "-cp",
            File(lib, "kotlin-compiler.jar").canonicalPath,
            "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler",
            "-script",
            script
        )
        val ktsArgs = runnerParameters[RunnerParamNames.KOTLIN_ARGS]
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
            if (scriptContent.isBlank())
                throw IllegalArgumentException("No script provided")
            val scriptFile = File.createTempFile("script", ".main.kts", getAgentTempDirectory())
            FileUtil.writeFile(scriptFile, scriptContent, "UTF-8");
            scriptFile
        }
        return scriptFile.absolutePath;
    }

    private fun getScriptContent(): String {
        return runnerContext.runnerParameters[RunnerParamNames.SCRIPT_CONTENT]
            ?: throw RunBuildException("Kotlin script content is not specified")
    }

    override fun getListeners() = super.getListeners() + JavaNotFoundProcessListener(runnerContext, logger)

    // JavaCommandLineBuilder doesn't respect OS of container
    // if linux container is run inside windows it tries to execute java.exe
    private fun getVirtualContextCommandLine(commandLine: ProgramCommandLine): ProgramCommandLine {
        val executable = when (runnerContext.virtualContext.targetOSType) {
            OSType.WINDOWS -> "java.exe"
            else -> "java"
        }
        return SimpleProgramCommandLine(
            commandLine.environment,
            commandLine.workingDirectory,
            executable,
            commandLine.arguments
        )
    }
}