package com.example.presentation.forgot_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.email
import cmpcourseapp.feature.auth.presentation.generated.resources.email_placeholder
import cmpcourseapp.feature.auth.presentation.generated.resources.forgot_password
import cmpcourseapp.feature.auth.presentation.generated.resources.forgot_password_email_sent_successfully
import cmpcourseapp.feature.auth.presentation.generated.resources.submit
import com.example.designsystem.components.brand.Logo
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.layouts.MyAdaptiveFormLayout
import com.example.designsystem.components.layouts.SnackBarScaffold
import com.example.designsystem.components.textfields.MyTextField
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordRoot(
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
) {
    SnackBarScaffold {
        MyAdaptiveFormLayout(
            headerText = stringResource(Res.string.forgot_password),
            errorText = state.error?.asString(),
            logo = {
                Logo()
            }
        ) {
            MyTextField(
                state = state.emailTextFieldState,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.email_placeholder),
                title = stringResource(Res.string.email),
                isError = state.error != null,
                supportingText = state.error?.asString(),
                keyboardType = KeyboardType.Email,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            MyButton(
                text = stringResource(Res.string.submit),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(ForgotPasswordAction.OnSubmitClick)
                },
                enabled = state.canSubmit && !state.isLoading,
                loading = state.isLoading
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (state.isEmailSent) {
                Text(
                    text = stringResource(Res.string.forgot_password_email_sent_successfully),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.success,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        ForgotPasswordScreen(
            state = ForgotPasswordState(),
            onAction = {}
        )
    }
}