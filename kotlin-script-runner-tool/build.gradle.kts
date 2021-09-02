
plugins {
    id ("base")
}

group = "org.jetbrains.teamcity"

version = rootProject.version

val BUNDLED_TOOL_VERSION = "1.5.0"

tasks {
    register<DownloadKotlinTask>("downloadBundled") {
        toolVersion = BUNDLED_TOOL_VERSION
        outputDir.set(File("$buildDir/bundled-download"))
    }

    register<Zip>("includeToolDef") {
        archiveFileName.set("kotlin.compiler.bundled.zip")
        destinationDirectory.set(file("$buildDir/bundled"))

        from(zipTree("$buildDir/bundled-download/kotlin-compiler-$BUNDLED_TOOL_VERSION.zip")) {
            include("kotlinc/**")
            eachFile {
                relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
            }
            includeEmptyDirs = false
        }
        from("tools/teamcity-plugin.xml")
        dependsOn(named("downloadBundled"))
    }
}

artifacts {
    add("default", tasks.named("includeToolDef"))
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
