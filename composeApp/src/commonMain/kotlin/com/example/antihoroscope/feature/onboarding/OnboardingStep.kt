package com.example.antihoroscope.feature.onboarding

enum class OnboardingStep(
    val index: Int,
) {
    Welcome(index = 0),
    Concept(index = 1),
    Zodiac(index = 2),
    Notifications(index = 3);

    companion object {
        val totalSteps: Int = entries.size
    }
}
