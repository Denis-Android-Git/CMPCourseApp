package com.example.domain.logging

interface MyLogger {
    fun info(message: String)
    fun error(message: String, throwable: Throwable? = null)
    fun warn(message: String)
    fun debug(message: String)
}