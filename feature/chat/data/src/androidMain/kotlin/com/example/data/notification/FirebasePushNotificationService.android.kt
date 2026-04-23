package com.example.data.notification

import com.example.domain.logging.MyLogger
import com.example.domain.notification.PushNotificationService
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext

actual class FirebasePushNotificationService(
    private val myLogger: MyLogger
) : PushNotificationService {
    actual override fun observeDeviceToken(): Flow<String?> = flow {
        try {
            val fcmToken = Firebase.messaging.token.await()
            myLogger.info("Initial token received = $fcmToken")
            emit(fcmToken)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            myLogger.error("Failed to get FCM token", e)
            emit(null)
        }
    }
}