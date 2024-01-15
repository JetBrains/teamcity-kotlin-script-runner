

package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.tools.ToolTypeAdapter

class KotlinToolType: ToolTypeAdapter() {

    override fun getType() = KOTLIN_COMPILER_TOOL_TYPE
    override fun getDisplayName() = KOTLIN_COMPILER_TOOL_DISPLAY_NAME
    override fun getDescription() = KOTLIN_COMPILER_TOOL_DESCRIPTION

    override fun isSupportDownload() = true

    override fun getValidPackageDescription() =
         """
            <p>
            The latest release version of Kotlin compiler can be 
            <a href="https://github.com/JetBrains/kotlin/releases/latest" target="_blank" rel="noreferrer">downloaded from GitHub</a>. 
            Use files with a name <b style="white-space: nowrap;">kotlin-compiler-&lt;version&gt;.zip</b>.
            </p>
            <p>
            For other versions, please follow the "Release on GitHub" links <a href="https://kotlinlang.org/docs/releases.html#release-details" target="_blank" rel="noreferrer">here</a>.
            </p>
        """.trimIndent()


    companion object {
        val INSTANCE = KotlinToolType()
    }
}