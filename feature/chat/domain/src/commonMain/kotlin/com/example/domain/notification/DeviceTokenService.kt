package com.example.domain.notification

import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult

interface DeviceTokenService {
    suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote>

    suspend fun unregisterToken(
        token: String
    ): EmptyResult<DataError.Remote>
}