package com.example.data.di

import com.example.data.lifeCycle.AppLifeCycleObserver
import com.example.data.network.ConnectionErrorHandler
import com.example.data.network.ConnectivityObserver
import com.example.data.notification.FirebasePushNotificationService
import com.example.database.my_database.DbFactory
import com.example.domain.notification.PushNotificationService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DbFactory() }
    singleOf(::AppLifeCycleObserver)
    singleOf(::ConnectivityObserver)
    singleOf(::ConnectionErrorHandler)
    singleOf(::FirebasePushNotificationService) bind PushNotificationService::class

}