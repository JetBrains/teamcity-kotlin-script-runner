plugins {
    kotlin("jvm") version "1.4.21" apply false
}

group = "org.jetbrains.teamcity"

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://download.jetbrains.com/teamcity-repository")
        maven(url = "https://repo.labs.intellij.net/teamcity")
    }
}

extra["pluginVersion"] = "${if (project.hasProperty("PluginVersion")) project.property("PluginVersion") else "SNAPSHOT"}"
version = extra["pluginVersion"]

extra["teamcityVersion"] = project.findProperty("teamcityVersion") ?: "2021.1-SNAPSHOT"

tasks.register<Copy>("pluginZip") {
    from("kotlin-step-server/build/distributions/kotlin-step-$version.zip")
    into("build/distributions")
    rename("kotlin-step-$version.zip", "kotlin-step.zip")
}

