plugins {
    kotlin("jvm") version "2.3.20"
    `maven-publish`
}

group = "com.pedropathing"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pedropathing.com/")
}

dependencies {
    api("com.pedropathing:core:2.1.1")
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(kotlin("reflect"))
    testImplementation("io.mockk:mockk:1.14.9")
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