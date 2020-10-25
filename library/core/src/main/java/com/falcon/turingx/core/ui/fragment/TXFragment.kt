@file:Suppress("UNCHECKED_CAST")

package com.falcon.turingx.core.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.falcon.turingx.core.ui.activity.TXActivity

/** A base fragment. */
abstract class TXFragment : Fragment() {

    companion object {
        const val IS_CHANGING_CONFIGURATION = "is_fragment_changing_configuration"
    }

    /**
     * Check if fragment is running at the first time.
     *
     * If an new fragment is created [func] will be called, if lifecycle is recreating the
     * fragment [func] will not be called.
     * */
    protected fun Bundle?.whenFragmentLoadingAtFirstTime(func: () -> Unit) {
        if (this == null) {
            func()
        } else {
            if (!getBoolean(IS_CHANGING_CONFIGURATION)) {
                func()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_CHANGING_CONFIGURATION, activity?.isChangingConfigurations ?: false)
    }
}

/** DSL function to access activity from fragment. */
fun <T : TXActivity> TXFragment.withActivity(event: T.() -> Unit) {
    (activity as? T)?.run(event)
}

/** DSL function to access parent fragment. */
fun TXFragment.withParentFragment(event: Fragment.() -> Unit) {
    parentFragment?.run(event)
}

/** DSL function to access context from fragment. */
fun TXFragment.withContext(event: Context.() -> Unit) {
    context?.run(event)
}

/** DSL function to access arguments from fragment. */
fun TXFragment.withArguments(event: Bundle.() -> Unit) {
    this.arguments?.run(event)
}

/** Retrieve the argument from fragment contained in the [key]. */
inline fun <reified T : Any> TXFragment.extra(key: Lazy<String>) = lazy {
    val value = arguments?.get(key.value)
    value as T
}

/**
 * Retrieve the argument from fragment contained in the [key]. If don't have a value (null) to key,
 * returns a default value.
 * */
inline fun <reified T : Any> TXFragment.extra(key: String, default: T) = lazy {
    val value = arguments?.get(key) ?: default
    value as T
}