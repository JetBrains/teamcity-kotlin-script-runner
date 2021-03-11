package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.serverSide.ServerResponsibility
import jetbrains.buildServer.tools.ServerToolPreProcessorAdapter
import jetbrains.buildServer.tools.installed.ToolPaths
import jetbrains.buildServer.util.FileUtil
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.jetbrains.annotations.NotNull
import java.nio.file.Path

class KotlinToolPreProcessor(val pluginDescriptor: PluginDescriptor, val toolPaths: ToolPaths,
                             val serverResponsibility: ServerResponsibility): ServerToolPreProcessorAdapter() {
    override fun getName() = KotlinToolType.INSTANCE.type

    override fun doBeforeServerStartup() {

        if (!serverResponsibility.canWriteToConfigDirectory())
            return

        for (version in KOTLIN_VERSION_NUMBERS_BUNDLED) {
            val fileName = getToolFileName(version)
            val destination = toolPaths.getSharedToolPath(fileName)
            if (destination.exists())
                continue
            val pluginRoot: Path = pluginDescriptor.pluginRoot.toPath()
            val source = pluginRoot.resolve("bundled").resolve(fileName).toFile()
            if (!source.exists()) {
                SERVER_LOG.warn("Missing bundled tool at ${source.canonicalPath}")
                continue
            }
            FileUtil.copy(source, destination)
        }
    }

}