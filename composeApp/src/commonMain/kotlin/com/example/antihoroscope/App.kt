package com.example.antihoroscope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.antihoroscope.feature.home.HomePlaceholderScreen
import com.example.antihoroscope.feature.onboarding.OnboardingScreen
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
@Preview
fun App() {
    AntiHoroscopeTheme {
        var isOnboardingCompleted by remember { mutableStateOf(false) }
        var selectedZodiacSignId by remember { mutableStateOf<String?>(null) }

        if (isOnboardingCompleted) {
            HomePlaceholderScreen(selectedZodiacSignId = selectedZodiacSignId)
        } else {
            OnboardingScreen(
                onCompleted = { result ->
                    selectedZodiacSignId = result.zodiacSignId
                    isOnboardingCompleted = true
                },
            )
        }
    }
}
