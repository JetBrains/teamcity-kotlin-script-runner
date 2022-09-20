plugins {
    kotlin("jvm")
    id ("com.github.rodm.teamcity-server") version "1.1.1"
}


group = "org.jetbrains.teamcity"

val BUNDLED_TOOL_VERSION = "1.7.10"
val pluginVersion = rootProject.extra["pluginVersion"]
version = pluginVersion

dependencies {
    agent(project(path = ":kotlin-script-runner-agent", configuration = "plugin"))
    compile(project(":kotlin-script-runner-common"))
    compile(kotlin("stdlib"))
    provided("org.jetbrains.teamcity.internal:server:${rootProject.extra["teamcityVersion"]}")
    provided("org.jetbrains.teamcity.internal:server-tools:${rootProject.extra["teamcityVersion"]}")
}

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

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn(named("includeToolDef"))
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.getByName<Test>("test") {
    useTestNG {
        suites("/src/test/testng-kotlin-script-runner-server.xml")
    }
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

teamcity {

    version = rootProject.extra["teamcityVersion"] as String

    server {
        descriptor = file("../teamcity-plugin.xml")
        tokens = mapOf("Version" to pluginVersion)

        files {
            into("bundled") {
                from("$buildDir/bundled")
            }
        }
        files {
            into("kotlin-dsl") {
                from("kotlin-dsl")
            }
        }
    }

}
