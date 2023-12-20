plugins {
    kotlin("jvm")
    id("com.github.rodm.teamcity-agent") version "1.5.2"
}

group = "org.jetbrains.teamcity"

version = rootProject.extra["pluginVersion"]

dependencies {
    compile(project(":kotlin-script-runner-common"))
    provided("org.jetbrains.teamcity:agent-api:${rootProject.extra["teamcityVersion"]}")
    provided("org.jetbrains.teamcity.internal:agent:${rootProject.extra["teamcityVersion"]}")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.testng:testng:6.8")
    testImplementation("org.assertj:assertj-core:2.2.0")
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
        suites("/src/test/testng-kotlin-script-runner-agent.xml")
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
