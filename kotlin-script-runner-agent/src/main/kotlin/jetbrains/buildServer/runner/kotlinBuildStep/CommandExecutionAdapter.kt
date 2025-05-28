package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.CommandExecution
import java.io.File

class CommandExecutionAdapter(private val _buildService: BuildServiceAdapter) : CommandExecution {
    var result: BuildFinishedStatus? = null
        private set

    private val processListeners by lazy { _buildService.listeners }

    override fun makeProgramCommandLine() = _buildService.makeProgramCommandLine()

    override fun beforeProcessStarted() = _buildService.beforeProcessStarted()

    override fun interruptRequested() = _buildService.interrupt()

    override fun isCommandLineLoggingEnabled() = _buildService.isCommandLineLoggingEnabled

    override fun onStandardOutput(text: String) = processListeners.forEach { it.onStandardOutput(text) }

    override fun onErrorOutput(text: String) = processListeners.forEach { it.onErrorOutput(text) }

    override fun processStarted(programCommandLine: String, workingDirectory: File) =
        processListeners.forEach { it.processStarted(programCommandLine, workingDirectory) }

    override fun processFinished(exitCode: Int) {
        processListeners.forEach { it.processFinished(exitCode) }

        _buildService.afterProcessFinished()

        result = _buildService.getRunResult(exitCode)
        if (result == BuildFinishedStatus.FINISHED_SUCCESS) {
            _buildService.afterProcessSuccessfullyFinished()
        }
    }
}