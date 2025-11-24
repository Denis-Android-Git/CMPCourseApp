package com.example.data.di

import com.example.data.auth.DataStoreSessionStorage
import com.example.data.auth.KtorAuthService
import com.example.data.logging.KermitLogger
import com.example.data.network.HttpClientFactory
import com.example.domain.auth.AuthService
import com.example.domain.auth.SessionStorage
import com.example.domain.logging.MyLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<MyLogger> { KermitLogger }
    single {
        HttpClientFactory(get()).create(get())
    }
    singleOf(::KtorAuthService) bind AuthService::class
    singleOf(::DataStoreSessionStorage) bind SessionStorage::class

}