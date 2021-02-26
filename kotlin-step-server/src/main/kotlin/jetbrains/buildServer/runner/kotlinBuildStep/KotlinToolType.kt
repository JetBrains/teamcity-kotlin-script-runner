package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.tools.ToolTypeAdapter

class KotlinToolType: ToolTypeAdapter() {
    override fun getType(): String = "kotlin"

    companion object {
        val INSTANCE = KotlinToolType()
    }
}
