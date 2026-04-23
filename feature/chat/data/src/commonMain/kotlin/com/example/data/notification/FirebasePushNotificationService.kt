package com.example.data.notification

import com.example.domain.notification.PushNotificationService
import kotlinx.coroutines.flow.Flow

expect class FirebasePushNotificationService : PushNotificationService {
    override fun observeDeviceToken(): Flow<String?>
}