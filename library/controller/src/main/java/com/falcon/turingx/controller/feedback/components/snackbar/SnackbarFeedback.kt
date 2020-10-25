package com.falcon.turingx.controller.feedback.components.snackbar

import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.falcon.turingx.controller.R
import com.falcon.turingx.controller.feedback.components.Feedback
import com.falcon.turingx.controller.feedback.components.FeedbackType.*
import java.lang.ref.WeakReference

class SnackbarFeedback internal constructor() : Feedback() {

    /** SnackbarFeedback builder */
    class Builder : FeedbackBuilder() {
        /** Snackbar configurations */
        var snackbar: (Snackbar.() -> Unit)? = null
            private set
        /** Set snackbar configurations dsl */
        fun withSnackbar(builder: Snackbar.() -> Unit) {
            snackbar = builder
        }
    }

    companion object {
        /** Build a SnackbarFeedback and checking if desired params is not null. */
        fun build(builder: Builder.() -> Unit): SnackbarFeedback {
            val snackbarBuilder = Builder().apply(builder)
            // validate if builder have the required params
            snackbarBuilder.validate()
            // after evaluate, return the feedback
            return SnackbarFeedback().apply {

                this.lifecycle = snackbarBuilder.lifecycle!!
                this.message = snackbarBuilder.message!!
                this.view = snackbarBuilder.view
                this.type = snackbarBuilder.type!!

                this.snackbarBuilder = snackbarBuilder.snackbar
            }
        }
    }

    /** Weak reference to Snackbar view, used to dismiss when is requested. */
    private var snackbar: WeakReference<Snackbar>? = null

    /** Snackbar configurations */
    private var snackbarBuilder: (Snackbar.() -> Unit)? = null

    /** Tells to snackbar that it should use the time of [Snackbar.LENGTH_LONG] */
    private var useLongDuration: Boolean = false

    /** Build and show a Snackbar. */
    override fun show() {
        snackbar = view?.run {
            val duration = if (useLongDuration) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT
            val message = this@SnackbarFeedback.message(context)
            val color = when (type) {
                SUCCESS -> R.color.turingx_feedback_success
                ERROR -> R.color.turingx_feedback_error
                NEUTRAL -> R.color.turingx_feedback_neutral
            }
            Snackbar.make(this, message, duration).apply {
                this.view.setBackgroundColor(ContextCompat.getColor(context, color))
                this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    ?.apply {
                        setTextColor(Color.WHITE)
                        maxLines = 5
                    }
                this@SnackbarFeedback.snackbarBuilder?.invoke(this)
            }.let { WeakReference(it) }
        }?.apply { get()?.show() }
    }

    /**
     * Dismiss the current [snackbar] if its reference exists and set the current reference to null.
     * */
    override fun dismiss() {
        snackbar?.get()?.dismiss()
        snackbar = null
    }

    /**
     * @return true if the current [snackbar] is showing.
     * @return false if the current [snackbar] is not showing.
     * */
    override fun isShowing(): Boolean {
        return snackbar?.get()?.isShown ?: false
    }
}