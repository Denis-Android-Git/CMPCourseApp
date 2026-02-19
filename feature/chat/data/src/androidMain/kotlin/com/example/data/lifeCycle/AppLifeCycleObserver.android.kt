package com.example.data.lifeCycle

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.flowOn

actual class AppLifeCycleObserver {
    actual val isInForeground: Flow<Boolean> = callbackFlow {
        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        val isAtLeastStarted = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        send(isAtLeastStarted)
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> trySend(true)
                Lifecycle.Event.ON_STOP -> trySend(false)
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        awaitClose {
            lifecycle.removeObserver(observer)
        }
    }.flowOn(Dispatchers.Main)
}