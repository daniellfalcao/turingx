@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate")

package com.falcon.turingx.widget.progressdialog

import android.app.ProgressDialog
import android.content.Context
import com.falcon.turingx.core.components.StringWrapper

/** A progress dialog.*/
class TXProgressDialog(val context: Context?, var message: StringWrapper, var isCancelable: Boolean = true) {

    private var progressDialog: ProgressDialog? = null

    /** Display the progress dialog. */
    fun show() {
        // if progress dialog is showing dismiss.
        if (progressDialog?.isShowing == true) dismiss()
        // creates the indeterminate progress dialog and show.
        progressDialog = context?.indeterminateProgressDialog(message(context)).apply {
            this?.setCancelable(isCancelable)
        }
    }

    /** Dismiss the progress dialog. */
    fun dismiss() {
        try {
            progressDialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Creates a indeterminate progress dialog. */
    private fun Context.indeterminateProgressDialog(message: String): ProgressDialog {
        return ProgressDialog(this).apply {
            isIndeterminate = true
            setMessage(message)
            show()
        }
    }

}