package com.example.presentation.email_verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.close
import cmpcourseapp.feature.auth.presentation.generated.resources.email_verified_failed
import cmpcourseapp.feature.auth.presentation.generated.resources.email_verified_failed_desc
import cmpcourseapp.feature.auth.presentation.generated.resources.email_verified_successfully
import cmpcourseapp.feature.auth.presentation.generated.resources.email_verified_successfully_desc
import cmpcourseapp.feature.auth.presentation.generated.resources.login
import cmpcourseapp.feature.auth.presentation.generated.resources.verifying_account
import com.example.designsystem.components.brand.FailureIcon
import com.example.designsystem.components.brand.SuccessIcon
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle
import com.example.designsystem.components.layouts.AdaptiveResultLayout
import com.example.designsystem.components.layouts.SimpleResultLayout
import com.example.designsystem.components.layouts.SnackBarScaffold
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailVerificationScreenRoot(
    viewModel: EmailVerificationScreenViewModel = koinViewModel(),
    onLoginClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EmailVerificationScreen(
        state = state,
        onAction = {
            when (it) {
                EmailVerificationScreenAction.OnLoginClick -> onLoginClick()
                EmailVerificationScreenAction.OnCloseClick -> onCloseClick()
            }
            viewModel.onAction(it)
        }
    )
}

@Composable
fun EmailVerificationScreen(
    state: EmailVerificationScreenState,
    onAction: (EmailVerificationScreenAction) -> Unit,
) {
    SnackBarScaffold {
        AdaptiveResultLayout {
            when {
                state.isVarifying -> VerifyContent(
                    modifier = Modifier.fillMaxWidth()
                )

                state.isEmailVerified -> {
                    SimpleResultLayout(
                        title = stringResource(Res.string.email_verified_successfully),
                        description = stringResource(Res.string.email_verified_successfully_desc),
                        icon = {
                            SuccessIcon()
                        },
                        primaryButton = {
                            MyButton(
                                text = stringResource(Res.string.login),
                                onClick = { onAction(EmailVerificationScreenAction.OnLoginClick) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                else -> {
                    SimpleResultLayout(
                        title = stringResource(Res.string.email_verified_failed),
                        description = stringResource(Res.string.email_verified_failed_desc),
                        icon = {
                            Spacer(modifier = Modifier.height(32.dp))
                            FailureIcon(
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        },
                        primaryButton = {
                            MyButton(
                                text = stringResource(Res.string.close),
                                onClick = { onAction(EmailVerificationScreenAction.OnCloseClick) },
                                modifier = Modifier.fillMaxWidth(),
                                style = MyButtonStyle.SECONDARY
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VerifyContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.heightIn(min = 200.dp).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp), color = MaterialTheme.colorScheme.primary)
        Text(
            text = stringResource(Res.string.verifying_account),
            color = MaterialTheme.colorScheme.extended.textSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        EmailVerificationScreen(
            state = EmailVerificationScreenState(
                isEmailVerified = true
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun PreviewProgress() {
    MyTheme {
        EmailVerificationScreen(
            state = EmailVerificationScreenState(
                isVarifying = true
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun PreviewFail() {
    MyTheme {
        EmailVerificationScreen(
            state = EmailVerificationScreenState(),
            onAction = {}
        )
    }
}