plugins {
    kotlin("jvm") version "2.4.20"
    `maven-publish`
}

group = "com.pedropathing"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.dairy.foundation/releases/")
}

dependencies {
    api("com.pedropathing:core") {
        version {
            strictly("[3.0, 3.1)")
            prefer("3.0.1")
        }
    }
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