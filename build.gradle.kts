import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint.strictly

plugins {
    kotlin("jvm") version "2.4.20"
    id("com.android.library") version "9.0.1" apply false
    `maven-publish`
}

group = "com.pedropathing"
version = "2.0.1"

allprojects.forEach {
    it.repositories {
        mavenCentral()
        google()
        maven("https://repo.dairy.foundation/releases/")
    }
}

subprojects.forEach {
    it.version = rootProject.version
    it.group = rootProject.group
}

kotlin {
    jvmToolchain(8)
    compilerOptions {
        optIn.add("com.pedropathing.callbacks.InternalCallbacksApi")
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

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "builder-dsl"
        }
    }
}