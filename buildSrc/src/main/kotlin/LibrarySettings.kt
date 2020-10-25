import org.gradle.api.JavaVersion

object LibrarySettings {

    const val minimumSdk = 21
    const val targetSdk = 30
    const val toolsVersion = "30.0.2"

    const val groupId = "com.falcon.turingx"
    const val versionName = "1.0.0"
    const val versionCode = 1

    object Publishing {
        const val mavenPublicationName = "release"
    }

    object Credentials {
        const val user = "daniellfalcao"
        const val key = ""
    }

    object Proguard {
        const val android = "proguard-android-optimize.txt"
        const val consumer = "consumer-rules.pro"
        const val rules = "proguard-rules.pro"
        const val minifyEnableRelease = false
        const val minifyEnableDebug = false
    }

    object Test {
        const val instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    object Compile {

        val javaVersion = JavaVersion.VERSION_1_8

        val freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlin.contracts.ExperimentalContracts",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
        const val kotlinJvmTarget = "1.8"
    }
}