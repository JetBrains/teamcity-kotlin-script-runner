import java.text.SimpleDateFormat
import java.util.Date
import com.github.jk1.license.render.*
import java.util.*
import java.io.FileInputStream
import java.nio.file.Path
import java.nio.file.Paths

plugins {
    kotlin("jvm") version "1.4.21" apply false
    id ("com.github.jk1.dependency-license-report") version "1.17"
}

initializeWorkspace()

group = "org.jetbrains.teamcity"

val localRepo = anyParamPath("TC_LOCAL_REPO")

allprojects {
    repositories {
        mavenLocal()
        if (localRepo != null) {
            maven(url = "file:///${localRepo}")
        }
        mavenCentral()
        maven(url = "https://download.jetbrains.com/teamcity-repository")
        maven(url = "https://repo.labs.intellij.net/teamcity")
    }
}

val timestamp = SimpleDateFormat("yyMMdd_HHmm").format(Date())

extra["pluginVersion"] = "${if (project.hasProperty("PluginVersion")) project.property("PluginVersion") else "SNAPSHOT_${timestamp}"}"
version = extra["pluginVersion"]

extra["teamcityVersion"] = anyParam("teamcityVersion") ?: "2022.10-SNAPSHOT"

tasks.register<Copy>("pluginZip") {
    from("kotlin-script-runner-server/build/distributions/kotlin-script-runner.zip")
    into("build/distributions")
}

licenseReport {
    renderers = arrayOf(JsonReportRenderer("third-party-libs.json"))
}


fun anyParamPath(vararg names: String): Path? {
    val param = anyParam(*names)
    if (param == null || param.isEmpty())
        return null
    return if (Paths.get(param).isAbsolute()) {
        Paths.get(param)
    } else {
        getRootDir().toPath().resolve(param)
    }
}

fun anyParam(vararg names: String): String? {
    var param: String? = ""
    try {
        for(name in names) {
            param = if (project.hasProperty(name)) {
                project.property(name).toString()
            } else {
                System.getProperty(name) ?: System.getenv(name) ?: null
            }
            if (param != null)
                break;
        }
        if (param == null || param.isEmpty())
            param = null
    } finally {
        println("AnyParam: ${names.joinToString(separator = ",")} -> $param")
    }
    return param
}


fun initializeWorkspace() {
    if (System.getProperty("idea.active") != null) {
        println("Attempt to configure workspace in IDEA")
        val coreVersionProperties = project.projectDir.toPath().parent.parent.resolve(".version.properties")
        if (coreVersionProperties.toFile().exists()) {
            val p = Properties().also {
                it.load(FileInputStream(coreVersionProperties.toFile()))
            }
            p.forEach {(k,v) ->
                System.setProperty(k.toString(), v.toString());
            }
        }
    }
}