package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.tools.ToolTypeAdapter

class KotlinToolType: ToolTypeAdapter() {

    override fun getType() = "kotlin-compiler"
    override fun getDisplayName() = "Kotlin compiler"

    override fun isSupportDownload() = true

    companion object {
        val INSTANCE = KotlinToolType()
    }
}
