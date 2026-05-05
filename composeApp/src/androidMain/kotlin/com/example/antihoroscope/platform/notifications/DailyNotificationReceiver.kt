package com.example.antihoroscope.platform.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.antihoroscope.MainActivity

class DailyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureNotificationChannel(notificationManager)

        notificationManager.notify(
            DAILY_NOTIFICATION_ID,
            buildDailyNotification(context),
        )
    }
}

private fun buildDailyNotification(context: Context): Notification {
    val openAppIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val contentIntent = PendingIntent.getActivity(
        context,
        DAILY_NOTIFICATION_REQUEST_CODE,
        openAppIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context, DAILY_NOTIFICATION_CHANNEL_ID)
    } else {
        @Suppress("DEPRECATION")
        Notification.Builder(context)
    }

    return builder
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Антигороскоп")
        .setContentText("Звёзды проснулись. Твоё странное предсказание уже готово.")
        .setContentIntent(contentIntent)
        .setAutoCancel(true)
        .build()
}

private fun ensureNotificationChannel(notificationManager: NotificationManager) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val channel = NotificationChannel(
        DAILY_NOTIFICATION_CHANNEL_ID,
        "Космические предсказания",
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = "Ежедневные антипредсказания"
    }

    notificationManager.createNotificationChannel(channel)
}

private const val DAILY_NOTIFICATION_ID = 1001
private const val DAILY_NOTIFICATION_CHANNEL_ID = "daily_antihoroscope"
