package com.example.data.auth

import com.example.data.auth.dto.AuthInfoSerializable
import com.example.data.auth.dto.requests.EmailRequest
import com.example.data.auth.dto.requests.LoginRequest
import com.example.data.auth.dto.requests.RegisterRequest
import com.example.data.mappers.toDomain
import com.example.data.network.get
import com.example.data.network.post
import com.example.domain.auth.AuthInfo
import com.example.domain.auth.AuthService
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import com.example.domain.util.map
import io.ktor.client.HttpClient

class KtorAuthService(
    private val httpClient: HttpClient
) : AuthService {
    override suspend fun register(email: String, password: String, name: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = RegisterRequest(
                email = email,
                password = password,
                username = name
            )
        )
    }

    override suspend fun resendVarificationEmail(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/resend-verification",
            body = EmailRequest(
                email = email
            )
        )
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> {
        return httpClient.get(
            route = "/auth/verify",
            queryParams = mapOf(
                "token" to token
            )
        )
    }

    override suspend fun login(email: String, password: String): CustomResult<AuthInfo, DataError.Remote> {
        return httpClient.post<LoginRequest, AuthInfoSerializable>(
            route = "/auth/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        ).map {
            it.toDomain()
        }
    }

}