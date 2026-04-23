package com.example.data.notification

import com.example.domain.auth.SessionStorage
import com.example.domain.notification.DeviceTokenService
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FMS : FirebaseMessagingService() {

    private val deviceTokenService by inject<DeviceTokenService>()
    private val sessionStorage by inject<SessionStorage>()
    private val appScope by inject<CoroutineScope>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        appScope.launch {
            val authInfo = sessionStorage.observeAuthInfo().first()
            if (authInfo != null) {
                deviceTokenService.registerToken(token, "ANDROID")
            }
        }
    }
}