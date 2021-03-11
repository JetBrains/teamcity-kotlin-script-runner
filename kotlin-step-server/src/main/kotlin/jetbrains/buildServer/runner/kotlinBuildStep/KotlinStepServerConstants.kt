package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.log.Loggers

internal val SERVER_LOG = Loggers.SERVER

internal val KOTLIN_VERSIONS_SUPPORTED = listOf("1.3.72", "1.4.31").map { KotlinDowloadableToolVersion(it) }
internal val KOTLIN_VERSION_NUMBERS_BUNDLED = listOf("1.4.31")
internal val KOTLIN_COMPILER_PREFIX = "kotlin-compiler-"
internal val DOT_ZIP = ".zip"
internal val MIN_ZIP_NAME_LEN = KOTLIN_COMPILER_PREFIX.length + DOT_ZIP.length

internal fun getToolFileName(version: String): String = KOTLIN_COMPILER_PREFIX + version + DOT_ZIP

