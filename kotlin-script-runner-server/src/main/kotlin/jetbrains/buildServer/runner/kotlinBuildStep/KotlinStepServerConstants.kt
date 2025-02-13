

package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.tools.ToolVersionIdHelper

internal val SERVER_LOG = Loggers.SERVER

internal val KOTLIN_BUNDLED_VERSION_NUMBER = "2.1.10"
internal val KOTLIN_BUNDLED_VERSION_ID = ToolVersionIdHelper.getToolId(KotlinToolType.INSTANCE, "bundled")
internal val KOTLIN_COMPILER_PREFIX = "kotlin-compiler-"
internal val DOT_ZIP = ".zip"
internal val MIN_ZIP_NAME_LEN = KOTLIN_COMPILER_PREFIX.length + DOT_ZIP.length
internal val DEFAULT_KOTLIN_PATH_REF = "%teamcity.tool.kotlin.compiler.DEFAULT%"
internal val KOTLIN_RUNNER_DETECTABLE_EXTENSIONS = setOf("kts", "main.kts")

internal fun getToolFileName(version: String): String = KOTLIN_COMPILER_PREFIX + version + DOT_ZIP