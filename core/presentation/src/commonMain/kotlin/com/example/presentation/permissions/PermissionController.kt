package com.example.presentation.permissions

expect class PermissionController {
    suspend fun requestPermission(customPermission: CustomPermission): CustomPermissionState
}