package com.falcon.turingx.widget.utils

import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

/** Delete TextInputLayout error content when edit text changed. */
fun TextInputLayout.clearErrorOnTextChanged() {
    this.editText?.addTextChangedListener {
        this.error = null
    }
}

/** Delete EditText from TextInputLayout content. */
fun TextInputLayout.clear() = apply { this.editText?.clear() }