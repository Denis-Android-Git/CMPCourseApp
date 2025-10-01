package com.example.domain.util

sealed interface DataError : Error {
    enum class Remote : DataError {
        BAD_REQUEST,
        REQUEST_TIMEOUT,
        INTERNAL_SERVER_ERROR,
        NOT_FOUND,
        UNAUTHORIZED,
        FORBIDDEN,
        UNKNOWN,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET_CONNECTION,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERVICE_UNAVAILABLE,
        SERIALIZATION_ERROR
    }

    enum class Local : DataError {
        DISC_FULL,
        FILE_NOT_FOUND,
        UNKNOWN
    }
}