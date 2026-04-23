package com.example.presentation.permissions

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION

actual class PermissionController(
    private val permissionsController: PermissionsController
) {
    actual suspend fun requestPermission(customPermission: CustomPermission): CustomPermissionState {
        return try {
            permissionsController.providePermission(customPermission.toMokoPermission())
            CustomPermissionState.GRANTED
        } catch (_: DeniedAlwaysException) {
            CustomPermissionState.DENIED_FOREVER

        } catch (_: DeniedException) {
            CustomPermissionState.DENIED

        } catch (_: RequestCanceledException) {
            CustomPermissionState.DENIED
        }
    }
}

fun CustomPermission.toMokoPermission(): Permission {
    return when (this) {
        CustomPermission.NOTIFICATIONS -> Permission.REMOTE_NOTIFICATION
    }
}