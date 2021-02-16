plugins {
    kotlin("jvm") version "1.4.21" apply false
    id ("com.github.rodm.teamcity-server") version "1.2" apply false
}

group = "org.jetbrains.teamcity"

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://download.jetbrains.com/teamcity-repository")
    }
}

extra["pluginVersion"] = "${if (project.hasProperty("PluginVersion")) project.property("PluginVersion") else "SNAPSHOT"}"
version = (extra["pluginVersion"] ?: "SNAPSHOT") as String

extra["teamcityVersion"] = "2020.2"

tasks.register<Copy>("pluginZip") {
    from("teamcity-kotlin-step-server/build/distributions/teamcity-kotlin-step-server-$version.zip")
    into("build/distributions")
    rename("teamcity-kotlin-step-server-$version.zip", "kotlin-step.zip")
}

