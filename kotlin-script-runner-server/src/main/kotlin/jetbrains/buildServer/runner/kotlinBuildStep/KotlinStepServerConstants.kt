/*
 * Copyright 2000-2022 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.tools.ToolVersionIdHelper

internal val SERVER_LOG = Loggers.SERVER

internal val KOTLIN_BUNDLED_VERSION_NUMBER = "1.7.10"
internal val KOTLIN_BUNDLED_VERSION_ID = ToolVersionIdHelper.getToolId(KotlinToolType.INSTANCE, "bundled")
internal val KOTLIN_COMPILER_PREFIX = "kotlin-compiler-"
internal val DOT_ZIP = ".zip"
internal val MIN_ZIP_NAME_LEN = KOTLIN_COMPILER_PREFIX.length + DOT_ZIP.length
internal val DEFAULT_KOTLIN_PATH_REF = "%teamcity.tool.kotlin.compiler.DEFAULT%"
internal val KOTLIN_RUNNER_DETECTABLE_EXTENSIONS = setOf("kts", "main.kts")

internal fun getToolFileName(version: String): String = KOTLIN_COMPILER_PREFIX + version + DOT_ZIP

