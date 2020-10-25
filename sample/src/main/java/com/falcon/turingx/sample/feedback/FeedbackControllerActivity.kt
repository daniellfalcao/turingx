package com.falcon.turingx.sample.feedback

import android.os.Bundle
import android.widget.Toast
import com.falcon.turingx.controller.feedback.TXFeedbackController
import com.falcon.turingx.controller.feedback.components.FeedbackType
import com.falcon.turingx.controller.feedback.components.dialog.DialogFeedback
import com.falcon.turingx.controller.feedback.components.snackbar.SnackbarFeedback
import com.falcon.turingx.controller.feedback.dispatchToManager
import com.falcon.turingx.core.components.toStringWrapper
import com.falcon.turingx.core.ui.activity.TXActivity
import com.falcon.turingx.sample.R
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackControllerActivity : TXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        TXFeedbackController.initialize(application)

        buttonSnackbar.setOnClickListener {
            SnackbarFeedback.build {
                this.view = container
                this.lifecycle = this@FeedbackControllerActivity.lifecycle
                this.message = "Uma mensagem na snackbar".toStringWrapper()
                this.type = FeedbackType.NEUTRAL
            }.dispatchToManager()
        }

        buttonSnackbarAction.setOnClickListener {
            SnackbarFeedback.build {
                this.view = container
                this.lifecycle = this@FeedbackControllerActivity.lifecycle
                this.message = "Uma mensagem na snackbar".toStringWrapper()
                this.type = FeedbackType.NEUTRAL
                withSnackbar {
                    setAction("Ação") {
                        Toast.makeText(
                            this@FeedbackControllerActivity,
                            "ação",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.dispatchToManager()
        }

        buttonDialog.setOnClickListener {
            DialogFeedback.build {
                this.view = container
                this.lifecycle = this@FeedbackControllerActivity.lifecycle
                this.title = "Título".toStringWrapper()
                this.message = "Uma mensagem no dialog".toStringWrapper()
                this.actionText = "YeeY".toStringWrapper()
                this.type = FeedbackType.NEUTRAL
            }.dispatchToManager()
        }

    }

}