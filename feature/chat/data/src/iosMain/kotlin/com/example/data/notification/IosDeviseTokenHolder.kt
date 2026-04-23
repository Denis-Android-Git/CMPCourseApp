package com.example.data.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object IosDeviseTokenHolder {

    private var _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    fun updateToken(token: String) {
        _token.value = token
    }
}