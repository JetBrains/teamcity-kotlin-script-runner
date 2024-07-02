import java.net.URI

plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
}

group = "org.jetbrains.teamcity"
version = "SNAPSHOT_240625_1540"

repositories {
    mavenCentral()
    maven {
        url = URI("https://packages.jetbrains.team/maven/p/tc/maven")
        name = "space"
        credentials {
            username = project.property("publicationUsername") as String
            password = project.property("publicationPassword") as String
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-scripting-common")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-jvm")
    compileOnly("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    api("org.jetbrains.kotlin:kotlin-stdlib:1.5.1")
    implementation("org.jetbrains.kotlin:kotlin-main-kts:1.6.0")
    api("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    api("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")
    api("org.jetbrains.teamcity:kotlin-service-messages:0.0.1")
    compileOnly(kotlin("stdlib-jdk8"))
}
//
tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    manifest {
        attributes["Main-Class"] = "org.jetbrains.teamcity.HostRunnerKt"
    }
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            version = "1.0"
            this.groupId = "org.example"
            this.artifactId = "teamcity-step-script-definition"
        }
    }
}