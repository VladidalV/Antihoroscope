package com.example.antihoroscope.feature.onboarding

import androidx.lifecycle.ViewModel
import com.example.antihoroscope.core.analytics.NoOpAnalyticsTracker
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel(
    private val stateHolder: OnboardingStateHolder = OnboardingStateHolder(),
    private val analytics: OnboardingAnalytics = OnboardingAnalytics(NoOpAnalyticsTracker),
) : ViewModel() {
    private val _state = MutableStateFlow(stateHolder.state)
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    private var lastTrackedStep: OnboardingStep = stateHolder.state.currentStep

    init {
        analytics.trackStarted()
        analytics.trackStepViewed(stateHolder.state.currentStep)
    }

    fun onIntent(intent: OnboardingIntent) {
        val previousStep = stateHolder.state.currentStep
        trackIntent(intent = intent, fromStep = previousStep)

        val effect = stateHolder.dispatch(intent)
        _state.value = stateHolder.state

        trackStepIfChanged()

        when (effect) {
            is OnboardingEffect.Completed -> {
                analytics.trackCompleted(effect.result)
                _events.tryEmit(OnboardingEvent.Completed(effect.result))
            }
            null -> Unit
        }
    }

    private fun trackIntent(
        intent: OnboardingIntent,
        fromStep: OnboardingStep,
    ) {
        when (intent) {
            OnboardingIntent.NextClicked -> analytics.trackNextClicked(fromStep)
            OnboardingIntent.BackClicked -> analytics.trackBackClicked(fromStep)
            OnboardingIntent.SkipClicked -> analytics.trackSkipped(fromStep)
            is OnboardingIntent.ZodiacSelected -> analytics.trackZodiacSelected(intent.zodiacSignId)
            is OnboardingIntent.NotificationTimeChanged -> analytics.trackNotificationTimeSelected(intent.time)
            OnboardingIntent.NotificationPermissionRequestStarted -> {
                analytics.trackNotificationPermissionRequested()
            }
            is OnboardingIntent.NotificationPermissionRequestFinished -> {
                analytics.trackNotificationPermissionResult(intent.status)
            }
            is OnboardingIntent.NotificationToggleChanged,
            OnboardingIntent.CompleteClicked,
            -> Unit
        }
    }

    private fun trackStepIfChanged() {
        val currentStep = stateHolder.state.currentStep
        if (currentStep != lastTrackedStep) {
            analytics.trackStepViewed(currentStep)
            lastTrackedStep = currentStep
        }
    }
}
