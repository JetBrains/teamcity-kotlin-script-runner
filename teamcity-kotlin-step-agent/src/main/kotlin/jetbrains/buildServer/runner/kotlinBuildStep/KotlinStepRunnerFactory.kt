package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory

class KotlinStepRunnerFactory : CommandLineBuildServiceFactory, AgentBuildRunnerInfo {
    override fun getType(): String {
        return KotlinStepConstants.TYPE
    }

    override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean {
        return true
    }

    override fun createService(): CommandLineBuildService {
        return KotlinStepRunnerService()
    }

    override fun getBuildRunnerInfo(): AgentBuildRunnerInfo {
        return this
    }
}