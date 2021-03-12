package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.tools.ToolTypeAdapter

class KotlinToolType: ToolTypeAdapter() {

    override fun getType() = KOTLIN_COMPILER_TOOL_TYPE
    override fun getDisplayName() = KOTLIN_COMPILER_TOOL_DISPLAY_NAME

    override fun isSupportDownload() = true

    companion object {
        val INSTANCE = KotlinToolType()
    }
}
