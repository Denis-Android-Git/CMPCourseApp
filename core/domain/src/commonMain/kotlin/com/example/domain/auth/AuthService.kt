package com.example.domain.auth

import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult

interface AuthService {
    suspend fun register(
        email: String,
        password: String,
        name: String
    ): EmptyResult<DataError.Remote>

    suspend fun resendVarificationEmail(email: String): EmptyResult<DataError.Remote>

    suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote>

    suspend fun login(
        email: String,
        password: String
    ): CustomResult<AuthInfo, DataError.Remote>

    suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote>
    suspend fun resetPassword(
        token: String,
        password: String
    ): EmptyResult<DataError.Remote>

}