package com.example.antihoroscope.feature.onboarding

sealed interface OnboardingEvent {
    data class Completed(
        val result: OnboardingCompletionResult,
    ) : OnboardingEvent
}
