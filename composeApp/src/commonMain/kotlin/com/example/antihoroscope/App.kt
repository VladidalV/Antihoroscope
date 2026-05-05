package com.example.antihoroscope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.antihoroscope.data.settings.rememberOnboardingSettingsStorage
import com.example.antihoroscope.domain.onboarding.CompleteOnboardingParams
import com.example.antihoroscope.domain.onboarding.CompleteOnboardingResult
import com.example.antihoroscope.domain.onboarding.CompleteOnboardingUseCase
import com.example.antihoroscope.domain.onboarding.ObserveOnboardingStatusUseCase
import com.example.antihoroscope.feature.home.HomePlaceholderScreen
import com.example.antihoroscope.feature.onboarding.OnboardingScreen
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
@Preview
fun App() {
    AntiHoroscopeTheme {
        val onboardingSettingsStorage = rememberOnboardingSettingsStorage()
        val observeOnboardingStatusUseCase = remember(onboardingSettingsStorage) {
            ObserveOnboardingStatusUseCase(onboardingSettingsStorage)
        }
        val completeOnboardingUseCase = remember(onboardingSettingsStorage) {
            CompleteOnboardingUseCase(onboardingSettingsStorage)
        }
        val onboardingStatus = remember(observeOnboardingStatusUseCase) {
            observeOnboardingStatusUseCase()
        }
        var isOnboardingCompleted by remember {
            mutableStateOf(onboardingStatus.isCompleted)
        }
        var selectedZodiacSignId by remember {
            mutableStateOf(onboardingStatus.zodiacSignId)
        }

        if (isOnboardingCompleted) {
            HomePlaceholderScreen(selectedZodiacSignId = selectedZodiacSignId)
        } else {
            OnboardingScreen(
                onCompleted = { result ->
                    val completionParams = CompleteOnboardingParams(
                        zodiacSignId = result.zodiacSignId,
                        notificationsEnabled = result.notificationsEnabled,
                        notificationHour = result.notificationTime.hour,
                        notificationMinute = result.notificationTime.minute,
                    )

                    when (val completionResult = completeOnboardingUseCase(completionParams)) {
                        is CompleteOnboardingResult.Success -> {
                            selectedZodiacSignId = completionResult.zodiacSignId
                            isOnboardingCompleted = true
                        }
                        CompleteOnboardingResult.Error.UnknownZodiacSign -> Unit
                    }
                },
            )
        }
    }
}
