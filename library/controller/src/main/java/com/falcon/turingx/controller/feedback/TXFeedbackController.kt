package com.falcon.turingx.controller.feedback

import android.app.Application
import android.content.Context
import com.falcon.turingx.controller.feedback.components.Feedback
import com.falcon.turingx.core.utils.isAtLeastResumed
import timber.log.Timber
import java.lang.ref.WeakReference

class TXFeedbackController {

    private lateinit var context: Context

    private constructor(context: Context) {
        this.context = context
    }

    constructor() {
        requireNotNull(instance) { "TXFeedbackController must be initialized calling TXFeedbackController.initialize(context)" }
    }

    companion object {

        private var instance: TXFeedbackController? = null

        /**
         * Returns the instance of [TXFeedbackController].
         * If the instance not exists create a new instance and store the instance as static.
         * */
        fun initialize(application: Application) = instance ?: run {
            instance = TXFeedbackController(application)
            instance!!
        }

        /** internal function to retrieve [TXFeedbackController] instance */
        internal fun instance() = instance
    }

    /** A map with all feedback's that have already been displayed or are being displayed.*/
    private val handledFeedback = HashMap<String, MutableList<WeakReference<Feedback>>>()

    /**
     * Attempt show a feedback. See [canDisplay].
     *
     * @param feedback the instance of feedback that will be displayed.
     *
     * @return true if feedback will be displayed.
     * @return false if feedback will not be displayed.
     * */
    fun showFeedback(feedback: Feedback): Boolean {
        // check if feedback can be displayed by the rules of [canDisplay]
        val canDisplayFeedback = feedback.canDisplay()
        // if feedback can be displayed add then to list of handledFeedback's and show.
        if (canDisplayFeedback) {
            val feedbackList = mutableListOf(WeakReference(feedback))
            handledFeedback[feedback.screenKey]?.addAll(feedbackList) ?: run {
                handledFeedback[feedback.screenKey] = feedbackList
            }
            // tell him that he was treated.
            feedback.hasBeenHandled = true
            // display feedback
            feedback.show()
        }
        return canDisplayFeedback
    }

    /**
     * Check if [this] feedback can be displayed.
     *
     * Compare every handled feedback related to [this.screenKey] and check if have some feedback
     * with the same message and is still showing. If have some feedback with this definitions don't
     * display a new feedback with the same params,
     *
     * @return true if no have feedback's with the same message
     * @return true if have some feedback with the same message but is not showing anymore.
     * @return false if have feedback's with the same message and is still showing.
     * */
    private fun Feedback.canDisplay(): Boolean {
        // check if the lifecycle permits display the feedback
        if (!lifecycle.currentState.isAtLeastResumed()) return false
        // compare [this] feedback with all screen key related feedback's and check if message is
        // equals, if true and is still showing return false to not display [this] feedback again.
        handledFeedback[screenKey]?.map { it.get() }?.forEach { feedback ->
            // check if the current feedback [this] message is iquals to [feedback] message
            val isMessageEquals = message.invoke(context) == feedback?.message?.invoke(context)
            // check if the [feedback] is still showing
            val isShowing = feedback?.isShowing() ?: false

            Timber.d("O feedback tem a mesma mensagem? $isMessageEquals")
            Timber.d("O feedback está sendo mostrado atualmente? $isShowing")

            if (isMessageEquals && isShowing) return false
        }
        return true
    }
}

/** Extension to dispatch a feedback to controller where it can be displayed. */
fun <T : Feedback> T.dispatchToManager(): T {
    TXFeedbackController.instance()?.showFeedback(this)
    return this
}