

package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.tools.SimpleToolVersion
import jetbrains.buildServer.tools.ToolVersionIdHelper
import jetbrains.buildServer.tools.available.DownloadableToolVersion

class KotlinDowloadableToolVersion(val versionNumber: String): DownloadableToolVersion,
        SimpleToolVersion(KotlinToolType.INSTANCE, versionNumber,
                ToolVersionIdHelper.getToolId(KotlinToolType.INSTANCE, versionNumber),
                "${KOTLIN_COMPILER_TOOL_DISPLAY_NAME} $versionNumber"
        ) {

    override fun getDownloadUrl() = "https://github.com/JetBrains/kotlin/releases/download/v${version}/kotlin-compiler-${version}.zip"

    override fun getDestinationFileName() = "kotlin-compiler-${version}.zip"
}