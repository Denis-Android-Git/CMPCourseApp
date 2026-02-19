package com.example.data.lifeCycle

import kotlinx.coroutines.flow.Flow

expect class AppLifeCycleObserver {
    val isInForeground: Flow<Boolean>
}