package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession
import jetbrains.buildServer.agent.runner.MultiCommandBuildSessionFactory

class KotlinStepRunnerFactory() : MultiCommandBuildSessionFactory {
    override fun createSession(runnerContext: BuildRunnerContext) = object : MultiCommandBuildSession {
        private var commandsIterator: Iterator<CommandExecution> = emptySequence<CommandExecution>().iterator()

        private val kotlinStepRunnerService = KotlinStepRunnerService()
        private val kotlinStepExecutionAdapter = CommandExecutionAdapter(kotlinStepRunnerService)

        override fun sessionStarted() {
            kotlinStepRunnerService.initialize(runnerContext.build, runnerContext)
            commandsIterator =
                sequence {
                    if (runnerContext.isVirtualContext) {
                        println("checking java....")
                        //yieldAll(_dockerJavaExecutableProvider.getCommandExecutionSequence())
                    }
                    yield(kotlinStepExecutionAdapter)
                }.iterator()
        }

        override fun getNextCommand(): CommandExecution? {
            if (commandsIterator.hasNext()) {
                return commandsIterator.next()
            }

            return null
        }

        override fun sessionFinished(): BuildFinishedStatus? = kotlinStepExecutionAdapter.result

    }

    override fun getBuildRunnerInfo() = object : AgentBuildRunnerInfo {
        override fun getType(): String = KOTLIN_RUNNER_TYPE

        override fun canRun(config: BuildAgentConfiguration): Boolean = true
    }
}