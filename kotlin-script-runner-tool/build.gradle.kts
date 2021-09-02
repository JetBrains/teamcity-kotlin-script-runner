
plugins {
    id("com.github.rodm.teamcity-agent") version "1.4"
}

group = "org.jetbrains.teamcity"

version = rootProject.version

val BUNDLED_TOOL_VERSION = "1.5.0"

tasks {
    register<DownloadKotlinTask>("downloadBundled") {
        toolVersion = BUNDLED_TOOL_VERSION
        outputDir.set(File("$buildDir/bundled-download"))
    }

    agentPlugin {
        dependsOn(named("downloadBundled"))
    }
}

teamcity {
    agent {
        archiveName = "kotlin.compiler.bundled.zip"
        descriptor = "tools/teamcity-plugin.xml"
        files {
            from(zipTree("$buildDir/bundled-download/kotlin-compiler-$BUNDLED_TOOL_VERSION.zip")) {
                includeEmptyDirs = false
                eachFile {
                    path = path.split(Regex.fromLiteral("/"), 2)[1]
                }
            }
        }
    }
}

artifacts {
    add("default", tasks.named("agentPlugin"))
}

abstract class DownloadKotlinTask : DefaultTask() {
    @get:Input
    abstract var toolVersion: String

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun download() {
        val destFile = outputDir.file("kotlin-compiler-$toolVersion.zip").get().asFile
        if (!destFile.exists()) {
            val url = "https://github.com/JetBrains/kotlin/releases/download/v$toolVersion/kotlin-compiler-$toolVersion.zip"
            ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
        }
    }
}
