package com.falcon.turingx.sample

import android.content.Intent
import android.os.Bundle
import com.falcon.turingx.core.ui.activity.TXActivity
import com.falcon.turingx.sample.feedback.FeedbackControllerActivity
import com.falcon.turingx.sample.location.LocationActivity
import com.falcon.turingx.sample.widget.WidgetsActivity
import kotlinx.android.synthetic.main.activity_sample.*

class SampleActivity : TXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        buttonFeedbackController.setOnClickListener {
            Intent(this, FeedbackControllerActivity::class.java).also {
                startActivity(it)
            }
        }

        buttonLocation.setOnClickListener {
            Intent(this, LocationActivity::class.java).also {
                startActivity(it)
            }
        }

        buttonWidgets.setOnClickListener {
            Intent(this, WidgetsActivity::class.java).also {
                startActivity(it)
            }
        }
    }

}