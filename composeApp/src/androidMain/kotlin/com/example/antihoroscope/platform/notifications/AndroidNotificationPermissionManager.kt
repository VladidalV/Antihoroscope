package com.example.antihoroscope.platform.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private class AndroidNotificationPermissionManager(
    private val context: Context,
    private val launchPermissionRequest: () -> Unit,
) : NotificationPermissionManager {
    override fun getCurrentStatus(): NotificationPermissionStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return NotificationPermissionStatus.Granted
        }

        val isGranted = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

        return if (isGranted) {
            NotificationPermissionStatus.Granted
        } else {
            NotificationPermissionStatus.Unknown
        }
    }

    override fun requestPermission(
        onResult: (NotificationPermissionStatus) -> Unit,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onResult(NotificationPermissionStatus.Granted)
            return
        }

        if (getCurrentStatus() == NotificationPermissionStatus.Granted) {
            onResult(NotificationPermissionStatus.Granted)
            return
        }

        pendingPermissionResult = onResult
        launchPermissionRequest()
    }
}

private var pendingPermissionResult: ((NotificationPermissionStatus) -> Unit)? = null

@Composable
actual fun rememberNotificationPermissionManager(): NotificationPermissionManager {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        val status = if (isGranted) {
            NotificationPermissionStatus.Granted
        } else {
            NotificationPermissionStatus.Denied
        }
        pendingPermissionResult?.invoke(status)
        pendingPermissionResult = null
    }

    return remember(context, launcher) {
        AndroidNotificationPermissionManager(
            context = context.applicationContext,
            launchPermissionRequest = {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
        )
    }
}
