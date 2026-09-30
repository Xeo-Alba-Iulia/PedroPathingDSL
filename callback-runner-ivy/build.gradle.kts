plugins {
    id("com.android.library")
}

kotlin {
    jvmToolchain(8)
    compilerOptions {
        optIn.add("com.pedropathing.callbacks.InternalCallbacksApi")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

android {
    namespace = "com.pedropathing.callbacks"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    compileOnly(project(":"))
    compileOnly("org.firstinspires.ftc:RobotCore:12.0.0")
    compileOnly("org.firstinspires.ftc:FtcCommon:12.0.0")
    implementation("com.pedropathing.ivy:core:1.1.1")
}