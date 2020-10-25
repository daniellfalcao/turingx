package com.falcon.turingx.widget.utils

import android.widget.EditText

/** Delete EditText content. */
fun EditText.clear() = apply { this.setText("") }
