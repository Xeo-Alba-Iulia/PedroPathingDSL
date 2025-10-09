plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

group = "com.pedropathing"
version = "0.2.2-2.0.2"

repositories {
    mavenCentral()
    maven("https://maven.pedropathing.com/")
}

dependencies {
    api("com.pedropathing:core:2.0.2")
    testImplementation(kotlin("test-junit5"))
    testImplementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "builder-dsl"
            version = project.version.toString()
        }
    }
}

kotlin {
    jvmToolchain(8)
}