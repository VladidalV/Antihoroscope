package com.example.antihoroscope.feature.onboarding

import com.example.antihoroscope.core.analytics.AnalyticsTracker
import com.example.antihoroscope.platform.notifications.NotificationPermissionStatus

class OnboardingAnalytics(
    private val analyticsTracker: AnalyticsTracker,
) {
    fun trackStarted() {
        analyticsTracker.track("onboarding_started")
    }

    fun trackStepViewed(step: OnboardingStep) {
        analyticsTracker.track(
            eventName = "onboarding_step_viewed",
            params = mapOf(
                "step" to step.analyticsName,
                "step_index" to step.index.toString(),
            ),
        )
    }

    fun trackNextClicked(fromStep: OnboardingStep) {
        analyticsTracker.track(
            eventName = "onboarding_next_clicked",
            params = mapOf("from_step" to fromStep.analyticsName),
        )
    }

    fun trackBackClicked(fromStep: OnboardingStep) {
        analyticsTracker.track(
            eventName = "onboarding_back_clicked",
            params = mapOf("from_step" to fromStep.analyticsName),
        )
    }

    fun trackSkipped(atStep: OnboardingStep) {
        analyticsTracker.track(
            eventName = "onboarding_skipped",
            params = mapOf("at_step" to atStep.analyticsName),
        )
    }

    fun trackZodiacSelected(zodiacSignId: String) {
        analyticsTracker.track(
            eventName = "zodiac_selected",
            params = mapOf("zodiac_sign" to zodiacSignId),
        )
    }

    fun trackNotificationPermissionRequested() {
        analyticsTracker.track("notification_permission_requested")
    }

    fun trackNotificationPermissionResult(status: NotificationPermissionStatus) {
        val eventName = when (status) {
            NotificationPermissionStatus.Granted -> "notification_permission_granted"
            NotificationPermissionStatus.Denied -> "notification_permission_denied"
            NotificationPermissionStatus.Unknown -> "notification_permission_unknown"
            NotificationPermissionStatus.NotAvailable -> "notification_permission_not_available"
        }
        analyticsTracker.track(eventName)
    }

    fun trackNotificationTimeSelected(time: NotificationTimeUiModel) {
        analyticsTracker.track(
            eventName = "notification_time_selected",
            params = mapOf("time" to time.label),
        )
    }

    fun trackCompleted(result: OnboardingCompletionResult) {
        analyticsTracker.track(
            eventName = "onboarding_completed",
            params = mapOf(
                "zodiac_sign" to result.zodiacSignId,
                "notifications_enabled" to result.notificationsEnabled.toString(),
                "steps_completed" to OnboardingStep.totalSteps.toString(),
            ),
        )
    }
}

private val OnboardingStep.analyticsName: String
    get() = when (this) {
        OnboardingStep.Welcome -> "welcome"
        OnboardingStep.Concept -> "concept"
        OnboardingStep.Zodiac -> "zodiac"
        OnboardingStep.Notifications -> "notifications"
    }
