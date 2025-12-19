package com.example.cmpcourseapp.di

import com.example.data.di.chatDataModule
import com.example.data.di.coreDataModule
import com.example.presentation.di.authPresentationModule
import com.example.presentation.di.chatPresentationModule
import com.example.presentation.di.corePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule,
            chatPresentationModule,
            mainStateModule,
            corePresentationModule,
            chatDataModule
        )
    }
}