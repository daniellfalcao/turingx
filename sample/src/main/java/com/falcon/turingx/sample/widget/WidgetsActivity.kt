package com.falcon.turingx.sample.widget

import android.content.Intent
import android.os.Bundle
import com.falcon.turingx.core.ui.activity.TXActivity
import com.falcon.turingx.sample.R
import kotlinx.android.synthetic.main.activity_widgets.*

class WidgetsActivity : TXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widgets)

        buttonToolbar.setOnClickListener {
            Intent(this, ToolbarActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}