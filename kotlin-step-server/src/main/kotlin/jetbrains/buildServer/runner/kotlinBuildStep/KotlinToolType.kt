package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.tools.ToolTypeAdapter

class KotlinToolType: ToolTypeAdapter() {

    override fun getType() = Constants.TOOL_TYPE
    override fun getDisplayName() = Constants.TOOL_DISPLAY_NAME

    override fun isSupportDownload() = true

    companion object {
        val INSTANCE = KotlinToolType()
    }
}
