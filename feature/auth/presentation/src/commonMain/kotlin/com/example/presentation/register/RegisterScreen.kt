package com.example.presentation.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.email
import cmpcourseapp.feature.auth.presentation.generated.resources.email_placeholder
import cmpcourseapp.feature.auth.presentation.generated.resources.login
import cmpcourseapp.feature.auth.presentation.generated.resources.password
import cmpcourseapp.feature.auth.presentation.generated.resources.password_hint
import cmpcourseapp.feature.auth.presentation.generated.resources.register
import cmpcourseapp.feature.auth.presentation.generated.resources.username
import cmpcourseapp.feature.auth.presentation.generated.resources.username_hint
import cmpcourseapp.feature.auth.presentation.generated.resources.username_placeholder
import cmpcourseapp.feature.auth.presentation.generated.resources.welcome
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackState = remember { SnackbarHostState() }

    ObserveAsEvents(flow = viewModel.eventsFlow) { event ->
        when (event) {
            is RegisterEvent.Success -> {
                onRegisterSuccess(event.email)
            }

        }
    }

    RegisterScreen(
        state = state,
        onAction = {
            when (it) {
                RegisterAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(it)
        },
        snackbarHostState = snackState
    )
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    snackbarHostState: SnackbarHostState,
    onAction: (RegisterAction) -> Unit,
) {
    SnackBarScaffold(
        snackbarHostState = snackbarHostState
    ) {
        MyAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome),
            errorText = state.registerError?.asString(),
            logo = {
                Logo()
            },
        ) {
            MyTextField(
                state = state.userNameTextState,
                placeholder = stringResource(Res.string.username_placeholder),
                title = stringResource(Res.string.username),
                supportingText = state.userNameError?.asString() ?: stringResource(Res.string.username_hint),
                isError = state.userNameError != null,
                onFocusChanged = {
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MyTextField(
                state = state.emailTextState,
                placeholder = stringResource(Res.string.email_placeholder),
                title = stringResource(Res.string.email),
                keyboardType = KeyboardType.Email,
                supportingText = state.emailError?.asString(),
                isError = state.emailError != null,
                onFocusChanged = {
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MyPasswordTextField(
                state = state.passwordTextState,
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.password),
                supportingText = state.passwordError?.asString() ?: stringResource(Res.string.password_hint),
                isError = state.passwordError != null,
                onFocusChanged = {
                    onAction(RegisterAction.OnInputTextFocusGain)
                },
                isPasswordVisible = state.isPasswordVisible,
                onVisibilityClick = {
                    onAction(RegisterAction.OnPasswordVisibilityClick)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            MyButton(
                text = stringResource(Res.string.register),
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
                enabled = state.canRegister,
                loading = state.isRegistering,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            MyButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(RegisterAction.OnLoginClick)
                },
                style = MyButtonStyle.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}