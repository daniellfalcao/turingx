@file:Suppress("UNCHECKED_CAST", "unused")

package com.falcon.turingx.core.result

import java.io.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A discriminated union that encapsulates successful outcome with a value of type [T]
 * or a failure with an arbitrary [Throwable] exception.
 */
open class TXResult<out T> (val value: Any?) : Serializable {

    /**
     * Returns `true` if this instance represents successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = value !is Failure

    /**
     * Returns `true` if this instance represents failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = value is Failure

    // value & exception retrieval

    /**
     * Returns the encapsulated value if this instance represents [success][TXResult.isSuccess] or `null`
     * if it is [failure][TXResult.isFailure].
     *
     * This function is shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    /**
     * Returns the encapsulated exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    /**
     * Returns a string `Success(v)` if this instance represents [success][TXResult.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    override fun toString(): String =
        when (value) {
            is Failure -> value.toString() // "Failure($exception)"
            else -> "Success($value)"
        }

    // companion with constructors

    /**
     * Companion object for [TXResult] class that contains its constructor functions
     * [success] and [failure].
     */
    companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */

        fun <T> success(value: T): TXResult<T> = TXResult(value)

        /**
         * Returns an instance that encapsulates the given [exception] as failure.
         */

        fun <T> failure(exception: Throwable): TXResult<T> = TXResult(createFailure(exception))
    }

    class Failure(@JvmField val exception: Throwable) : Serializable {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
}

/**
 * Creates an instance of internal marker [TXResult.Failure] class to
 * make sure that this class is not exposed in ABI.
 */
internal fun createFailure(exception: Throwable): Any =
    TXResult.Failure(exception)

/**
 * Throws exception if the NewResult is failure. This internal function minimizes
 * d bytecode for [getOrThrow] and makes sure that in the future we can
 * add some exception-augmenting logic here (if needed).
 */
fun TXResult<*>.throwOnFailure() {
    if (value is TXResult.Failure) throw value.exception
}

/**
 * Calls the specified function [block] and returns its encapsulated NewResult if invocation was successful,
 * catching and encapsulating any thrown exception as a failure.
 */
fun <R> runCatching(block: () -> R): TXResult<R> {
    return try {
        TXResult.success(block())
    } catch (e: Throwable) {
        TXResult.failure(e)
    }
}

/**
 * Calls the specified function [block] with `this` value as its receiver and returns its encapsulated NewResult
 * if invocation was successful, catching and encapsulating any thrown exception as a failure.
 */
fun <T, R> T.runCatching(block: T.() -> R): TXResult<R> {
    return try {
        TXResult.success(block())
    } catch (e: Throwable) {
        TXResult.failure(e)
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][TXResult.isSuccess] or throws the encapsulated exception
 * if it is [failure][TXResult.isFailure].
 *
 * This function is shorthand for `getOrElse { throw it }` (see [getOrElse]).
 */
fun <T> TXResult<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

/**
 * Returns the encapsulated value if this instance represents [success][TXResult.isSuccess] or the
 * NewResult of [onFailure] function for encapsulated exception if it is [failure][TXResult.isFailure].
 *
 * Note, that an exception thrown by [onFailure] function is rethrown by this function.
 *
 * This function is shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
 */
@ExperimentalContracts
fun <R, T : R> TXResult<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> value as T
        else -> onFailure(exception)
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][TXResult.isSuccess] or the
 * [defaultValue] if it is [failure][TXResult.isFailure].
 *
 * This function is shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
fun <R, T : R> TXResult<T>.getOrDefault(defaultValue: R): R {
    if (isFailure) return defaultValue
    return value as T
}

/**
 * Returns the the NewResult of [onSuccess] for encapsulated value if this instance represents [success][TXResult.isSuccess]
 * or the NewResult of [onFailure] function for encapsulated exception if it is [failure][TXResult.isFailure].
 *
 * Note, that an exception thrown by [onSuccess] or by [onFailure] function is rethrown by this function.
 */
@ExperimentalContracts
fun <R, T> TXResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R
): R {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(value as T)
        else -> onFailure(exception)
    }
}

/**
 * Returns the encapsulated NewResult of the given [transform] function applied to encapsulated value
 * if this instance represents [success][TXResult.isSuccess] or the
 * original encapsulated exception if it is [failure][TXResult.isFailure].
 *
 * Note, that an exception thrown by [transform] function is rethrown by this function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
fun <R, T> TXResult<T>.map(transform: (value: T) -> R): TXResult<R> {
    return when {
        isSuccess -> TXResult.success(transform(value as T))
        else -> TXResult(value)
    }
}

/**
 * Returns the encapsulated NewResult of the given [transform] function applied to encapsulated value
 * if this instance represents [success][TXResult.isSuccess] or the
 * original encapsulated exception if it is [failure][TXResult.isFailure].
 *
 * Any exception thrown by [transform] function is caught, encapsulated as a failure and returned by this function.
 * See [map] for an alternative that rethrows exceptions.
 */
fun <R, T> TXResult<T>.mapCatching(transform: (value: T) -> R): TXResult<R> {
    return when {
        isSuccess -> runCatching { transform(value as T) }
        else -> TXResult(value)
    }
}

fun <R> TXResult<*>.mapFailure(): TXResult<R> {
    return this as TXResult<R>
}

/**
 * Performs the given [action] on encapsulated exception if this instance represents [failure][TXResult.isFailure].
 * Returns the original `NewResult` unchanged.
 *
 */
suspend fun <T> TXResult<T>.onFailure(action: suspend (exception: Throwable) -> Unit): TXResult<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}

/**
 * Performs the given [action] on encapsulated value if this instance represents [success][TXResult.isSuccess].
 * Returns the original `NewResult` unchanged.
 *
 */
suspend fun <T> TXResult<T>.onSuccess(action: suspend (value: T) -> Unit): TXResult<T> {
    if (isSuccess) action(value as T)
    return this
}