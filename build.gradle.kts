import java.text.SimpleDateFormat
import java.util.Date
import com.github.jk1.license.render.*

plugins {
    kotlin("jvm") version "1.4.21" apply false
    id ("com.github.jk1.dependency-license-report") version "1.17"
}

group = "org.jetbrains.teamcity"

val timestamp = SimpleDateFormat("yyMMdd_HHmm").format(Date())

val pluginVersion by extra(project.findProperty("PluginVersion") ?: "SNAPSHOT_${timestamp}")
version = pluginVersion

extra["teamcityVersion"] = project.findProperty("TeamCityVersion") ?: "2021.1"

tasks.register<Copy>("pluginZip") {
    from("kotlin-script-runner-server/build/distributions/kotlin-script-runner-$version.zip")
    into("build/distributions")
    rename("kotlin-script-runner-$version.zip", "kotlin-script-runner.zip")
}

licenseReport {
    renderers = arrayOf(JsonReportRenderer("third-party-libs.json"))
}

