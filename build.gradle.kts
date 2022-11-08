import java.text.SimpleDateFormat
import java.util.Date
import com.github.jk1.license.render.*

plugins {
    kotlin("jvm") version "1.4.21" apply false
    id ("com.github.jk1.dependency-license-report") version "1.17"
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

extra["teamcityVersion"] = project.findProperty("TeamCityVersion") ?: "2022.10-SNAPSHOT"

tasks.register<Copy>("pluginZip") {
    from("kotlin-script-runner-server/build/distributions/kotlin-script-runner.zip")
    into("build/distributions")
}

licenseReport {
    renderers = arrayOf(JsonReportRenderer("third-party-libs.json"))
}

