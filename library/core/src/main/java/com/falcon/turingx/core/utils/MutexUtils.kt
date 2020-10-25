package com.falcon.turingx.core.utils

import kotlinx.coroutines.sync.Mutex

inline fun Mutex.doOnLock(owner: Any? = null, action: () -> Unit){
    if (tryLock()) {
        try {
            return action()
        } finally {
            safeUnlock()
        }
    }
}

fun Mutex.safeUnlock(owner: Any? = null) {
    try {
        unlock(owner)
    } catch (e: Exception) {
    }
}
