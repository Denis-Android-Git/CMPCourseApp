package com.example.data.lifeCycle

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationState
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.UIKit.UIApplicationWillResignActiveNotification

actual class AppLifeCycleObserver {
    actual val isInForeground: Flow<Boolean> = callbackFlow {
        val currentState = UIApplication.sharedApplication.applicationState
        val isInForeGround = when (currentState) {
            UIApplicationState.UIApplicationStateActive -> true
            // App itself is active, but could be that notification center is dragged down
            // or there's an ongoing phone call
            UIApplicationState.UIApplicationStateInactive -> true
            else -> false
        }
        send(isInForeGround)

        val notificationCenter = NSNotificationCenter.defaultCenter

        val foregroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(true)
        }

        val willEnterForegroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(true)
        }

        val backgroundObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(false)
        }

        val willResignActiveObserver = notificationCenter.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) {
            trySend(false)
        }

        awaitClose {
            notificationCenter.removeObserver(foregroundObserver)
            notificationCenter.removeObserver(willEnterForegroundObserver)
            notificationCenter.removeObserver(backgroundObserver)
            notificationCenter.removeObserver(willResignActiveObserver)
        }
    }
}