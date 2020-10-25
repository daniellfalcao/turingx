package com.falcon.turingx.core.components

import android.content.Context
import androidx.annotation.StringRes

/** A String holder that contains a real String or a StringRes. */
class StringWrapper {

    private var messageRes: Int? = null
    private var messageStr: String? = null

    constructor(@StringRes message: Int) {
        messageRes = message
    }

    constructor(message: String) {
        messageStr = message
    }

    /** Get the internal value of the String. */
    operator fun invoke(context: Context): String {
        return messageStr ?: run { messageRes?.let { context.getString(it) } ?: run { "" } }
    }

}

/** Convert a String to StringWrapper. */
fun String.toStringWrapper() = StringWrapper(this)