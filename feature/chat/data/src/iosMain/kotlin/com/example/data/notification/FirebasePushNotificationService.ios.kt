package com.example.data.notification

import com.example.domain.notification.PushNotificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UIKit.registeredForRemoteNotifications

actual class FirebasePushNotificationService :
    PushNotificationService {
    actual override fun observeDeviceToken(): Flow<String?> {
        return IosDeviseTokenHolder
            .token
            .onStart {
                if (IosDeviseTokenHolder.token.value == null) {
                    val userDefaults = NSUserDefaults.standardUserDefaults
                    val fcmToken = userDefaults.stringForKey("FCM_TOKEN")

                    if (fcmToken != null) {
                        IosDeviseTokenHolder.updateToken(fcmToken)
                    } else {
                        UIApplication.sharedApplication.registerForRemoteNotifications()
                    }
                }
            }
    }
}