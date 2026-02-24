package com.example.data.di

import com.example.data.lifeCycle.AppLifeCycleObserver
import com.example.data.network.ConnectivityObserver
import com.example.database.my_database.DbFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DbFactory(androidContext()) }
    singleOf(::AppLifeCycleObserver)
    singleOf(::ConnectivityObserver)

}