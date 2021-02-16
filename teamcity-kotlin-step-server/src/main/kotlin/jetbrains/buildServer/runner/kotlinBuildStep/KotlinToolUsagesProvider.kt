package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.serverSide.BuildPromotionEx
import jetbrains.buildServer.serverSide.SRunningBuild
import jetbrains.buildServer.tools.InstalledToolVersion
import jetbrains.buildServer.tools.ToolUsagesProvider
import jetbrains.buildServer.tools.ToolVersion

class KotlinToolUsagesProvider(val serverToolProvider: KotlinServerToolProvider): ToolUsagesProvider {

    override fun getRequiredTools(build: SRunningBuild): List<ToolVersion?> {
        val bp = build.buildPromotion as? BuildPromotionEx ?: return emptyList()
        for (runnerDescriptor in bp.buildSettings.allBuildRunners.enabledBuildRunners) {
            if (runnerDescriptor.runType.type == KotlinToolType.INSTANCE.type)
                return ArrayList(serverToolProvider.bundledToolVersions)
        }
        return emptyList()
    }
}