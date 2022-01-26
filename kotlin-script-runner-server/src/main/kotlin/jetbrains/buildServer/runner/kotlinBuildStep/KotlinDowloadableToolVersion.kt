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