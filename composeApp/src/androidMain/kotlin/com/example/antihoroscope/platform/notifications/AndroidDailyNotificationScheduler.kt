package com.example.antihoroscope.platform.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

private class AndroidDailyNotificationScheduler(
    private val context: Context,
) : DailyNotificationScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleDailyNotification(
        hour: Int,
        minute: Int,
    ): NotificationScheduleResult {
        return runCatching {
            val triggerAtMillis = nextTriggerAtMillis(hour = hour, minute = minute)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                AlarmManager.INTERVAL_DAY,
                notificationPendingIntent(context),
            )
            NotificationScheduleResult.Scheduled
        }.getOrElse { error ->
            NotificationScheduleResult.Error(error.message)
        }
    }

    override fun cancelDailyNotification() {
        alarmManager.cancel(notificationPendingIntent(context))
    }
}

@Composable
actual fun rememberDailyNotificationScheduler(): DailyNotificationScheduler {
    val context = LocalContext.current.applicationContext
    return remember(context) { AndroidDailyNotificationScheduler(context) }
}

private fun nextTriggerAtMillis(
    hour: Int,
    minute: Int,
): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return calendar.timeInMillis
}

internal fun notificationPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, DailyNotificationReceiver::class.java)
    return PendingIntent.getBroadcast(
        context,
        DAILY_NOTIFICATION_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}

internal const val DAILY_NOTIFICATION_REQUEST_CODE = 4001
