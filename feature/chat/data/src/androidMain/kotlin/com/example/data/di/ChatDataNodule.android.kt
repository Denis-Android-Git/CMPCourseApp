package com.example.data.di

import com.example.database.my_database.DbFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DbFactory(androidContext()) }
}