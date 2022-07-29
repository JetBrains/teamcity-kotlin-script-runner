plugins {
    kotlin("jvm")
    id ("com.github.rodm.teamcity-server") version "1.4"
}

group = "org.jetbrains.teamcity"

version = rootProject.version

val bundled: Configuration by configurations.creating

dependencies {
    agent(project(path = ":kotlin-script-runner-agent", configuration = "plugin"))
    bundled (project(":kotlin-script-runner-tool"))
    implementation(project(":kotlin-script-runner-common"))
    implementation(kotlin("stdlib"))
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
    test {
        useTestNG {
            suites("/src/test/testng-kotlin-script-runner-server.xml")
        }
    }
}

teamcity {

    version = rootProject.extra["teamcityVersion"] as String

    server {
        archiveName = "${rootProject.name}-${rootProject.version}"
        descriptor = file("../teamcity-plugin.xml")
        tokens = mapOf("Version" to project.version)

        files {
            into("bundled") {
                from(bundled)
            }
        }
        files {
            into("kotlin-dsl") {
                from("kotlin-dsl")
            }
        }
    }
}
