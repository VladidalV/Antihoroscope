package com.example.antihoroscope.platform.notifications

import androidx.compose.runtime.Composable

interface NotificationPermissionManager {
    fun getCurrentStatus(): NotificationPermissionStatus

    fun requestPermission(
        onResult: (NotificationPermissionStatus) -> Unit,
    )
}

@Composable
expect fun rememberNotificationPermissionManager(): NotificationPermissionManager
