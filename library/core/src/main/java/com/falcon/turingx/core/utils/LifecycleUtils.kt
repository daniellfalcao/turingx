package com.falcon.turingx.core.utils

import androidx.lifecycle.Lifecycle

/**
 * Utils to get check current lifecycle state.
 * */
fun Lifecycle.State.isAtLeastResumed() = isAtLeast(Lifecycle.State.RESUMED)
fun Lifecycle.State.isAtLeastDestroyed() = isAtLeast(Lifecycle.State.DESTROYED)
fun Lifecycle.State.isAtLeastStarted() = isAtLeast(Lifecycle.State.STARTED)
fun Lifecycle.State.isAtLeastInitialized() = isAtLeast(Lifecycle.State.INITIALIZED)
fun Lifecycle.State.isAtLeastCreated() = isAtLeast(Lifecycle.State.CREATED)