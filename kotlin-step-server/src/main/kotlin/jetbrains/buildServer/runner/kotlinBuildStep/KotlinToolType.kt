package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.tools.ToolTypeAdapter

class KotlinToolType: ToolTypeAdapter() {

    override fun getType(): String = "kotlin"

    override fun isSupportDownload() = true

    companion object {
        val INSTANCE = KotlinToolType()
    }
}
