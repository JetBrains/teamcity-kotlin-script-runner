import java.text.SimpleDateFormat
import java.util.Date

plugins {
    kotlin("jvm") version "1.4.21" apply false
}

group = "org.jetbrains.teamcity"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://download.jetbrains.com/teamcity-repository")
        maven(url = "https://repo.labs.intellij.net/teamcity")
    }
}

val timestamp = SimpleDateFormat("yyMMdd_HHmm").format(Date())

extra["pluginVersion"] = "${if (project.hasProperty("PluginVersion")) project.property("PluginVersion") else "SNAPSHOT_${timestamp}"}"
version = extra["pluginVersion"]

extra["teamcityVersion"] = project.findProperty("teamcityVersion") ?: "2021.1-SNAPSHOT"

tasks.register<Copy>("pluginZip") {
    from("kotlin-script-runner-server/build/distributions/kotlin-script-runner-$version.zip")
    into("build/distributions")
    rename("kotlin-script-runner-$version.zip", "kotlin-script-runner.zip")
}

