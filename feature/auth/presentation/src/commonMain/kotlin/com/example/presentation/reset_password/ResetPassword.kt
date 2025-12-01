package com.example.presentation.reset_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.password
import cmpcourseapp.feature.auth.presentation.generated.resources.password_hint
import cmpcourseapp.feature.auth.presentation.generated.resources.reset_password_successfully
import cmpcourseapp.feature.auth.presentation.generated.resources.set_new_password
import cmpcourseapp.feature.auth.presentation.generated.resources.submit
import com.example.designsystem.components.brand.Logo
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.layouts.MyAdaptiveFormLayout
import com.example.designsystem.components.textfields.MyPasswordTextField
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordRoot(
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ResetPasswordScreen(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
) {
    MyAdaptiveFormLayout(
        headerText = stringResource(Res.string.set_new_password),
        errorText = state.error?.asString(),
        logo = {
            Logo()
        },
    ) {
        MyPasswordTextField(
            state = state.passwordState,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.password),
            title = stringResource(Res.string.password),
            supportingText = stringResource(Res.string.password_hint),
            isPasswordVisible = state.isPasswordVisible,
            onVisibilityClick = {
                onAction(ResetPasswordAction.OnVisibilityClick)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        MyButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.submit),
            onClick = {
                onAction(ResetPasswordAction.OnSubmitClick)
            },
            enabled = !state.isLoading && state.canSubmit,
            loading = state.isLoading
        )
        if (state.isSuccess) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.reset_password_successfully),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.success,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        ResetPasswordScreen(
            state = ResetPasswordState(),
            onAction = {}
        )
    }
}