package com.example.antihoroscope.feature.onboarding

import com.example.antihoroscope.platform.notifications.NotificationPermissionStatus

sealed interface OnboardingIntent {
    data object NextClicked : OnboardingIntent
    data object BackClicked : OnboardingIntent
    data object SkipClicked : OnboardingIntent
    data class ZodiacSelected(val zodiacSignId: String) : OnboardingIntent
    data class NotificationToggleChanged(val enabled: Boolean) : OnboardingIntent
    data class NotificationTimeChanged(val time: NotificationTimeUiModel) : OnboardingIntent
    data object NotificationPermissionRequestStarted : OnboardingIntent
    data class NotificationPermissionRequestFinished(
        val status: NotificationPermissionStatus,
    ) : OnboardingIntent
    data object CompleteClicked : OnboardingIntent
}
