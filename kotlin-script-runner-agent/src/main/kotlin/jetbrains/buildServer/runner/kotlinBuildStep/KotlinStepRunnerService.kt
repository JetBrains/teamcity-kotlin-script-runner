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
        return runnerParameters[RunnerParamNames.KOTLIN_PATH]
                ?: throw ToolCannotBeFoundException("Kotlin compiler tool path is missing in the runner settings")
    }

    private fun getProgramArgs(script: String, lib: File): List<String> {
        val scriptArgs = listOf("-cp", File(lib, "kotlin-compiler.jar").canonicalPath, "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler", "-script", script)
        val ktsArgs = getRunnerParameters()[RunnerParamNames.KTS_ARGS]
        if (ktsArgs == null || ktsArgs.isEmpty())
            return scriptArgs
        else
            return scriptArgs + CommandLineArgumentsUtil.extractArguments(ktsArgs)
    }

    private fun getClasspath(lib: File): String {
        return File(lib, "kotlin-preloader.jar").canonicalPath
    }


    private fun getOrCreateScript(): String {
        val scriptType = runnerParameters[RunnerParamNames.SCRIPT_TYPE]
        val scriptFile = if (scriptType.equals(ScriptTypes.FILE)) {
            val scriptFileName = runnerParameters[RunnerParamNames.SCRIPT_FILE]
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
        return getRunnerContext().getRunnerParameters().get(RunnerParamNames.SCRIPT_CONTENT)
                ?: throw RunBuildException("Kotlin script content is not specified")
    }
}