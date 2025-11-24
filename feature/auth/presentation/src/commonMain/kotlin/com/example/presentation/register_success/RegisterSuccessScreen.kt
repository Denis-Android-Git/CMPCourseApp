package com.example.presentation.register_success

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.account_successfully_created
import cmpcourseapp.feature.auth.presentation.generated.resources.login
import cmpcourseapp.feature.auth.presentation.generated.resources.resend_verification_email
import cmpcourseapp.feature.auth.presentation.generated.resources.resent_verification_email
import cmpcourseapp.feature.auth.presentation.generated.resources.verification_email_sent_to_x
import com.example.designsystem.components.brand.SuccessIcon
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle
import com.example.designsystem.components.layouts.AdaptiveResultLayout
import com.example.designsystem.components.layouts.SimpleResultLayout
import com.example.designsystem.components.layouts.SnackBarScaffold
import com.example.designsystem.theme.MyTheme
import com.example.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterSuccessRoot(
    viewModel: RegisterSuccessViewModel = koinViewModel(),
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackState = remember { SnackbarHostState() }
    ObserveAsEvents(viewModel.eventsFlow) { event ->
        when (event) {
            is RegisterSuccessEvent.ResendEmailSuccess -> {
                snackState.showSnackbar(
                    message = getString(
                        Res.string.resent_verification_email
                    )
                )
            }
        }
    }
    RegisterSuccessScreen(
        state = state,
        onAction = {
            when (it) {
                RegisterSuccessAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(it)
        },
        snackbarHostState = snackState
    )
}

@Composable
fun RegisterSuccessScreen(
    state: RegisterSuccessState,
    onAction: (RegisterSuccessAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    SnackBarScaffold(
        snackbarHostState = snackbarHostState
    ) {
        AdaptiveResultLayout {
            SimpleResultLayout(
                title = stringResource(Res.string.account_successfully_created),
                description = stringResource(Res.string.verification_email_sent_to_x, state.registeredEmail),
                icon = {
                    SuccessIcon()
                },
                primaryButton = {
                    MyButton(
                        text = stringResource(Res.string.login),
                        onClick = { onAction(RegisterSuccessAction.OnLoginClick) },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                secondaryButton = {
                    MyButton(
                        text = stringResource(Res.string.resend_verification_email),
                        onClick = { onAction(RegisterSuccessAction.OnResendVerificationEmailClick) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isResendingEmail,
                        loading = state.isResendingEmail,
                        style = MyButtonStyle.SECONDARY
                    )
                },
                secondaryError = state.resendVarificationError?.asString(),
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        RegisterSuccessScreen(
            state = RegisterSuccessState(
                registeredEmail = "example@example.com"
            ),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}