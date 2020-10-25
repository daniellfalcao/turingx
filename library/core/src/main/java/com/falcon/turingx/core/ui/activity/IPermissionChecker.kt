package com.falcon.turingx.core.ui.activity

interface IPermissionChecker {
    fun checkPermission(
        permissions: Array<String>,
        requestCode: Int,
        result: (isPermissionGranted: Boolean) -> Unit
    )
}