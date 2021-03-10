plugins {
    kotlin("jvm")
    id ("com.github.rodm.teamcity-server") version "1.1.1"
}


group = "org.jetbrains.teamcity"

val pluginVersion = rootProject.extra["pluginVersion"]
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    agent(project(path = ":kotlin-step-agent", configuration = "plugin"))
    compile(project(":kotlin-step-common"))
    compile(kotlin("stdlib"))
    provided("org.jetbrains.teamcity.internal:server:${rootProject.extra["teamcityVersion"]}")
    provided("org.jetbrains.teamcity.internal:server-tools:${rootProject.extra["teamcityVersion"]}")
}

tasks {
    register<DownloadKotlinTask>("downloadBundled") {
        version = "1.4.31"
        outputDir.set(File("$buildDir/bundled"))
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn(named("downloadBundled"))
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.getByName<Test>("test") {
    useTestNG {
        suites("/src/test/testng-kotlin-step-server.xml")
    }
}

abstract class DownloadKotlinTask : DefaultTask() {
    @get:Input
    abstract var version: String

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun download() {
        val destFile = outputDir.file("kotlin-compiler-${version}.zip").get().asFile
        if (!destFile.exists()) {
            val url = "https://github.com/JetBrains/kotlin/releases/download/v${version}/kotlin-compiler-${version}.zip"
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
/*
        files {
            into("kotlin-dsl") {
                from("kotlin-dsl")
            }
        }

 */
    }

}
