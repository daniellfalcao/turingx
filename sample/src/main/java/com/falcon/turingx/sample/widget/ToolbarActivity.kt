package com.falcon.turingx.sample.widget

import android.os.Bundle
import com.falcon.turingx.core.ui.activity.TXActivity
import com.falcon.turingx.sample.R
import kotlinx.android.synthetic.main.activity_toolbar.*

class ToolbarActivity: TXActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar)

        TXToolbar.animateTitle(text, scroll)

    }
}