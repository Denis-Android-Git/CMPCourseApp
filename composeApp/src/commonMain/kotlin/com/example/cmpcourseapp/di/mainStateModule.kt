package com.example.cmpcourseapp.di

import com.example.cmpcourseapp.mainstate.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainStateModule = module {
    viewModelOf(::MainViewModel)
}