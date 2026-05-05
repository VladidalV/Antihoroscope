package com.example.antihoroscope.data.settings

interface OnboardingSettingsStorage {
    fun getSnapshot(): OnboardingSettingsSnapshot

    fun saveOnboardingCompleted(
        zodiacSignId: String,
        notificationsEnabled: Boolean,
        notificationHour: Int,
        notificationMinute: Int,
    )
}
