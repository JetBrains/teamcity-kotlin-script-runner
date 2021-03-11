package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.agent.*
import jetbrains.buildServer.runner.kotlinBuildStep.Constants.TOOL_TYPE
import java.io.File

class KotlinToolProvider(toolProvidersRegistry: ToolProvidersRegistry, agentConfiguration: BuildAgentConfiguration) {

    init {
        toolProvidersRegistry.registerToolProvider(object : ToolProvider {

            override fun supports(toolName: String): Boolean {
                return TOOL_TYPE == toolName
            }

            @Throws(ToolCannotBeFoundException::class)
            override fun getPath(toolName: String): String {
                throw ToolCannotBeFoundException("No default Kotlin tool defined")
            }

            @Throws(ToolCannotBeFoundException::class)
            override fun getPath(toolName: String,
                                 build: AgentRunningBuild,
                                 runner: BuildRunnerContext): String {
                return getKotlinPath(runner)
            }
        })
    }


    @Throws(ToolCannotBeFoundException::class)
    private fun getKotlinPath(runner:BuildRunnerContext): String {
        val runnerParameters: Map<String, String> = runner.getRunnerParameters()
        return runnerParameters["kotlin.path"] ?: throw ToolCannotBeFoundException("No Kotlin tool path provided")
    }
}
