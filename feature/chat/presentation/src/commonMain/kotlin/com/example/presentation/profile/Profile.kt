package com.example.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.core.designsystem.generated.resources.Res
import cmpcourseapp.core.designsystem.generated.resources.upload_icon
import cmpcourseapp.feature.chat.presentation.generated.resources.cancel
import cmpcourseapp.feature.chat.presentation.generated.resources.contact_chirp_support_change_email
import cmpcourseapp.feature.chat.presentation.generated.resources.current_password
import cmpcourseapp.feature.chat.presentation.generated.resources.delete
import cmpcourseapp.feature.chat.presentation.generated.resources.delete_profile_picture
import cmpcourseapp.feature.chat.presentation.generated.resources.delete_profile_picture_desc
import cmpcourseapp.feature.chat.presentation.generated.resources.email
import cmpcourseapp.feature.chat.presentation.generated.resources.new_password
import cmpcourseapp.feature.chat.presentation.generated.resources.password
import cmpcourseapp.feature.chat.presentation.generated.resources.password_hint
import cmpcourseapp.feature.chat.presentation.generated.resources.profile_image
import cmpcourseapp.feature.chat.presentation.generated.resources.save
import cmpcourseapp.feature.chat.presentation.generated.resources.upload_image
import com.example.designsystem.components.avatar.AvatarPhoto
import com.example.designsystem.components.avatar.AvatarSize
import com.example.designsystem.components.brand.MyHorizontalDivider
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle
import com.example.designsystem.components.dialogs.AdaptiveDialog
import com.example.designsystem.components.dialogs.DeleteDialog
import com.example.designsystem.components.textfields.MyPasswordTextField
import com.example.designsystem.components.textfields.MyTextField
import com.example.designsystem.theme.MyTheme
import com.example.presentation.profile.components.ProfileHeaderSection
import com.example.presentation.profile.components.ProfileSection
import com.example.presentation.util.DeviceConfiguration
import com.example.presentation.util.clearFocusOnTap
import com.example.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot(
    viewModel: ProfileViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AdaptiveDialog(
        onDismissRequest = onDismiss
    ) {
        ProfileScreen(
            state = state,
            onAction = {
                when (it) {
                    is ProfileAction.OnDismiss -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(it)
            }
        )
    }
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    Column(
        modifier = Modifier.clearFocusOnTap().fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeaderSection(
            userName = state.userName,
            onCloseClick = {
                onAction(ProfileAction.OnDismiss)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 20.dp)
        )
        MyHorizontalDivider()
        ProfileSection(
            headerText = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.profile_image)
        ) {
            Row {
                AvatarPhoto(
                    displayText = state.userInitials,
                    size = AvatarSize.LARGE,
                    imageUrl = state.profilePicture,
                    onClick = {
                        onAction(ProfileAction.OnUploadImage)
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MyButton(
                        text = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.upload_image),
                        onClick = {
                            onAction(ProfileAction.OnUploadImage)
                        },
                        style = MyButtonStyle.SECONDARY,
                        enabled = !state.isUploadingImage && !state.isDeletingImage,
                        loading = state.isUploadingImage,
                        leadingIcon = {
                            Icon(
                                vectorResource(Res.drawable.upload_icon),
                                contentDescription = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.upload_image)
                            )
                        }
                    )
                    MyButton(
                        text = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.delete),
                        onClick = {
                            onAction(ProfileAction.OnDeletePicture)
                        },
                        style = MyButtonStyle.DELETE_SECONDARY,
                        enabled = !state.isUploadingImage && !state.isDeletingImage && state.profilePicture != null,
                        loading = state.isDeletingImage,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.upload_image)
                            )
                        }
                    )
                }
            }
            if (state.imageError != null) {
                Text(
                    text = state.imageError.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        MyHorizontalDivider()
        ProfileSection(
            headerText = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.email)
        ) {
            MyTextField(
                state = state.emailTextState,
                enabled = false,
                supportingText = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.contact_chirp_support_change_email)
            )
        }
        MyHorizontalDivider()
        ProfileSection(
            headerText = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.password)
        ) {
            MyPasswordTextField(
                state = state.currentPasswordTextState,
                isPasswordVisible = state.isCurrentPasswordVisible,
                onVisibilityClick = {
                    onAction(ProfileAction.OnToggleCurrentPasswordVisibility)
                },
                placeholder = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.current_password),
                isError = state.currentPasswordError != null,
                supportingText = state.currentPasswordError?.asString()
            )
            MyPasswordTextField(
                state = state.newPasswordTextState,
                isPasswordVisible = state.isNewPasswordVisible,
                onVisibilityClick = {
                    onAction(ProfileAction.OnToggleNewPasswordVisibility)
                },
                placeholder = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.new_password),
                isError = state.newPasswordError != null,
                supportingText = state.newPasswordError?.asString()
                    ?: stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.password_hint)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                MyButton(
                    text = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.cancel),
                    style = MyButtonStyle.SECONDARY,
                    onClick = {
                        onAction(ProfileAction.OnDismiss)
                    }
                )
                MyButton(
                    text = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.save),
                    onClick = {
                        onAction(ProfileAction.OnChangePasswordClick)
                    },
                    enabled = state.canChangePassword,
                    loading = state.isChangingPassword
                )
            }
        }
        val configuration = currentDeviceConfiguration()
        if (configuration in listOf(
                DeviceConfiguration.MOBILE_PORTRAIT,
                DeviceConfiguration.MOBILE_LANDSCAPE
            )
        ) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    if (state.showDeleteConfirmationDialog) {
        DeleteDialog(
            title = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.delete_profile_picture),
            description = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.delete_profile_picture_desc),
            confirmButtonText = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.delete),
            cancelButtonText = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.cancel),
            onConfirm = {
                onAction(ProfileAction.OnConfirmDeletePicture)
            },
            onCancel = {
                onAction(ProfileAction.OnDismissConfirmDeleteDialog)
            },
            onDismissRequest = {
                onAction(ProfileAction.OnDismissConfirmDeleteDialog)
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        ProfileScreen(
            state = ProfileState(),
            onAction = {}
        )
    }
}