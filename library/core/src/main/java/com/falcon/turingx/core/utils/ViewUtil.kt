package com.falcon.turingx.core.utils

import android.app.Activity
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import com.falcon.turingx.core.ui.activity.TXActivity

/** Given a view, hide keyboard. */
fun View?.hideKeyboard() {
    this?.run {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }
}

/** Given a view, show keyboard. */
fun View?.showKeyboard() {
    this?.run {
        val imm =
            this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager?
        imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

/** Change view visibility to visible if it is not already visible . */
fun View?.setVisible() {
    this?.let {
        if (it.visibility == View.VISIBLE) {
            return@let
        } else {
            it.visibility = View.VISIBLE
        }
    }
}

/** Change view visibility to invisible if it is not already invisible . */
fun View?.setInvisible() {
    this?.let {
        if (it.visibility == View.INVISIBLE) {
            return@let
        } else {
            it.visibility = View.INVISIBLE
        }
    }
}

/** Change view visibility to gone if it is not already gone . */
fun View?.setGone() {
    this?.let {
        if (it.visibility == View.GONE) {
            return@let
        } else {
            it.visibility = View.GONE
        }
    }
}

/** Attempt convert a view to activity, return null if view cannot be casted to activity.*/
fun View.toActivity(): TXActivity? = context as? TXActivity?

/** Add a callback to a view to be called when view is measured. */
inline fun <T : View> T.afterMeasure(crossinline event: T.() -> Unit) {

    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                event()
            }
        }
    })
}