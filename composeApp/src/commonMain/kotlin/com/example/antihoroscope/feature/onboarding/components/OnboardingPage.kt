package com.example.antihoroscope.feature.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.feature.onboarding.OnboardingStep
import com.example.antihoroscope.ui.components.CosmicBackground
import com.example.antihoroscope.ui.components.CosmicButton
import com.example.antihoroscope.ui.components.CosmicTextButton
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun OnboardingPage(
    currentStep: OnboardingStep,
    title: String,
    description: String,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryButtonEnabled: Boolean = true,
    canGoBack: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    visual: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
) {
    CosmicBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 24.dp, vertical = 18.dp),
        ) {
            OnboardingTopBar(
                currentStep = currentStep,
                canGoBack = canGoBack,
                onBackClick = onBackClick,
                secondaryButtonText = secondaryButtonText,
                onSecondaryClick = onSecondaryClick,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                if (visual != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 144.dp, max = 220.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        visual()
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                }

                Text(
                    text = title,
                    color = AntiHoroscopeTheme.colors.starWhite,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = description,
                    color = AntiHoroscopeTheme.colors.moonMuted,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )

                if (content != null) {
                    Spacer(modifier = Modifier.height(28.dp))
                    content()
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            CosmicButton(
                text = primaryButtonText,
                onClick = onPrimaryClick,
                enabled = primaryButtonEnabled,
            )
        }
    }
}

@Composable
private fun OnboardingTopBar(
    currentStep: OnboardingStep,
    canGoBack: Boolean,
    onBackClick: (() -> Unit)?,
    secondaryButtonText: String?,
    onSecondaryClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (canGoBack && onBackClick != null) {
            CosmicTextButton(
                text = "Назад",
                onClick = onBackClick,
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 12.dp),
            )
        } else {
            Spacer(modifier = Modifier.width(58.dp))
        }

        OnboardingProgressDots(currentStep = currentStep)

        if (secondaryButtonText != null && onSecondaryClick != null) {
            CosmicTextButton(
                text = secondaryButtonText,
                onClick = onSecondaryClick,
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 12.dp),
            )
        } else {
            Spacer(modifier = Modifier.width(58.dp))
        }
    }
}
