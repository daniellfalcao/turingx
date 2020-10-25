@file:Suppress("MemberVisibilityCanBePrivate")

package com.falcon.turingx.lifecycle.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * SingleMediatorLiveData its a [MediatorLiveData] where keeps only one liveData instance, calling
 * [emit].
 * */
class SingleMediatorLiveData<T> : MediatorLiveData<T>() {

    /** the actual observed liveData */
    private var lastLiveData: LiveData<*>? = null

    /** Add a source to a MediatorLiveData excluding the last liveData added. */
    override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<in S>) {
        lastLiveData?.let { removeSource(it) }
        lastLiveData = source
        super.addSource(source, onChanged)
    }

    /** Call [emit] to add a liveData to MediatorLiveData. */
    fun emit(source: LiveData<T>) {
        addSource(source) { value = it }
    }

}