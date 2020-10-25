object Plugins {

    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
    const val kotlinAllOpen = "kotlin-allopen"
    const val mavenPublish = "maven-publish"
    const val firebasePerformance = "com.google.firebase.firebase-perf"
    const val googleServices = "com.google.gms.google-services"
    const val navigationSafeArgs = "androidx.navigation.safeargs.kotlin"
    const val commonLibrary = "library"

    object Gradle {
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Libraries.Kotlin.version}"
        const val kotlinAllOpenPlugin = "org.jetbrains.kotlin:kotlin-allopen:${Libraries.Kotlin.version}"
        const val googleServicesGradlePlugin = "com.google.gms:google-services:4.3.3"
        const val firebasePerformanceGradlePlugin = "com.google.firebase:perf-plugin:1.3.1"
    }
}