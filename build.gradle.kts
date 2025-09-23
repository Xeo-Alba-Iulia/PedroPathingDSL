plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

group = "com.pedropathing"
version = "1.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pedropathing.com/")
}

dependencies {
    api("com.pedropathing:core:1.1.0-SNAPSHOT")
    testImplementation(kotlin("test-junit5"))
    testImplementation(kotlin("reflect"))
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