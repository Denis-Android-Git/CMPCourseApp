package com.example.cmpcourseapp.di

import com.example.cmpcourseapp.mainstate.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainStateModule = module {
    viewModelOf(::MainViewModel)
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}