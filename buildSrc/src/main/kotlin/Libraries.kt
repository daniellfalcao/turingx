@file:Suppress("unused", "MemberVisibilityCanBePrivate")

import org.gradle.kotlin.dsl.DependencyHandlerScope

object Libraries {

    fun DependencyHandlerScope.implementation(library: String) {
        this.add("implementation", library)
    }

    fun DependencyHandlerScope.debugImplementation(library: String) {
        this.add("debugImplementation", library)
    }

    object Common {

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            // libraries
            Kotlin(dependencyHandler)
            Coroutines(dependencyHandler)
            Koin(dependencyHandler)
            Firebase(dependencyHandler)
            Utils.Stheto(dependencyHandler)
            Utils.Timber(dependencyHandler)
            Utils.LeakCanary(dependencyHandler)
        }
    }

    object UI {
        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            // libraries
            Androidx(dependencyHandler)
            MaterialComponents(dependencyHandler)
            Lifecycle(dependencyHandler)
        }
    }

    object Kotlin {

        internal const val version = "1.4.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(stdlib)
        }
    }

    object Coroutines {

        internal const val version = "1.3.9"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(core)
            dependencyHandler.implementation(android)
        }
    }

    object Androidx {

        const val core = "androidx.core:core-ktx:1.3.1"
        const val activity = "androidx.activity:activity-ktx:1.1.0"
        const val fragment = "androidx.fragment:fragment-ktx:1.2.5"
        const val collection = "androidx.collection:collection-ktx:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.1"
        const val annotation = "androidx.annotation:annotation:1.1.0"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
        const val cardView = "androidx.cardview:cardview:1.0.0"
        const val viewPager = "androidx.viewpager2:viewpager2:1.0.0"
        const val transitions = "androidx.transition:transition-ktx:1.4.0-beta01"
        const val swipe = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(core)
            dependencyHandler.implementation(activity)
            dependencyHandler.implementation(fragment)
            dependencyHandler.implementation(collection)
            dependencyHandler.implementation(constraintLayout)
            dependencyHandler.implementation(annotation)
            dependencyHandler.implementation(recyclerView)
            dependencyHandler.implementation(cardView)
            dependencyHandler.implementation(viewPager)
            dependencyHandler.implementation(transitions)
            dependencyHandler.implementation(swipe)
        }
    }

    object MaterialComponents {

        internal const val version = "1.2.1"
        const val material = "com.google.android.material:material:$version"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(material)
        }
    }

    object Lifecycle {

        internal const val version = "2.2.0"
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
        const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(runtime)
            dependencyHandler.implementation(livedata)
            dependencyHandler.implementation(viewModel)
            dependencyHandler.implementation(extensions)
        }
    }

    object Koin {

        internal const val version = "2.0.0"
        const val android = "org.koin:koin-android:$version"
        const val scope = "org.koin:koin-androidx-scope:$version"
        const val viewModel = "org.koin:koin-androidx-viewmodel:$version"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(android)
            dependencyHandler.implementation(scope)
            dependencyHandler.implementation(viewModel)
        }
    }

    object Firebase {

        const val ktx = "com.google.firebase:firebase-common-ktx:19.3.1"
        const val core = "com.google.firebase:firebase-core:17.5.0"
        const val messaging = "com.google.firebase:firebase-messaging:20.2.4"
        const val performance = "com.google.firebase:firebase-perf:19.0.8"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(ktx)
            dependencyHandler.implementation(core)
            dependencyHandler.implementation(messaging)
            dependencyHandler.implementation(performance)
        }
    }

    object Location {

        const val location = "com.google.android.gms:play-services-location:17.0.0"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(location)
        }
    }

    object Maps {

        const val maps = "com.google.android.gms:play-services-maps:17.0.0"
        const val utils = "com.google.maps.android:android-maps-utils:2.0.3"

        operator fun invoke(dependencyHandler: DependencyHandlerScope) {
            dependencyHandler.implementation(maps)
            dependencyHandler.implementation(utils)
        }
    }

    object Utils {

        object Stheto {

            const val stetho = "com.facebook.stetho:stetho:1.5.1"
            const val okhttp = "com.facebook.stetho:stetho-okhttp3:1.5.1"

            operator fun invoke(dependencyHandler: DependencyHandlerScope) {
                dependencyHandler.implementation(stetho)
                dependencyHandler.implementation(okhttp)
            }
        }

        object Timber {

            const val timber = "com.jakewharton.timber:timber:4.7.1"

            operator fun invoke(dependencyHandler: DependencyHandlerScope) {
                dependencyHandler.implementation(timber)
            }
        }

        object LeakCanary {

            const val leak = "com.squareup.leakcanary:leakcanary-android:2.4"

            operator fun invoke(dependencyHandler: DependencyHandlerScope) {
                dependencyHandler.debugImplementation(leak)
            }
        }
    }

}