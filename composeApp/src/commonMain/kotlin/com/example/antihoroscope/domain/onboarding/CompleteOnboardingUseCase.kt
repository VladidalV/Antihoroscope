package com.example.antihoroscope.domain.onboarding

import com.example.antihoroscope.data.settings.OnboardingSettingsStorage

class CompleteOnboardingUseCase(
    private val settingsStorage: OnboardingSettingsStorage,
) {
    operator fun invoke(params: CompleteOnboardingParams): CompleteOnboardingResult {
        val zodiacSignExists = params.zodiacSignId in SupportedZodiacSignIds
        if (!zodiacSignExists) {
            return CompleteOnboardingResult.Error.UnknownZodiacSign
        }

        settingsStorage.saveOnboardingCompleted(
            zodiacSignId = params.zodiacSignId,
            notificationsEnabled = params.notificationsEnabled,
            notificationHour = params.notificationHour,
            notificationMinute = params.notificationMinute,
        )

        return CompleteOnboardingResult.Success(
            zodiacSignId = params.zodiacSignId,
        )
    }
}

data class CompleteOnboardingParams(
    val zodiacSignId: String,
    val notificationsEnabled: Boolean,
    val notificationHour: Int,
    val notificationMinute: Int,
)

sealed interface CompleteOnboardingResult {
    data class Success(
        val zodiacSignId: String,
    ) : CompleteOnboardingResult

    sealed interface Error : CompleteOnboardingResult {
        data object UnknownZodiacSign : Error
    }
}

private val SupportedZodiacSignIds = setOf(
    "aries",
    "taurus",
    "gemini",
    "cancer",
    "leo",
    "virgo",
    "libra",
    "scorpio",
    "sagittarius",
    "capricorn",
    "aquarius",
    "pisces",
)
