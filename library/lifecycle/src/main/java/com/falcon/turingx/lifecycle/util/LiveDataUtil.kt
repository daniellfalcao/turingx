package com.falcon.turingx.lifecycle.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Tells observers that the data has changed. */
fun <T> MutableLiveData<T>.notifyDataChanged() {
    this.value = value
}

/** Transform a MutableLiveData in a read only liveData. */
fun <T> MutableLiveData<T>.readOnly() = this as LiveData<T>

/**
 * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds.
 * Once we got a notification via onChanged, we stop observing.
 * */
@Throws(InterruptedException::class)
fun <T> LiveData<T>.blockingGetValue(): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    observeForever { o ->
        data[0] = o
        latch.countDown()
    }
    latch.await(2, TimeUnit.SECONDS)
    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}