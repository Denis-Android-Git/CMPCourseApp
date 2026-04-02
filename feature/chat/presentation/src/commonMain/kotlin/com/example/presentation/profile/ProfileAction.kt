package com.example.presentation.profile

sealed interface ProfileAction {
    data object OnDismiss : ProfileAction
    data object OnUploadImage : ProfileAction
    data object OnErrorImagePicker : ProfileAction
    data class OnUriSelected(val uri: String) : ProfileAction
    class OnImageSelected(val bytes: ByteArray) : ProfileAction
    data object OnDeletePicture : ProfileAction
    data object OnConfirmDeletePicture : ProfileAction
    data object OnDismissConfirmDeleteDialog : ProfileAction
    data object OnToggleCurrentPasswordVisibility : ProfileAction
    data object OnToggleNewPasswordVisibility : ProfileAction
    data object OnChangePasswordClick : ProfileAction
}