plugins {
    kotlin("jvm")
    id ("com.github.rodm.teamcity-server") version "1.1.1"
}


group = "org.jetbrains.teamcity"

val pluginVersion = (rootProject.extra["pluginVersion"] ?: "SNAPSHOT") as String
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
