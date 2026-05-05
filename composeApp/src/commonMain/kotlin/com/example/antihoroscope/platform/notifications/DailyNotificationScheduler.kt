package com.example.antihoroscope.platform.notifications

import androidx.compose.runtime.Composable

interface DailyNotificationScheduler {
    fun scheduleDailyNotification(
        hour: Int,
        minute: Int,
    ): NotificationScheduleResult

    fun cancelDailyNotification()
}

sealed interface NotificationScheduleResult {
    data object Scheduled : NotificationScheduleResult
    data object NotAvailable : NotificationScheduleResult
    data class Error(
        val message: String?,
    ) : NotificationScheduleResult
}

@Composable
expect fun rememberDailyNotificationScheduler(): DailyNotificationScheduler
