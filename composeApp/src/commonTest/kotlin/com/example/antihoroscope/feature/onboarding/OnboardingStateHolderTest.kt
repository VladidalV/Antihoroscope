package com.example.antihoroscope.feature.onboarding

import com.example.antihoroscope.platform.notifications.NotificationPermissionStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OnboardingStateHolderTest {
    @Test
    fun startsOnWelcomeStep() {
        val stateHolder = OnboardingStateHolder()

        assertEquals(OnboardingStep.Welcome, stateHolder.state.currentStep)
        assertFalse(stateHolder.state.canGoBack)
        assertTrue(stateHolder.state.canSkip)
        assertTrue(stateHolder.state.canContinue)
    }

    @Test
    fun nextMovesWelcomeToConceptAndConceptToZodiac() {
        val stateHolder = OnboardingStateHolder()

        stateHolder.dispatch(OnboardingIntent.NextClicked)
        assertEquals(OnboardingStep.Concept, stateHolder.state.currentStep)

        stateHolder.dispatch(OnboardingIntent.NextClicked)
        assertEquals(OnboardingStep.Zodiac, stateHolder.state.currentStep)
    }

    @Test
    fun backMovesToPreviousStep() {
        val stateHolder = OnboardingStateHolder(
            initialState = OnboardingState(currentStep = OnboardingStep.Notifications),
        )

        stateHolder.dispatch(OnboardingIntent.BackClicked)
        assertEquals(OnboardingStep.Zodiac, stateHolder.state.currentStep)

        stateHolder.dispatch(OnboardingIntent.BackClicked)
        assertEquals(OnboardingStep.Concept, stateHolder.state.currentStep)
    }

    @Test
    fun skipFromWelcomeOrConceptMovesToZodiac() {
        val fromWelcome = OnboardingStateHolder()
        fromWelcome.dispatch(OnboardingIntent.SkipClicked)
        assertEquals(OnboardingStep.Zodiac, fromWelcome.state.currentStep)

        val fromConcept = OnboardingStateHolder(
            initialState = OnboardingState(currentStep = OnboardingStep.Concept),
        )
        fromConcept.dispatch(OnboardingIntent.SkipClicked)
        assertEquals(OnboardingStep.Zodiac, fromConcept.state.currentStep)
    }

    @Test
    fun skipOnZodiacIsIgnored() {
        val stateHolder = OnboardingStateHolder(
            initialState = OnboardingState(currentStep = OnboardingStep.Zodiac),
        )

        val effect = stateHolder.dispatch(OnboardingIntent.SkipClicked)

        assertNull(effect)
        assertEquals(OnboardingStep.Zodiac, stateHolder.state.currentStep)
        assertNull(stateHolder.state.selectedZodiacSignId)
    }

    @Test
    fun nextOnZodiacWithoutSelectedSignDoesNotContinue() {
        val stateHolder = OnboardingStateHolder(
            initialState = OnboardingState(currentStep = OnboardingStep.Zodiac),
        )

        val effect = stateHolder.dispatch(OnboardingIntent.NextClicked)

        assertNull(effect)
        assertEquals(OnboardingStep.Zodiac, stateHolder.state.currentStep)
        assertNotNull(stateHolder.state.errorMessage)
    }

    @Test
    fun selectedZodiacAllowsMovingToNotifications() {
        val stateHolder = OnboardingStateHolder(
            initialState = OnboardingState(currentStep = OnboardingStep.Zodiac),
        )

        stateHolder.dispatch(OnboardingIntent.ZodiacSelected("aries"))
        assertEquals("aries", stateHolder.state.selectedZodiacSignId)
        assertTrue(stateHolder.state.canContinue)

        stateHolder.dispatch(OnboardingIntent.NextClicked)
        assertEquals(OnboardingStep.Notifications, stateHolder.state.currentStep)
    }

    @Test
    fun unknownZodiacIsRejected() {
        val stateHolder = OnboardingStateHolder(
            initialState = OnboardingState(currentStep = OnboardingStep.Zodiac),
        )

        stateHolder.dispatch(OnboardingIntent.ZodiacSelected("ophiuchus"))

        assertNull(stateHolder.state.selectedZodiacSignId)
        assertNotNull(stateHolder.state.errorMessage)
    }

    @Test
    fun completeReturnsCompletedEffectWithSelectedSettings() {
        val stateHolder = OnboardingStateHolder(
            initialState = OnboardingState(
                currentStep = OnboardingStep.Notifications,
                selectedZodiacSignId = "leo",
                notificationsEnabled = true,
                notificationTime = NotificationTimeUiModel(hour = 20, minute = 0),
            ),
        )

        val effect = stateHolder.dispatch(OnboardingIntent.CompleteClicked)

        val completed = assertIs<OnboardingEffect.Completed>(effect)
        assertEquals("leo", completed.result.zodiacSignId)
        assertTrue(completed.result.notificationsEnabled)
        assertEquals(NotificationTimeUiModel(hour = 20, minute = 0), completed.result.notificationTime)
    }

    @Test
    fun permissionDeniedDisablesNotificationsAndStopsProgressState() {
        val stateHolder = OnboardingStateHolder(
            initialState = OnboardingState(
                currentStep = OnboardingStep.Notifications,
                notificationsEnabled = true,
            ),
        )

        stateHolder.dispatch(OnboardingIntent.NotificationPermissionRequestStarted)
        assertTrue(stateHolder.state.isNotificationPermissionRequestInProgress)

        stateHolder.dispatch(
            OnboardingIntent.NotificationPermissionRequestFinished(NotificationPermissionStatus.Denied),
        )

        assertEquals(NotificationPermissionStatus.Denied, stateHolder.state.notificationPermissionStatus)
        assertFalse(stateHolder.state.notificationsEnabled)
        assertFalse(stateHolder.state.isNotificationPermissionRequestInProgress)
    }

    @Test
    fun permissionGrantedKeepsNotificationsEnabled() {
        val stateHolder = OnboardingStateHolder(
            initialState = OnboardingState(
                currentStep = OnboardingStep.Notifications,
                notificationsEnabled = true,
            ),
        )

        stateHolder.dispatch(
            OnboardingIntent.NotificationPermissionRequestFinished(NotificationPermissionStatus.Granted),
        )

        assertEquals(NotificationPermissionStatus.Granted, stateHolder.state.notificationPermissionStatus)
        assertTrue(stateHolder.state.notificationsEnabled)
    }
}
