plugins {
    kotlin("jvm")
}

group = "org.jetbrains.teamcity"

val pluginVersion = (rootProject.extra["pluginVersion"] ?: "SNAPSHOT") as String
version = pluginVersion

repositories {
    mavenCentral()
}

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

tasks.getByName<Test>("test") {
    useTestNG {
        suites("/src/test/teamcity-pugin-kotlin-step.xml")
    }
}

