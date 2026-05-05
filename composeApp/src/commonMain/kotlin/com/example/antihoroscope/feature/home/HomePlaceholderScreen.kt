package com.example.antihoroscope.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.feature.onboarding.ZodiacSignUiModel
import com.example.antihoroscope.ui.components.CosmicBackground
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun HomePlaceholderScreen(
    selectedZodiacSignId: String?,
    modifier: Modifier = Modifier,
) {
    val selectedZodiacSign = ZodiacSignUiModel.all.firstOrNull { it.id == selectedZodiacSignId }

    CosmicBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            ZodiacBadge(selectedZodiacSign = selectedZodiacSign)

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Антигороскоп почти готов",
                color = AntiHoroscopeTheme.colors.starWhite,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Звёзды уже нервничают. Скоро здесь появится твоё первое дневное антипредсказание.",
                color = AntiHoroscopeTheme.colors.moonMuted,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ZodiacBadge(
    selectedZodiacSign: ZodiacSignUiModel?,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val shape = RoundedCornerShape(26.dp)

    Box(
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
                            colors.neonPurple.copy(alpha = 0.52f),
                            colors.neonCyan.copy(alpha = 0.26f),
                        ),
                    ),
                ),
                shape = shape,
            )
            .padding(22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = selectedZodiacSign?.symbol ?: "??",
                color = colors.neonCyan,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = selectedZodiacSign?.name ?: "Знак не выбран",
                color = colors.starWhite,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = selectedZodiacSign?.selectedCaption ?: "Космос пока в режиме ожидания.",
                color = colors.moonMuted,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
