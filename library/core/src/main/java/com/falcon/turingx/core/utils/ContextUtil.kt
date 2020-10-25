package com.falcon.turingx.core.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/** Attempt to convert a context in activity. Return null if context is not instance of a activity .*/
fun Context.toActivity() = this as? AppCompatActivity?