package com.example.data.notification

import com.example.data.dto.request.RegisterDeviceTokenRequest
import com.example.data.network.delete
import com.example.data.network.post
import com.example.domain.notification.DeviceTokenService
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import io.ktor.client.HttpClient

class KtorDeviceTokenService(
    private val httpClient: HttpClient
) : DeviceTokenService {
    override suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/notification/register",
            body = RegisterDeviceTokenRequest(
                token, platform
            )
        )
    }

    override suspend fun unregisterToken(token: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/notification/$token"
        )
    }
}