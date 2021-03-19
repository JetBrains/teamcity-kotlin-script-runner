plugins {
    kotlin("jvm")
}

group = "org.jetbrains.teamcity"

version = rootProject.extra["pluginVersion"]

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

