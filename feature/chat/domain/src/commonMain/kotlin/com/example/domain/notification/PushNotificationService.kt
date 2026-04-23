package com.example.domain.notification

import com.example.domain.auth.AuthInfo
import kotlinx.coroutines.flow.Flow

interface PushNotificationService {
    fun observeDeviceToken(): Flow<String?>
}