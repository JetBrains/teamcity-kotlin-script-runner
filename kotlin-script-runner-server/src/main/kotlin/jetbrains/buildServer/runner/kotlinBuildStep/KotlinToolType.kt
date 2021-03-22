/*
 * Copyright 2000-2021 JetBrains s.r.o.
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
            <a href="https://github.com/JetBrains/kotlin/releases/latest">downloaded from GitHub</a>. 
            Use files with a name <b style="white-space: nowrap;">kotlin-compiler-&lt;version&gt;.zip.</b>
            </p>
            <p>
            For other versions, please follow the "Release on GitHub" links <a href="https://kotlinlang.org/docs/releases.html#release-details" target="_blank">here</a>
            </p>
        """.trimIndent()


    companion object {
        val INSTANCE = KotlinToolType()
    }
}
