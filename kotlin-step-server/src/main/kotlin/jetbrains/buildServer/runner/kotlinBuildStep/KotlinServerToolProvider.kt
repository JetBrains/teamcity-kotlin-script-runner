package jetbrains.buildServer.runner.kotlinBuildStep

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
import java.nio.file.Path

class KotlinServerToolProvider(val pluginDescriptor: PluginDescriptor, val archiveManager: ArchiveExtractorManager, val sslTrustStoreProvider: SSLTrustStoreProvider):
        ServerToolProviderAdapter() {

    private val myBundledVersions = hashMapOf<String, InstalledToolVersion>()
    private val myToolVersions = hashMapOf<String, KotlinDowloadableToolVersion>()

    init {
        KOTLIN_VERSIONS_SUPPORTED.forEach { myToolVersions.put(it.id, it) }
        val pluginRoot: Path = pluginDescriptor.pluginRoot.toPath()
        KOTLIN_VERSION_NUMBERS_BUNDLED.map {
            val path = pluginRoot.resolve("bundled").resolve(getToolFileName(it))
            SimpleInstalledToolVersion(
                    SimpleToolVersion(getType(), it, ToolVersionIdHelper.getToolId(KotlinToolType.INSTANCE, it)),
                    null, null, path.toFile())
        }.forEach { myBundledVersions.put(it.id, it) }
    }

    override fun getType(): ToolType = KotlinToolType.INSTANCE

    override fun getBundledToolVersions() = myBundledVersions.values

    override fun getDefaultBundledVersionId(): String?
        = if(KOTLIN_VERSION_NUMBERS_BUNDLED.isEmpty()) null
        else ToolVersionIdHelper.getToolId(KotlinToolType.INSTANCE, KOTLIN_VERSION_NUMBERS_BUNDLED.get(0))

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
            throw ToolException("Error while unpacking a Kotlin compiler distribution: $e", e)
        } catch (e: UnsupportedArchiveTypeException) {
            throw ToolException("Unsupported archive format when trying to extract a Kotlin compiler tool from [" + toolPackage.absolutePath + "]", e)
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
}