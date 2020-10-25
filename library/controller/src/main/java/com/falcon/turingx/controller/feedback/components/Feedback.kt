package com.falcon.turingx.controller.feedback.components

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.falcon.turingx.core.components.StringWrapper
import java.lang.ref.WeakReference

abstract class Feedback {

    /** Base feedback builder. */
    abstract class FeedbackBuilder(
        var lifecycle: Lifecycle? = null,
        var message: StringWrapper? = null,
        var view: ViewGroup? = null,
        var type: FeedbackType? = null
    ) {
        open fun validate() {
            requireNotNull(lifecycle) { "Feedback must have a lifecycle." }
            requireNotNull(message) { "Feedback must have a message." }
            requireNotNull(view) { "Feedback must have a view." }
            requireNotNull(type) { "Feedback must have a type." }
        }
    }

    /** Indicates if feedback has been handled by controller */
    var hasBeenHandled: Boolean = false

    /** Get a key to feedback in relation to its view */
    val screenKey: String
        get() = view?.context.toString()

    /** The lifecycle of feedback owner */
    lateinit var lifecycle: Lifecycle

    /** The view where feedback with be displayed */
    private var _view: WeakReference<ViewGroup>? = null
    var view: ViewGroup?
        set(value) = let { _view = WeakReference(value) }
        get() = _view?.get()

    /** The message displayed by the feedback */
    lateinit var message: StringWrapper

    /** The type of feedback. See [FeedbackType] */
    var type: FeedbackType = FeedbackType.NEUTRAL

    /** Build and show a feedback */
    abstract fun show()

    /** Dismiss the current feedback if its reference exists. */
    abstract fun dismiss()

    /**
     * @return true if the current feedback is showing.
     * @return false if the current feedback is not showing.
     * */
    abstract fun isShowing(): Boolean

}