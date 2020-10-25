package com.falcon.turingx.controller.feedback.components.dialog

import android.app.Dialog
import androidx.annotation.StyleRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.falcon.turingx.controller.feedback.components.Feedback
import com.falcon.turingx.core.components.StringWrapper
import java.lang.ref.WeakReference

class DialogFeedback internal constructor() : Feedback() {

    /** DialogFeedback builder */
    class Builder : FeedbackBuilder(null, null, null) {
        /** Dialog optional theme */
        @StyleRes
        var theme: Int? = null

        /** Dialog title */
        var title: StringWrapper? = null

        /** Dialog button text */
        var actionText: StringWrapper? = null

        /** Tell to dialog if can be cancelable */
        var cancelable: Boolean = true

        /** Callback triggered when click in text button */
        var onActionTextClicked: (() -> Unit)? = null
            private set

        /** Callback triggered when dialog dismiss */
        var onDismiss: (() -> Unit)? = null
            private set

        fun onDismiss(event: () -> Unit) = apply { onDismiss = event }
        fun onActionTextClicked(event: () -> Unit) = apply { onActionTextClicked = event }

        override fun validate() {
            super.validate()
            requireNotNull(title) { "Feedback must have title." }
            requireNotNull(actionText) { "Feedback must have a actionText." }
        }
    }

    companion object {
        /** Build a DialogFeedback and checking if desired params is not null. */
        fun build(builder: Builder.() -> Unit): DialogFeedback {
            val dialogBuilder = Builder().apply(builder)
            // validate if builder have the required params
            dialogBuilder.validate()
            // after evaluate, return the feedback
            return DialogFeedback().apply {

                this.lifecycle = dialogBuilder.lifecycle!!
                this.message = dialogBuilder.message!!
                this.view = dialogBuilder.view
                this.type = dialogBuilder.type!!

                this.theme = dialogBuilder.theme
                this.title = dialogBuilder.title
                this.cancelable = dialogBuilder.cancelable
                this.onDismiss = dialogBuilder.onDismiss
                this.actionText = dialogBuilder.actionText
                this.onActionTextClicked = dialogBuilder.onActionTextClicked
            }
        }
    }

    /** Weak reference to dialog view, used to dismiss when is requested. */
    private var dialog: WeakReference<Dialog>? = null

    /** Dialog configurations */
    private var theme: Int? = null
    private var title: StringWrapper? = null
    private var actionText: StringWrapper? = null
    private var cancelable = true
    private var onDismiss: (() -> Unit)? = null
    private var onActionTextClicked: (() -> Unit)? = null

    /** Build and show a Dialog. */
    override fun show() {
        dialog = view?.run {
            val title = title!!(context)
            val message = message!!(context)
            val positiveButtonText = actionText!!(context)
            theme?.let { theme ->
                MaterialAlertDialogBuilder(context, theme)
            } ?: run {
                MaterialAlertDialogBuilder(context)
            }.apply {
                setTitle(title)
                setMessage(message)
                setCancelable(cancelable)
                setPositiveButton(positiveButtonText) { _, _ ->
                    onActionTextClicked?.invoke() ?: run { dismiss() }
                }
                setOnDismissListener {
                    onDismiss?.invoke()
                }
            }
        }?.show()?.let { WeakReference(it) }
    }

    /**
     * Dismiss the current [dialog] if its reference exists and set the current reference to null.
     * */
    override fun dismiss() {
        dialog?.get()?.dismiss()
        dialog = null
    }

    /**
     * @return true if the current [dialog] is showing.
     * @return false if the current [dialog] is not showing.
     * */
    override fun isShowing(): Boolean {
        return dialog?.get()?.isShowing ?: false
    }
}