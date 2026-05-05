package com.example.antihoroscope.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.feature.onboarding.components.NotificationTimeSelector
import com.example.antihoroscope.feature.onboarding.components.OnboardingPage
import com.example.antihoroscope.feature.onboarding.components.ZodiacSelector
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun OnboardingScreen(
    onCompleted: (OnboardingCompletionResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val stateHolder = remember { OnboardingStateHolder() }
    var state by remember { mutableStateOf(stateHolder.state) }

    fun dispatch(intent: OnboardingIntent) {
        val effect = stateHolder.dispatch(intent)
        state = stateHolder.state

        when (effect) {
            is OnboardingEffect.Completed -> onCompleted(effect.result)
            null -> Unit
        }
    }

    when (state.currentStep) {
        OnboardingStep.Welcome -> WelcomeStep(
            state = state,
            modifier = modifier,
            onNext = { dispatch(OnboardingIntent.NextClicked) },
            onSkip = { dispatch(OnboardingIntent.SkipClicked) },
        )
        OnboardingStep.Concept -> ConceptStep(
            state = state,
            modifier = modifier,
            onBack = { dispatch(OnboardingIntent.BackClicked) },
            onNext = { dispatch(OnboardingIntent.NextClicked) },
            onSkip = { dispatch(OnboardingIntent.SkipClicked) },
        )
        OnboardingStep.Zodiac -> ZodiacStep(
            state = state,
            modifier = modifier,
            onBack = { dispatch(OnboardingIntent.BackClicked) },
            onNext = { dispatch(OnboardingIntent.NextClicked) },
            onZodiacSelected = { zodiacSignId ->
                dispatch(OnboardingIntent.ZodiacSelected(zodiacSignId))
            },
        )
        OnboardingStep.Notifications -> NotificationsStep(
            state = state,
            modifier = modifier,
            onBack = { dispatch(OnboardingIntent.BackClicked) },
            onComplete = { dispatch(OnboardingIntent.CompleteClicked) },
            onSkip = { dispatch(OnboardingIntent.SkipClicked) },
            onNotificationsEnabledChanged = { enabled ->
                dispatch(OnboardingIntent.NotificationToggleChanged(enabled))
            },
            onTimeSelected = { time ->
                dispatch(OnboardingIntent.NotificationTimeChanged(time))
            },
        )
    }
}

@Composable
private fun WelcomeStep(
    state: OnboardingState,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingPage(
        currentStep = state.currentStep,
        title = "Антигороскоп",
        description = "Единственный гороскоп, который честно не знает, что делает.",
        primaryButtonText = "Войти в космос",
        onPrimaryClick = onNext,
        secondaryButtonText = "Пропустить",
        onSecondaryClick = onSkip,
        modifier = modifier,
        visual = { MysticOrbVisual(label = "AH") },
        content = {
            OnboardingBodyText(
                text = "Каждый день звёзды будут присылать тебе странное, бесполезное, но подозрительно убедительное предсказание.",
            )
        },
    )
}

@Composable
private fun ConceptStep(
    state: OnboardingState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingPage(
        currentStep = state.currentStep,
        title = "Звёзды уже всё напутали",
        description = "Мы не знаем будущего. Но звёзды точно знают, что тебе сегодня нельзя солёное.",
        primaryButtonText = "Понятно, подозрительно",
        onPrimaryClick = onNext,
        canGoBack = state.canGoBack,
        onBackClick = onBack,
        secondaryButtonText = "Пропустить",
        onSecondaryClick = onSkip,
        modifier = modifier,
        visual = { MysticOrbVisual(label = "??") },
        content = {
            ExamplePredictionCard(
                text = "Сегодня найдёшь свою любовь. Даже если ты уже замужем.",
            )
        },
    )
}

@Composable
private fun ZodiacStep(
    state: OnboardingState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onZodiacSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingPage(
        currentStep = state.currentStep,
        title = "Выбери свой знак",
        description = "Это нужно, чтобы звёзды могли ошибаться персонально.",
        primaryButtonText = "Продолжить",
        onPrimaryClick = onNext,
        primaryButtonEnabled = state.canContinue,
        canGoBack = state.canGoBack,
        onBackClick = onBack,
        modifier = modifier,
        content = {
            ZodiacSelector(
                zodiacSigns = state.zodiacSigns,
                selectedZodiacSignId = state.selectedZodiacSignId,
                onZodiacSelected = onZodiacSelected,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OnboardingBodyText(
                text = state.selectedZodiacSign?.selectedCaption
                    ?: state.errorMessage
                    ?: "Сначала выбери знак. Космос без этого не справится.",
            )
        },
    )
}

@Composable
private fun NotificationsStep(
    state: OnboardingState,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onTimeSelected: (NotificationTimeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingPage(
        currentStep = state.currentStep,
        title = "Когда тревожить судьбу?",
        description = "Выбери время, когда тебе удобно получать ежедневное космическое предупреждение.",
        primaryButtonText = if (state.notificationsEnabled) "Включить уведомления" else "Продолжить",
        onPrimaryClick = onComplete,
        canGoBack = state.canGoBack,
        onBackClick = onBack,
        secondaryButtonText = "Не сейчас",
        onSecondaryClick = onSkip,
        modifier = modifier,
        visual = { MysticOrbVisual(label = state.notificationTime.label) },
        content = {
            NotificationTimeSelector(
                notificationsEnabled = state.notificationsEnabled,
                selectedTime = state.notificationTime,
                onNotificationsEnabledChanged = onNotificationsEnabledChanged,
                onTimeSelected = onTimeSelected,
            )
        },
    )
}

@Composable
private fun MysticOrbVisual(
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.neonPurple.copy(alpha = 0.30f),
                        colors.neonMagenta.copy(alpha = 0.12f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = size.minDimension * 0.56f,
                ),
                radius = size.minDimension * 0.56f,
                center = center,
            )
            drawCircle(
                color = colors.neonCyan.copy(alpha = 0.22f),
                radius = size.minDimension * 0.34f,
                center = Offset(center.x, center.y),
            )
            drawCircle(
                color = colors.cosmicSurface.copy(alpha = 0.80f),
                radius = size.minDimension * 0.28f,
                center = center,
            )
        }

        Text(
            text = label,
            color = colors.starWhite,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ExamplePredictionCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.cosmicSurfaceHigh.copy(alpha = 0.78f),
                        colors.cosmicSurface.copy(alpha = 0.58f),
                    ),
                ),
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colors.neonMagenta.copy(alpha = 0.54f),
                            colors.neonCyan.copy(alpha = 0.28f),
                        ),
                    ),
                ),
                shape = shape,
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Предсказание дня",
            color = colors.neonCyan,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = text,
            color = colors.starWhite,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun OnboardingBodyText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        color = AntiHoroscopeTheme.colors.moonMuted,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
    )
}
