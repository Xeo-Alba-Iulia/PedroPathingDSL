plugins {
    kotlin("jvm") version "2.4.20"
    `maven-publish`
}

group = "com.pedropathing"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://repo.dairy.foundation/releases/")
}

kotlin {
    jvmToolchain(8)
    compilerOptions {
        optIn.add("com.pedropathing.paths.InternalCallbacksApi")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

dependencies {
    api("com.pedropathing:core") {
        version {
            strictly("[3.0, 3.1)")
            prefer("3.0.1")
        }
    }
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "builder-dsl"
        }
    }
}