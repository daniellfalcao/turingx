package com.falcon.turingx.lifecycle.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**  Used as a wrapper for data that is exposed via a LiveData that represents an event. */
abstract class Event<T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    /** A Event that holders a data value to be dispatched. */
    class Data<T>(content: T) : Event<T>(content)

    /** A Event that represents a callback of some action. */
    class Callback : Event<Any>("CALLBACK") {
        fun MutableLiveData<Callback>.call() = apply { notifyDataChanged() }
    }
}

/**
 * Extension to observe Data events, simplifying the pattern of checking if the [Event]'s content
 * has already been handled.
 * */
@JvmName("observeDataEvent")
fun <T> LiveData<Event.Data<T>>.observeEvent(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, { event -> event.getContentIfNotHandled()?.let { observer(it) } })
}

/**
 * Extension to observe Callback events, simplifying the pattern of checking if the [Event]'s
 * content has already been handled.
 * */
@JvmName("observeCallbackEvent")
fun LiveData<Event.Callback>.observeEvent(owner: LifecycleOwner, observer: () -> Unit) {
    observe(owner, { event -> event.getContentIfNotHandled()?.let { observer() } })
}
