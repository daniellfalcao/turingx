package com.falcon.turingx.core.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.falcon.turingx.core.utils.*

/** A base activity. */
abstract class TXActivity : AppCompatActivity(), IPermissionChecker {

    companion object {
        const val IS_CHANGING_CONFIGURATION = "is_activity_changing_configuration"
    }

    private var requestedPermissions: HashMap<Int, ((isPermissionGranted: Boolean) -> Unit)?> = hashMapOf()

    /**
     * Check if [permissions] is granted, if not request permissions and return the result of the
     * user operation in [result].
     * */
    @SuppressLint("NewApi")
    override fun checkPermission(
        permissions: Array<String>,
        requestCode: Int,
        result: (isPermissionGranted: Boolean) -> Unit
    ) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !permissions.isPermissionsGranted(this)) {
            requestedPermissions[requestCode] = result
            requestPermissions(permissions, requestCode)
        } else {
            result(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
            requestedPermissions[requestCode]?.let { it(false) }
        } else {
            requestedPermissions[requestCode]?.let { it(true) }
        }
        requestedPermissions[requestCode] = null
    }

    /**
     * Check if activity is running at the first time.
     *
     * If an new activity is created [func] will be called, if lifecycle is recreating the
     * activity [func] will not be called.
     * */
    protected fun Bundle?.whenActivityLoadingAtFirstTime(func: () -> Unit) {
        if (this == null) {
            func()
        } else {
            if (!getBoolean(IS_CHANGING_CONFIGURATION)) {
                func()
            }
        }
    }
}

/** Retrieve the extra from activity contained in the [key]. */
inline fun <reified T : Any> TXActivity.extra(key: String) = lazy {
    val value = intent.extras?.get(key)
    value as T
}

/**
 * Retrieve the extra from activity contained in the [key]. If don't have a value (null) to key,
 * returns a default value.
 * */
inline fun <reified T : Any> TXActivity.extra(key: String, default: T) = lazy {
    val value = intent.extras?.get(key) ?: default
    value as T
}

/** A key to activity. */
fun TXActivity.screenKey() = localClassName

/** DSL function to access intent from activity. */
fun TXActivity.withIntent(event: Intent.() -> Unit) = apply { this.intent?.run(event) }

/**
 * Utils to get check current lifecycle state.
 * */
fun TXActivity.isAtLeastResumed() = lifecycle.currentState.isAtLeastResumed()
fun TXActivity.isAtLeastDestroyed() = lifecycle.currentState.isAtLeastDestroyed()
fun TXActivity.isAtLeastStarted() = lifecycle.currentState.isAtLeastStarted()
fun TXActivity.isAtLeastInitialized() = lifecycle.currentState.isAtLeastInitialized()
fun TXActivity.isAtLeastCreated() = lifecycle.currentState.isAtLeastCreated()