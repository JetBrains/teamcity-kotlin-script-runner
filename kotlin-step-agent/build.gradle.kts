plugins {
    kotlin("jvm")
    id ("com.github.rodm.teamcity-agent")
}

group = "org.jetbrains.teamcity"

val pluginVersion = (rootProject.extra["pluginVersion"] ?: "SNAPSHOT") as String
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":kotlin-step-common"))
    implementation(kotlin("stdlib-jdk8"))
    provided("org.jetbrains.teamcity:agent-api:${rootProject.extra["teamcityVersion"]}")
    provided("org.jetbrains.teamcity.internal:agent:${rootProject.extra["teamcityVersion"]}")
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

    agent {
        descriptor {
            pluginDeployment {
                useSeparateClassloader = true
            }
        }
    }

}
