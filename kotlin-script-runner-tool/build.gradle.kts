
plugins {
    id("com.github.rodm.teamcity-agent") version "1.4"
}

group = "org.jetbrains.teamcity"

version = rootProject.version

val BUNDLED_TOOL_NAME = "kotlin.compiler.bundled"
val BUNDLED_TOOL_VERSION = "1.5.0"

repositories {
    ivy {
        url = uri("https://github.com/JetBrains/")
        patternLayout {
            artifact("[organisation]/releases/download/v[revision]/[artifact]-[revision].[ext]")
        }
        metadataSources {
            artifact()
        }
    }
}

val bundled: Configuration by configurations.creating

dependencies {
    bundled (group = "kotlin", name = "kotlin-compiler", version = BUNDLED_TOOL_VERSION, ext = "zip")
}

teamcity {
    agent {
        archiveName = BUNDLED_TOOL_NAME
        descriptor = "tools/teamcity-plugin.xml"
        files {
            from(zipTree(configurations["bundled"].singleFile)) {
                includeEmptyDirs = false
                eachFile {
                    path = path.split(Regex.fromLiteral("/"), 2)[1]
                }
            }
        }
    }
}

artifacts {
    add("default", tasks.named("agentPlugin"))
}
