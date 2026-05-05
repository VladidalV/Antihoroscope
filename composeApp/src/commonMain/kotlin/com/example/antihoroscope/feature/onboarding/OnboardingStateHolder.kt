package com.example.antihoroscope.feature.onboarding

class OnboardingStateHolder(
    initialState: OnboardingState = OnboardingState(),
) {
    var state: OnboardingState = initialState
        private set

    fun dispatch(intent: OnboardingIntent): OnboardingEffect? {
        return when (intent) {
            OnboardingIntent.NextClicked -> moveNext()
            OnboardingIntent.BackClicked -> {
                state = state.copy(
                    currentStep = state.currentStep.previous(),
                    errorMessage = null,
                )
                null
            }
            OnboardingIntent.SkipClicked -> skip()
            is OnboardingIntent.ZodiacSelected -> {
                selectZodiac(intent.zodiacSignId)
                null
            }
            is OnboardingIntent.NotificationToggleChanged -> {
                state = state.copy(
                    notificationsEnabled = intent.enabled,
                    errorMessage = null,
                )
                null
            }
            is OnboardingIntent.NotificationTimeChanged -> {
                state = state.copy(
                    notificationTime = intent.time,
                    errorMessage = null,
                )
                null
            }
            OnboardingIntent.CompleteClicked -> complete()
        }
    }

    private fun moveNext(): OnboardingEffect? {
        return when (state.currentStep) {
            OnboardingStep.Welcome,
            OnboardingStep.Concept,
            -> {
                state = state.copy(
                    currentStep = state.currentStep.next(),
                    errorMessage = null,
                )
                null
            }
            OnboardingStep.Zodiac -> {
                if (state.selectedZodiacSignId == null) {
                    state = state.copy(errorMessage = SELECT_ZODIAC_ERROR)
                    null
                } else {
                    state = state.copy(
                        currentStep = OnboardingStep.Notifications,
                        errorMessage = null,
                    )
                    null
                }
            }
            OnboardingStep.Notifications -> complete()
        }
    }

    private fun skip(): OnboardingEffect? {
        return when (state.currentStep) {
            OnboardingStep.Welcome,
            OnboardingStep.Concept,
            -> {
                state = state.copy(
                    currentStep = OnboardingStep.Zodiac,
                    errorMessage = null,
                )
                null
            }
            OnboardingStep.Zodiac -> {
                null
            }
            OnboardingStep.Notifications -> {
                state = state.copy(
                    notificationsEnabled = false,
                    errorMessage = null,
                )
                complete()
            }
        }
    }

    private fun selectZodiac(zodiacSignId: String) {
        val exists = state.zodiacSigns.any { it.id == zodiacSignId }
        state = if (exists) {
            state.copy(
                selectedZodiacSignId = zodiacSignId,
                errorMessage = null,
            )
        } else {
            state.copy(errorMessage = UNKNOWN_ZODIAC_ERROR)
        }
    }

    private fun complete(): OnboardingEffect? {
        val selectedZodiacSignId = state.selectedZodiacSignId
        if (selectedZodiacSignId == null) {
            state = state.copy(errorMessage = SELECT_ZODIAC_ERROR)
            return null
        }

        state = state.copy(
            isCompleting = false,
            errorMessage = null,
        )

        return OnboardingEffect.Completed(
            result = OnboardingCompletionResult(
                zodiacSignId = selectedZodiacSignId,
                notificationsEnabled = state.notificationsEnabled,
                notificationTime = state.notificationTime,
            ),
        )
    }

    private fun OnboardingStep.next(): OnboardingStep {
        return when (this) {
            OnboardingStep.Welcome -> OnboardingStep.Concept
            OnboardingStep.Concept -> OnboardingStep.Zodiac
            OnboardingStep.Zodiac -> OnboardingStep.Notifications
            OnboardingStep.Notifications -> OnboardingStep.Notifications
        }
    }

    private fun OnboardingStep.previous(): OnboardingStep {
        return when (this) {
            OnboardingStep.Welcome -> OnboardingStep.Welcome
            OnboardingStep.Concept -> OnboardingStep.Welcome
            OnboardingStep.Zodiac -> OnboardingStep.Concept
            OnboardingStep.Notifications -> OnboardingStep.Zodiac
        }
    }

    private companion object {
        const val SELECT_ZODIAC_ERROR = "Сначала выбери знак. Космос без этого не справится."
        const val UNKNOWN_ZODIAC_ERROR = "Такого знака зодиака космос пока не признаёт."
    }
}

sealed interface OnboardingEffect {
    data class Completed(
        val result: OnboardingCompletionResult,
    ) : OnboardingEffect
}

data class OnboardingCompletionResult(
    val zodiacSignId: String,
    val notificationsEnabled: Boolean,
    val notificationTime: NotificationTimeUiModel,
)
