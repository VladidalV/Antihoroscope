package com.example.antihoroscope.feature.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.feature.onboarding.OnboardingStep
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun OnboardingProgressDots(
    currentStep: OnboardingStep,
    modifier: Modifier = Modifier,
    totalSteps: Int = OnboardingStep.totalSteps,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(totalSteps) { index ->
            OnboardingProgressDot(isActive = index == currentStep.index)
        }
    }
}

@Composable
private fun OnboardingProgressDot(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val width by animateDpAsState(
        targetValue = if (isActive) 28.dp else 8.dp,
        animationSpec = tween(durationMillis = 220),
    )
    val color by animateColorAsState(
        targetValue = if (isActive) colors.neonMagenta else colors.moonMuted.copy(alpha = 0.34f),
        animationSpec = tween(durationMillis = 220),
    )

    val dotModifier = if (isActive) {
        modifier.shadow(
            elevation = 10.dp,
            shape = CircleShape,
            ambientColor = colors.neonMagenta,
            spotColor = colors.neonPurple,
        )
    } else {
        modifier
    }

    Row(
        modifier = dotModifier
            .width(width)
            .height(8.dp)
            .background(
                color = color,
                shape = CircleShape,
            ),
    ) {
        // Deliberately empty: the Row is the animated dot shape.
    }
}
