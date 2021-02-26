plugins {
    kotlin("jvm")
    id ("com.github.rodm.teamcity-server")
    id ("com.github.rodm.teamcity-environments")
}

group = "org.jetbrains.teamcity"

val pluginVersion = (rootProject.extra["pluginVersion"] ?: "SNAPSHOT") as String
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":kotlin-step-common"))
    agent (project(path = ":kotlin-step-agent", configuration = "plugin"))
    implementation(kotlin("stdlib-jdk8"))
    provided("org.jetbrains.teamcity.internal:server:${rootProject.extra["teamcityVersion"]}")
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

teamcity {
    version = rootProject.extra["teamcityVersion"] as String

    server {
        descriptor = file("../teamcity-plugin.xml")
        tokens = mapOf("Version" to pluginVersion)
/*
        files {
            into("kotlin-dsl") {
                from("kotlin-dsl")
            }
        }

 */
    }

}
