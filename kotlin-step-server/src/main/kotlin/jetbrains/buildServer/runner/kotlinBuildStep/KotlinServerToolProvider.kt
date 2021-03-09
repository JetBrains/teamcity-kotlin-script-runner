package jetbrains.buildServer.runner.kotlinBuildStep

import jetbrains.buildServer.DevelopmentMode
import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.plugins.files.JarSearcherBase
import jetbrains.buildServer.tools.*
import jetbrains.buildServer.tools.utils.URLDownloader
import jetbrains.buildServer.util.ArchiveExtractorManager
import jetbrains.buildServer.util.ArchiveFileSelector
import jetbrains.buildServer.util.FileUtil
import jetbrains.buildServer.util.UnsupportedArchiveTypeException
import jetbrains.buildServer.util.ssl.SSLTrustStoreProvider
import jetbrains.buildServer.web.openapi.PluginDescriptor
import java.io.File
import java.io.IOException

class KotlinServerToolProvider(val pluginDescriptor: PluginDescriptor, val archiveManager: ArchiveExtractorManager, val sslTrustStoreProvider: SSLTrustStoreProvider):
        ServerToolProviderAdapter() {

    private val myBundledVersions = hashMapOf<String, InstalledToolVersion>()
    private val myToolVersions = hashMapOf<String, KotlinDowloadableToolVersion>()

    init {
        KOTLIN_VERSIONS_SUPPORTED.forEach { myToolVersions.put(it.id, it) }
    }

    override fun getType(): ToolType = KotlinToolType.INSTANCE

    override fun getBundledToolVersions() = emptyList<InstalledToolVersion>()

    override fun getDefaultBundledVersionId(): String? = null

    override fun getAvailableToolVersions() = myToolVersions.values

    override fun tryGetPackageVersion(toolPackage: File): GetPackageVersionResult {
        val zipName = toolPackage.name
        if (!(zipName.startsWith(KOTLIN_COMPILER_PREFIX) && zipName.endsWith(DOT_ZIP) && zipName.length > MIN_ZIP_NAME_LEN))
            return GetPackageVersionResult.error("Failed to determine Kotlin version. Make sure the ${zipName} file is a valid Kotlin archive")

        val versionNumber = zipName.substring(KOTLIN_COMPILER_PREFIX.length, zipName.length - DOT_ZIP.length)
        val toolId = ToolVersionIdHelper.getToolId(KotlinToolType.INSTANCE, versionNumber)
        val toolVersion = myToolVersions.get(toolId)

        return if (toolVersion == null)
            GetPackageVersionResult.error("Failed to determine Kotlin version for tool id ${toolId}")
        else
            GetPackageVersionResult.version(toolVersion)
    }

    @Throws(ToolException::class)
    override fun fetchToolPackage(toolVersion: ToolVersion, targetDirectory: File): File {
        val dowloadableVersion = myToolVersions.get(toolVersion.id)
        if (dowloadableVersion == null)
            throw ToolException("Tool version ${toolVersion.id} not found")
        val location = File(targetDirectory, dowloadableVersion.getDestinationFileName())
        try {
            URLDownloader.download(dowloadableVersion.getDownloadUrl(), sslTrustStoreProvider.getTrustStore(), location)
        } catch (e: Throwable) {
            throw ToolException("Failed to download package " + toolVersion + " to " + location + e.message, e)
        }
        return location
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
        final val KOTLIN_VERSIONS_SUPPORTED = listOf("1.3.72", "1.4.31").map { KotlinDowloadableToolVersion(it) }
        final val KOTLIN_COMPILER_PREFIX = "kotlin-compiler-"
        final val DOT_ZIP = ".zip"
        final val MIN_ZIP_NAME_LEN = KOTLIN_COMPILER_PREFIX.length + DOT_ZIP.length
    }
}