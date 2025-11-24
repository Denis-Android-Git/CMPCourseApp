package com.example.data.mappers

import com.example.data.auth.dto.AuthInfoSerializable
import com.example.data.auth.dto.UserSerializable
import com.example.domain.auth.AuthInfo
import com.example.domain.auth.User

fun AuthInfoSerializable.toDomain(): AuthInfo {
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDomain()
    )
}

fun AuthInfo.toSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toSerializable()
    )
}

fun User.toSerializable(): UserSerializable {
    return UserSerializable(
        id = id,
        email = email,
        username = userName,
        hasVerifiedEmail = hasVarifiedEmail,
        profilePicture = profilePicture
    )
}

fun UserSerializable.toDomain(): User {
    return User(
        id = id,
        email = email,
        userName = username,
        hasVarifiedEmail = hasVerifiedEmail,
        profilePicture = profilePicture
    )
}