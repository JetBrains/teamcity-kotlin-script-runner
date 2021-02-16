package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration

class KotlinStepBuildRunnerInfo : AgentBuildRunnerInfo {

    override fun getType(): String {
        return KotlinStepConstants.TYPE
    }

    override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean {
        return true
    }

}