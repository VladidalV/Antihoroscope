package com.example.antihoroscope.platform.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

private class IosDailyNotificationScheduler(
    private val notificationCenter: UNUserNotificationCenter = UNUserNotificationCenter.currentNotificationCenter(),
) : DailyNotificationScheduler {
    override fun scheduleDailyNotification(
        hour: Int,
        minute: Int,
    ): NotificationScheduleResult {
        val content = UNMutableNotificationContent().apply {
            setTitle("Антигороскоп")
            setBody("Звёзды проснулись. Твоё странное предсказание уже готово.")
        }
        val dateComponents = NSDateComponents().apply {
            this.hour = hour.toLong()
            this.minute = minute.toLong()
        }
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = dateComponents,
            repeats = true,
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = DAILY_NOTIFICATION_IDENTIFIER,
            content = content,
            trigger = trigger,
        )

        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf(DAILY_NOTIFICATION_IDENTIFIER),
        )
        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Failed to schedule daily notification: ${error.localizedDescription}")
            }
        }

        return NotificationScheduleResult.Scheduled
    }

    override fun cancelDailyNotification() {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf(DAILY_NOTIFICATION_IDENTIFIER),
        )
    }
}

@Composable
actual fun rememberDailyNotificationScheduler(): DailyNotificationScheduler {
    return remember { IosDailyNotificationScheduler() }
}

private const val DAILY_NOTIFICATION_IDENTIFIER = "daily_antihoroscope"
