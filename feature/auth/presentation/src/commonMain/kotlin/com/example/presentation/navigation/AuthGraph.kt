package com.example.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.example.presentation.email_verification.EmailVerificationScreenRoot
import com.example.presentation.forgot_password.ForgotPasswordRoot
import com.example.presentation.login.LoginRoot
import com.example.presentation.register.RegisterRoot
import com.example.presentation.register_success.RegisterSuccessRoot
import com.example.presentation.reset_password.ResetPasswordRoot

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    navigation<AuthGraphRoutes.Graph>(
        startDestination = AuthGraphRoutes.Login
    ) {
        composable<AuthGraphRoutes.Login> {
            LoginRoot(
                onLoginSuccess = onLoginSuccess,
                onCreateAccountClick = {
                    navController.navigate(AuthGraphRoutes.Register) {
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(AuthGraphRoutes.ForgotPassword)
                }
            )
        }

        composable<AuthGraphRoutes.Register> {
            RegisterRoot(
                onRegisterSuccess = {
                    navController.navigate(AuthGraphRoutes.RegisterSuccess(it)) {
                        popUpTo<AuthGraphRoutes.Register> {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo<AuthGraphRoutes.Register> {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<AuthGraphRoutes.RegisterSuccess> {
            RegisterSuccessRoot(
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo<AuthGraphRoutes.RegisterSuccess> {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<AuthGraphRoutes.EmailVerification>(
            deepLinks = listOf(
                navDeepLink {
                    this.uriPattern = "https://chirp.pl-coding.com/api/auth/verify?token={token}"
                },
                navDeepLink {
                    this.uriPattern = "chirp://chirp.pl-coding.com/api/auth/verify?token={token}"
                }
            )
        ) {
            EmailVerificationScreenRoot(
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo<AuthGraphRoutes.EmailVerification> {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onCloseClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo<AuthGraphRoutes.EmailVerification> {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<AuthGraphRoutes.ForgotPassword> {
            ForgotPasswordRoot()
        }
        composable<AuthGraphRoutes.ResetPassword>(
            deepLinks = listOf(
                navDeepLink {
                    this.uriPattern = "https://chirp.pl-coding.com/api/auth/reset-password?token={token}"
                },
                navDeepLink {
                    this.uriPattern = "chirp://chirp.pl-coding.com/api/auth/reset-password?token={token}"
                }
            )
        ) {
            ResetPasswordRoot()
        }
    }
}