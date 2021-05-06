package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.serverSide.discovery.BreadthFirstRunnerDiscoveryExtension
import jetbrains.buildServer.serverSide.discovery.DiscoveredObject
import jetbrains.buildServer.util.CollectionsUtil
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

    private fun skipElement(element: Element): Boolean {
        if (!element.isLeaf)
            return true
        val name = element.name.toLowerCase()
        return !name.endsWith(".kts") || name.endsWith(".module.kts") || name.endsWith(".gradle.kts")
    }

    override fun getConstraint() = PositionConstraint.last()

    override fun getOrderId() = KOTLIN_RUNNER_TYPE
}