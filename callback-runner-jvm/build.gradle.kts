plugins {
    kotlin("jvm")
    `maven-publish`
}

version = project(":").version
group = project(":").group

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        optIn.add("com.pedropathing.callbacks.InternalCallbacksApi")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

configurations {
    apiElements {
        outgoing {
            capability("com.pedropathing:callback-runner-jvm:${version}")
            capability("com.pedropathing:callback-runner:${version}")
        }
    }
    runtimeElements {
        outgoing {
            capability("com.pedropathing:callback-runner-jvm:${version}")
            capability("com.pedropathing:callback-runner:${version}")
        }
    }
}

dependencies {
    compileOnly(project(":"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("CallbackRunnerJvm") {
            from(components["java"])
            artifactId = "callback-runner-jvm"
        }
    }
}