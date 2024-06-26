plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
}

group = "org.jetbrains.teamcity"
version = "SNAPSHOT_240625_1540"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.8.21")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:1.8.21")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:1.8.21")
    api("org.jetbrains.kotlin:kotlin-scripting-dependencies:1.8.21")
    api("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven:1.8.21")
    api("org.example:service-messages-prototype:1.0")
    implementation(kotlin("stdlib-jdk8"))
}

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