package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.ProgramCommandLine

class ContainerJavaDiscoveryService(private val javaExistsPredicate: (Boolean) -> Unit) : BuildServiceAdapter() {
    override fun makeProgramCommandLine() = object : ProgramCommandLine {
        override fun getExecutablePath() = "java"

        override fun getWorkingDirectory() = runnerContext.workingDirectory.absolutePath

        override fun getArguments() = listOf("-version")

        override fun getEnvironment() = environmentVariables
    }

    override fun beforeProcessStarted() {
        logger.message("Running `java -version` inside container to check if it is configured in system PATH...")
    }

    override fun getRunResult(exitCode: Int): BuildFinishedStatus {
        val javaExists = exitCode == 0
        javaExistsPredicate(javaExists)

        if (javaExists) {
            logger.message("Java is configured in container's PATH.")
        } else {
            logger.message("Java was not found in container's PATH.")
        }
        return BuildFinishedStatus.FINISHED_SUCCESS
    }

    override fun isCommandLineLoggingEnabled() = false
}
