package com.example.domain.error

import com.example.domain.util.Error

enum class ConnectionError : Error {
    NOT_CONNECTED,
    MESSAGE_SEND_FAILED
}