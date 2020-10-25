plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinAndroidExtensions)
    id(Plugins.kotlinKapt)
    id(Plugins.mavenPublish)
}

android {
    compileSdkVersion(LibrarySettings.targetSdk)
    buildToolsVersion(LibrarySettings.toolsVersion)
    defaultConfig {
        applicationId = "com.falcon.turingx.sample"
        minSdkVersion(LibrarySettings.minimumSdk)
        targetSdkVersion(LibrarySettings.targetSdk)
        versionName = LibrarySettings.versionName
        versionCode = LibrarySettings.versionCode
        testInstrumentationRunner = LibrarySettings.Test.instrumentationRunner
    }
    compileOptions {
        sourceCompatibility = LibrarySettings.Compile.javaVersion
        targetCompatibility = LibrarySettings.Compile.javaVersion
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = LibrarySettings.Proguard.minifyEnableRelease
            proguardFiles(
                getDefaultProguardFile(LibrarySettings.Proguard.android),
                LibrarySettings.Proguard.rules
            )
        }
        getByName("debug") {
            isMinifyEnabled = LibrarySettings.Proguard.minifyEnableDebug
            proguardFiles(
                getDefaultProguardFile(LibrarySettings.Proguard.android),
                LibrarySettings.Proguard.rules
            )
        }
    }

    kotlinOptions {
        freeCompilerArgs = LibrarySettings.Compile.freeCompilerArgs
        jvmTarget = LibrarySettings.Compile.kotlinJvmTarget
    }

    @Suppress("UnstableApiUsage")
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(project(":library:core"))
    implementation(project(":library:widget"))
    implementation(project(":library:location"))
    implementation(project(":library:controller"))

    Libraries.Common(this)
    Libraries.UI(this)
    Libraries.Location(this)
}
