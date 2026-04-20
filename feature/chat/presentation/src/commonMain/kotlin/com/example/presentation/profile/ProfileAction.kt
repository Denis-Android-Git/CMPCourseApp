package com.example.presentation.profile

sealed interface ProfileAction {
    data object OnDismiss : ProfileAction
    data object OnUploadImage : ProfileAction
    class OnImageSelected(val bytes: ByteArray, val mimeType: String?) : ProfileAction
    data object OnDeletePicture : ProfileAction
    data object OnConfirmDeletePicture : ProfileAction
    data object OnDismissConfirmDeleteDialog : ProfileAction
    data object OnToggleCurrentPasswordVisibility : ProfileAction
    data object OnToggleNewPasswordVisibility : ProfileAction
    data object OnChangePasswordClick : ProfileAction
}