package com.example.cmpcourseapp.navigation

import com.example.data.logging.KermitLogger
import com.example.domain.logging.MyLogger

object ExternalUriHandler {
    private val myLogger: MyLogger = KermitLogger
    private var cached: String? = null
    var listener: ((uri: String) -> Unit)? = null
        set(value) {
            field = value
            if (value != null) {
                cached?.let {
                    myLogger.debug("uri_check = ExternalUriHandler: $it")
                    value.invoke(it)
                }
                cached = null
            }
        }

    fun onNewUri(uri: String) {
        cached = uri
        listener?.let {
            myLogger.debug("uri_check = ExternalUriHandler onNewUri: $uri")
            it.invoke(uri)
            cached = null
        }
    }
}