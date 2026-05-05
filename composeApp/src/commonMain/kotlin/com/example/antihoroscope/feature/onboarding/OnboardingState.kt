package com.example.antihoroscope.feature.onboarding

data class OnboardingState(
    val currentStep: OnboardingStep = OnboardingStep.Welcome,
    val zodiacSigns: List<ZodiacSignUiModel> = ZodiacSignUiModel.all,
    val selectedZodiacSignId: String? = null,
    val notificationsEnabled: Boolean = false,
    val notificationTime: NotificationTimeUiModel = NotificationTimeUiModel.Default,
    val isCompleting: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedZodiacSign: ZodiacSignUiModel?
        get() = zodiacSigns.firstOrNull { it.id == selectedZodiacSignId }

    val canGoBack: Boolean
        get() = currentStep != OnboardingStep.Welcome

    val canSkip: Boolean
        get() = currentStep != OnboardingStep.Zodiac

    val canContinue: Boolean
        get() = currentStep != OnboardingStep.Zodiac || selectedZodiacSignId != null

    val isLastStep: Boolean
        get() = currentStep == OnboardingStep.Notifications
}

data class NotificationTimeUiModel(
    val hour: Int,
    val minute: Int,
) {
    init {
        require(hour in 0..23) { "Hour must be in 0..23." }
        require(minute in 0..59) { "Minute must be in 0..59." }
    }

    val label: String
        get() = "${hour.twoDigits()}:${minute.twoDigits()}"

    companion object {
        val Default = NotificationTimeUiModel(hour = 9, minute = 0)

        val presets = listOf(
            NotificationTimeUiModel(hour = 9, minute = 0),
            NotificationTimeUiModel(hour = 13, minute = 0),
            NotificationTimeUiModel(hour = 20, minute = 0),
        )
    }
}

private fun Int.twoDigits(): String = if (this < 10) "0$this" else toString()
