package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.DevelopmentMode
import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.plugins.files.JarSearcherBase
import jetbrains.buildServer.tools.*
import jetbrains.buildServer.util.ArchiveExtractorManager
import jetbrains.buildServer.util.ArchiveFileSelector
import jetbrains.buildServer.util.FileUtil
import jetbrains.buildServer.util.UnsupportedArchiveTypeException
import jetbrains.buildServer.web.openapi.PluginDescriptor
import java.io.File
import java.io.IOException

class KotlinServerToolProvider(val pluginDescriptor: PluginDescriptor, val archiveManager: ArchiveExtractorManager): ServerToolProviderAdapter() {

    private val myBundledVersions = hashMapOf<String, InstalledToolVersion>()
    private val myToolVersions = hashMapOf<String, ToolVersion>()

    init {
        val bundledToolsLocation: File = File(pluginDescriptor.getPluginRoot(), "tools")
        registerToolVersion(KOTLIN_1_3_72, File(bundledToolsLocation, KOTLIN_1_3_72.id + ".zip"))
        registerToolVersion(KOTLIN_1_4_21, File(bundledToolsLocation, KOTLIN_1_4_21.id + ".zip"))
    }

    private fun registerToolVersion(toolVersion: ToolVersion, packedAgentTool: File) {
        myToolVersions.put(toolVersion.id, toolVersion)
        if (packedAgentTool.isFile || DevelopmentMode.isEnabled) {
            myBundledVersions.put(toolVersion.id, SimpleInstalledToolVersion.newBundledToAgentTool(toolVersion, packedAgentTool))
        } else {
            Loggers.SERVER.warn("Bundled agent tool " + toolVersion.displayName + " package not found on path " + packedAgentTool.absolutePath)
        }
    }

    override fun getType(): ToolType = KotlinToolType.INSTANCE

    override fun getBundledToolVersions() = myBundledVersions.values

    override fun getDefaultBundledVersionId() = KOTLIN_1_4_21.id

    override fun getAvailableToolVersions() = myToolVersions.values

    override fun tryGetPackageVersion(toolPackage: File): GetPackageVersionResult {
        var toolId: String? = null
        try {
            archiveManager.extractFiles(toolPackage, ArchiveFileSelector {
                if (toolId == null) {
                    val top = if (it.contains("/")) { it.substring(0, it.indexOf("/")) } else { it }
                    if (top.startsWith(KOTLIN_PREFIX)) {
                        toolId = top
                    }
                }
                null
            })
        } catch (e: Exception) {
            Loggers.SERVER.warnAndDebugDetails("Error while trying to get package version of Kotlin distribution [" + toolPackage.absolutePath + "]", e)
        }

        return if (toolId != null) {
            val toolVersion = myToolVersions.get(toolId!!)
            if (toolVersion == null) GetPackageVersionResult.error("Failed to determine Kotlin version for tool id ${toolId}")
            else GetPackageVersionResult.version(toolVersion)
        } else {
            GetPackageVersionResult.error("Failed to determine Kotlin version. Make sure file is a valid Kotlin archive")
        }
    }


    @Throws(ToolException::class)
    override fun unpackToolPackage(toolPackage: File, targetDirectory: File) {
        try {
            archiveManager.extractFiles(toolPackage, ArchiveFileSelector {
                File(targetDirectory, it.substring(it.indexOf("/") + 1))
            })
            writeToolDescriptor(File(targetDirectory, JarSearcherBase.TEAMCITY_PLUGIN_XML))
        } catch (e: IOException) {
            throw ToolException("Error while unpacking the Maven distribution: $e", e)
        } catch (e: UnsupportedArchiveTypeException) {
            throw ToolException("Unsupported archive format when trying to extract Maven tool from [" + toolPackage.absolutePath + "]", e)
        }
    }

    private fun writeToolDescriptor(file: File) {
        FileUtil.writeFile(file,
                """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <teamcity-agent-plugin xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                           xsi:noNamespaceSchemaLocation="urn:shemas-jetbrains-com:teamcity-agent-plugin-v1-xml">
                      <tool-deployment />
                    </teamcity-agent-plugin>
                """.trimIndent(), "UTF-8")
    }


    companion object {
        val KOTLIN_1_3_72: ToolVersion = SimpleToolVersion(KotlinToolType.INSTANCE, "1.3.72", "kotlin_1_3_72")
        val KOTLIN_1_4_21: ToolVersion = SimpleToolVersion(KotlinToolType.INSTANCE, "1.4.21", "kotlin_1_4_21")

        val KOTLIN_PREFIX = "kotlin_"
    }
}