package com.example.data.logging

import co.touchlab.kermit.Logger
import com.example.domain.logging.MyLogger

object KermitLogger : MyLogger {
    override fun info(message: String) {
        Logger.i(message)
    }

    override fun error(message: String, throwable: Throwable?) {
        Logger.e(message, throwable)
    }

    override fun warn(message: String) {
        Logger.w(message)
    }

    override fun debug(message: String) {
        Logger.d(message)
    }
}