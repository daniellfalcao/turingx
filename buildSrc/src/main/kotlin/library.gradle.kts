@file:Suppress("UnstableApiUsage")

import com.jfrog.bintray.gradle.BintrayExtension

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("maven-publish")
    id("com.jfrog.bintray")
}

android {
    compileSdkVersion(LibrarySettings.targetSdk)
    buildToolsVersion(LibrarySettings.toolsVersion)
    defaultConfig {
        minSdkVersion(LibrarySettings.minimumSdk)
        targetSdkVersion(LibrarySettings.targetSdk)
        versionName = LibrarySettings.versionName
        versionCode = LibrarySettings.versionCode
        testInstrumentationRunner = LibrarySettings.Test.instrumentationRunner
        consumerProguardFiles(LibrarySettings.Proguard.consumer)
    }
    compileOptions {
        sourceCompatibility = LibrarySettings.Compile.javaVersion
        targetCompatibility = LibrarySettings.Compile.javaVersion
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = LibrarySettings.Proguard.minifyEnableRelease
            proguardFiles(getDefaultProguardFile(LibrarySettings.Proguard.android), LibrarySettings.Proguard.rules)
        }
        getByName("debug") {
            isMinifyEnabled = LibrarySettings.Proguard.minifyEnableDebug
            proguardFiles(getDefaultProguardFile(LibrarySettings.Proguard.android), LibrarySettings.Proguard.rules)
        }
    }
    buildFeatures {
        dataBinding = true
    }
    kotlinOptions {
        freeCompilerArgs = LibrarySettings.Compile.freeCompilerArgs
        jvmTarget = LibrarySettings.Compile.kotlinJvmTarget
    }
}

tasks {

    register("androidJavadocJar", Jar::class) {
        archiveClassifier.set("javadoc")
        from("$buildDir/javadoc")
    }

    register("androidSourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }
}

publishing {

    publications {

        register<MavenPublication>(LibrarySettings.Publishing.mavenPublicationName) {

            groupId = LibrarySettings.groupId
            version = LibrarySettings.versionName

            afterEvaluate { artifact(tasks.getByName("bundleReleaseAar")) }
            artifact(tasks.getByName("androidJavadocJar"))
            artifact(tasks.getByName("androidSourcesJar"))

            pom {

                withXml {

                    fun groovy.util.Node.addDependency(dependency: Dependency, scope: String) {

                        var groupId = dependency.group
                        var libVersion = dependency.version

                        if (dependency.group?.startsWith("turingx") == true) {
                            groupId = LibrarySettings.groupId
                            libVersion = LibrarySettings.versionName
                        }

                        appendNode("dependency").apply {
                            appendNode("groupId", groupId)
                            appendNode("artifactId", dependency.name)
                            appendNode("version", libVersion)
                            appendNode("scope", scope)
                        }
                    }
                }
            }
        }
    }
}

bintray {
    user = LibrarySettings.Credentials.user
    key = LibrarySettings.Credentials.key
    publish = true
    setPublications(LibrarySettings.Publishing.mavenPublicationName)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "turingx"
        name = project.name
        userOrg = "daniellfalcao"
        githubRepo = "daniellfalcao/turingx"
        vcsUrl = "https://github.com/daniellfalcao/turingx"
        setLabels("kotlin")
        setLicenses("MIT")
        setVersion(LibrarySettings.versionName)
    })
}