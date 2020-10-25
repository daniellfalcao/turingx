package com.falcon.turingx.core.utils

import com.falcon.turingx.core.locale.TXlocale
import java.text.Normalizer
import java.util.*

@ExperimentalStdlibApi
/** Parse the first letter of each word to upper case. */
fun String.capitalizeWords(locale: Locale = TXlocale.BRASIL()): String {
    var result = ""
    for (i in this.toLowerCase(locale).split(" ")) {
        result += i.capitalize(locale) + " "
    }
    return result
}

/** Remove special characters like a accent (á, ç) and return the string normalized in lower case. */
fun String.normalize(): String {
    var string = trim()
    val out = CharArray(string.length)
    string = Normalizer.normalize(string, Normalizer.Form.NFD)
    var j = 0
    var i = 0
    val n = string.length
    while (i < n) {
        val c = string[i]
        if (c <= '\u007F') out[j++] = c
        ++i
    }
    return String(out).toLowerCase(TXlocale.BRASIL())
}