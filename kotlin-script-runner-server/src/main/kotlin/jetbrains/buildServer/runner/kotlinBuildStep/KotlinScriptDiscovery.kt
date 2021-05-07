package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.serverSide.BuildTypeSettings
import jetbrains.buildServer.serverSide.discovery.BreadthFirstRunnerDiscoveryExtension
import jetbrains.buildServer.serverSide.discovery.DiscoveredObject
import jetbrains.buildServer.util.CollectionsUtil
import jetbrains.buildServer.util.browser.Browser
import jetbrains.buildServer.util.browser.Element
import jetbrains.buildServer.util.positioning.PositionAware
import jetbrains.buildServer.util.positioning.PositionConstraint

class KotlinScriptDiscovery: BreadthFirstRunnerDiscoveryExtension(), PositionAware {
    override fun discoverRunnersInDirectory(dir: Element, filesAndDirs: MutableList<Element>): MutableList<DiscoveredObject> {
        val result = mutableListOf<DiscoveredObject>()
        if (!dir.fullName.toLowerCase().startsWith(".teamcity")) {
            for (element in filesAndDirs) {
                if (skipElement(element)) continue
                result.add(DiscoveredObject(KOTLIN_RUNNER_TYPE, mapOf(
                        RunnerParamNames.SCRIPT_TYPE to ScriptTypes.FILE,
                        RunnerParamNames.SCRIPT_FILE to element.fullName,
                        RunnerParamNames.KOTLIN_PATH to DEFAULT_KOTLIN_PATH_REF
                )))
            }
        }
        return result
    }

    override protected fun postProcessDiscoveredObjects(settings: BuildTypeSettings, browser: Browser, discovered: List<DiscoveredObject?>): List<DiscoveredObject?> {
        val existingScripts = settings.buildRunners
                .filter { it.type.equals(KOTLIN_RUNNER_TYPE) && ScriptTypes.FILE.equals(it.parameters[RunnerParamNames.SCRIPT_TYPE]) }
                .mapNotNull { it.parameters[RunnerParamNames.SCRIPT_FILE] }
                .map { it.replace("\\", "/") }.toSet()

        return discovered.filter {
            it != null && !existingScripts.contains(it.parameters[RunnerParamNames.SCRIPT_FILE]?.replace("\\", "/"))
        }
    }

    private fun skipElement(element: Element): Boolean {
        if (!element.isLeaf)
            return true
        val extension = element.name.toLowerCase().substringAfter(".")
        return !KOTLIN_RUNNER_DETECTABLE_EXTENSIONS.contains(extension)
    }

    override fun getConstraint() = PositionConstraint.last()

    override fun getOrderId() = KOTLIN_RUNNER_TYPE
}