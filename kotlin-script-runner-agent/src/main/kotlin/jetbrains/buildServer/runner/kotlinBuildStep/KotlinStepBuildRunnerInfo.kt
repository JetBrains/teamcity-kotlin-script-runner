

package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration

class KotlinStepBuildRunnerInfo : AgentBuildRunnerInfo {

    override fun getType(): String {
        return KOTLIN_RUNNER_TYPE
    }

    override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean {
        return true
    }

}