package com.example.antihoroscope.data.settings

data class OnboardingSettingsSnapshot(
    val onboardingCompleted: Boolean = false,
    val zodiacSignId: String? = null,
    val notificationsEnabled: Boolean = false,
    val notificationHour: Int = DEFAULT_NOTIFICATION_HOUR,
    val notificationMinute: Int = DEFAULT_NOTIFICATION_MINUTE,
)

const val DEFAULT_NOTIFICATION_HOUR = 9
const val DEFAULT_NOTIFICATION_MINUTE = 0
