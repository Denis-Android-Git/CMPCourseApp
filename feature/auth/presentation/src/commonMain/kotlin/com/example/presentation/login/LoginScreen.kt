package com.example.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.create_account
import cmpcourseapp.feature.auth.presentation.generated.resources.email
import cmpcourseapp.feature.auth.presentation.generated.resources.email_placeholder
import cmpcourseapp.feature.auth.presentation.generated.resources.forgot_password
import cmpcourseapp.feature.auth.presentation.generated.resources.login
import cmpcourseapp.feature.auth.presentation.generated.resources.password
import cmpcourseapp.feature.auth.presentation.generated.resources.welcome_back
import com.example.designsystem.components.brand.Logo
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle
import com.example.designsystem.components.layouts.MyAdaptiveFormLayout
import com.example.designsystem.components.layouts.SnackBarScaffold
import com.example.designsystem.components.textfields.MyPasswordTextField
import com.example.designsystem.components.textfields.MyTextField
import com.example.designsystem.theme.MyTheme
import com.example.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.eventsFlow) {
        when (it) {
            is LoginEvents.Success -> onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onAction = {
            when (it) {
                LoginAction.OnForgotPasswordClick -> onForgotPasswordClick()
                LoginAction.OnSignUpClick -> onCreateAccountClick()
                else -> Unit
            }
            viewModel.onAction(it)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    SnackBarScaffold {
        MyAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_back),
            errorText = state.error?.asString(),
            logo = {
                Logo()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            MyTextField(
                state = state.emailTextFieldState,
                placeholder = stringResource(Res.string.email_placeholder),
                keyboardType = KeyboardType.Email,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            MyPasswordTextField(
                state = state.passwordTextFieldState,
                placeholder = stringResource(Res.string.password),
                modifier = Modifier.fillMaxWidth(),
                isPasswordVisible = state.isPasswordVisible,
                onVisibilityClick = {
                    onAction(LoginAction.OnTogglePasswordVisibility)
                },
                title = stringResource(Res.string.password)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.forgot_password),
                modifier = Modifier.align(Alignment.End)
                    .clickable {
                        onAction(LoginAction.OnForgotPasswordClick)
                    },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(24.dp))

            MyButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
                enabled = state.canLogin,
                modifier = Modifier.fillMaxWidth(),
                loading = state.isLoggingIn
            )
            Spacer(modifier = Modifier.height(8.dp))
            MyButton(
                text = stringResource(Res.string.create_account),
                onClick = {
                    onAction(LoginAction.OnSignUpClick)
                },
                modifier = Modifier.fillMaxWidth(),
                style = MyButtonStyle.SECONDARY
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}