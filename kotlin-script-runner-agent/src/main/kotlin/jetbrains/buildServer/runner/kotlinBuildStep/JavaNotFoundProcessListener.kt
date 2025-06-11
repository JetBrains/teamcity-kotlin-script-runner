package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.ProcessListenerAdapter
import jetbrains.buildServer.util.OSType

class JavaNotFoundProcessListener(
    private val runnerContext: BuildRunnerContext,
    private val logger: BuildProgressLogger
) : ProcessListenerAdapter() {

    override fun processFinished(exitCode: Int) {
        runnerContext.virtualContext.let {
            if (it.isVirtual && it.targetOSType == OSType.UNIX && exitCode == COMMAND_NOT_FOUND_UNIX_CODE) {
                logger.error(
                    """
                        Kotlin Script step aborted — failed to locate a suitable JDK in the docker image.
                        
                        How to fix:
                        - Switch to an image that already contains a JDK
                        Examples: eclipse-temurin:17-jdk, openjdk:21-jdk, or any distro image that includes a JDK.
                        - Enable the ‘Run in Docker’ build feature in the build settings, and add a preceding step to install the JDK in the container.
                        Once JDK is present on PATH, the Kotlin Script runner will continue normally.
                    """.trimIndent()
                )
            }
        }
    }

    companion object {
        const val COMMAND_NOT_FOUND_UNIX_CODE = 127
    }
}