package com.example.antihoroscope.domain.onboarding

import com.example.antihoroscope.data.settings.OnboardingSettingsStorage

class ObserveOnboardingStatusUseCase(
    private val settingsStorage: OnboardingSettingsStorage,
) {
    operator fun invoke(): OnboardingStatus {
        val snapshot = settingsStorage.getSnapshot()
        return OnboardingStatus(
            isCompleted = snapshot.onboardingCompleted && snapshot.zodiacSignId != null,
            zodiacSignId = snapshot.zodiacSignId,
        )
    }
}
