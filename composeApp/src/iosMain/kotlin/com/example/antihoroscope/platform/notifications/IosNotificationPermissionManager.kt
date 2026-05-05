package com.example.antihoroscope.platform.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNUserNotificationCenter

private class IosNotificationPermissionManager(
    private val notificationCenter: UNUserNotificationCenter = UNUserNotificationCenter.currentNotificationCenter(),
) : NotificationPermissionManager {
    override fun getCurrentStatus(): NotificationPermissionStatus {
        return NotificationPermissionStatus.Unknown
    }

    override fun requestPermission(
        onResult: (NotificationPermissionStatus) -> Unit,
    ) {
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            when (settings?.authorizationStatus) {
                UNAuthorizationStatusAuthorized -> onResult(NotificationPermissionStatus.Granted)
                UNAuthorizationStatusDenied -> onResult(NotificationPermissionStatus.Denied)
                UNAuthorizationStatusNotDetermined -> requestSystemPermission(onResult)
                else -> onResult(NotificationPermissionStatus.Unknown)
            }
        }
    }

    private fun requestSystemPermission(
        onResult: (NotificationPermissionStatus) -> Unit,
    ) {
        notificationCenter.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
        ) { granted, _ ->
            onResult(
                if (granted) {
                    NotificationPermissionStatus.Granted
                } else {
                    NotificationPermissionStatus.Denied
                },
            )
        }
    }
}

@Composable
actual fun rememberNotificationPermissionManager(): NotificationPermissionManager {
    return remember { IosNotificationPermissionManager() }
}
