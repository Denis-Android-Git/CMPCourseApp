package com.example.presentation.di

import com.example.presentation.email_verification.EmailVerificationScreenViewModel
import com.example.presentation.forgot_password.ForgotPasswordViewModel
import com.example.presentation.login.LoginViewModel
import com.example.presentation.register.RegisterViewModel
import com.example.presentation.register_success.RegisterSuccessViewModel
import com.example.presentation.reset_password.ResetPasswordViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationScreenViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)

}