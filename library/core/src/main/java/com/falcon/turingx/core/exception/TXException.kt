package com.falcon.turingx.core.exception

import com.falcon.turingx.core.components.StringWrapper
import com.falcon.turingx.core.components.toStringWrapper

/** A base exception with message StringWrapper. */
abstract class TXException : Exception {

    constructor() : super()
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace)

    open var errorMessage: StringWrapper = "".toStringWrapper()
}