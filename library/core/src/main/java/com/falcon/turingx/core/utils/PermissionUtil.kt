package com.falcon.turingx.core.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/** Check if the permissions contained in [this] is all granted. */
fun Array<String>.isPermissionsGranted(context: Context): Boolean {
    for (permission in this) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}
