package com.falcon.turingx.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import androidx.fragment.app.Fragment

const val CHROME_PACKAGE = "com.android.chrome"

/**
 * Attempt open [url] in the Chrome with the given [headers].
 *
 * @param url url that will be loaded.
 * @param headers the current headers of the given url.
 * @param onActivityNotFound callback function if the device don't have the chrome installed.
 * */
fun Context.openChrome(
    url: String,
    vararg headers: (Pair<String, String>) = emptyArray(),
    onActivityNotFound: (() -> Unit) = {}
) {

    // build intent
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        setPackage(CHROME_PACKAGE)
    }
    // set headers
    if (headers.isNotEmpty()) {
        val bundle = Bundle()
        headers.forEach { header ->
            bundle.putString(header.first, header.second)
        }
        intent.putExtra(Browser.EXTRA_HEADERS, bundle)
    }
    // attempt to open chrome.
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onActivityNotFound()
    }

}

/** Wraps [openChrome] context extension function to be used by a fragment. */
fun Fragment.openChrome(
    url: String,
    vararg headers: Pair<String, String> = emptyArray(),
    onActivityNotFound: (() -> Unit) = {}
) = activity?.openChrome(url = url, headers = headers, onActivityNotFound = onActivityNotFound)
