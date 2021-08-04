package jetbrains.buildServer.runner.kotlinBuildStep

import com.google.gson.Gson
import com.google.gson.JsonArray
import jetbrains.buildServer.serverSide.IOGuard
import jetbrains.buildServer.serverSide.TeamCityProperties
import jetbrains.buildServer.tools.available.AvailableToolsFetcher
import jetbrains.buildServer.tools.available.FetchAvailableToolsResult
import jetbrains.buildServer.util.FuncThrow
import jetbrains.buildServer.util.VersionComparatorUtil
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern
import javax.annotation.RegEx


class KotlinScriptAvailableToolsFetcherImpl: KotlinScriptAvailableToolsFetcher {

    val gson = Gson()
    val RELEASE_VERSION_PATTERN = Pattern.compile("v[0-9]+\\.[0-9]+\\.[0-9]+")
    val TOOL_REPOSITORY_URL = "https://api.github.com/repos/JetBrains/kotlin/releases"

    override fun fetchAvailable(): FetchAvailableToolsResult {
        val url = URL(TeamCityProperties.getProperty("teamcity.internal.runner.kotlinScript.toolsUrl", TOOL_REPOSITORY_URL))
        try {
            val json = IOGuard.allowNetworkCall<String, Exception> {
                BufferedReader(InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).readText()
            }
            val releases = gson.fromJson(json, JsonArray::class.java)
            val tools = releases
                    .filter { it.isJsonObject }
                    .map { it.asJsonObject["tag_name"] }
                    .filter { it != null }
                    .map { it.asString }
                    .filter { RELEASE_VERSION_PATTERN.matcher(it).matches() }
                    .map { it.substring(1) }  // v1.4.31 => 1.4.31
                    .filter { VersionComparatorUtil.compare(it, "1.3.70") >= 0 }
                    .map { KotlinDowloadableToolVersion(it) }
            return FetchAvailableToolsResult.createSuccessful(tools)
        } catch (ex: Exception) {
            val msg = "Failed to fetch available Kotlin compiler versions from ${url.toString()}"
            SERVER_LOG.warnAndDebugDetails(msg, ex)
            return FetchAvailableToolsResult.createError(msg, ex)
        }
    }
}
