package com.example.antihoroscope.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.domain.prediction.GenerationLimit
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun GenerationLimitIndicator(
    generationLimit: GenerationLimit,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val accent = if (generationLimit.isExhausted) {
        colors.warningRose
    } else {
        colors.neonCyan
    }
    val label = if (generationLimit.isExhausted) {
        "Космос выдохся"
    } else {
        "Осталось: ${generationLimit.remainingCount}"
    }
    val shape = RoundedCornerShape(999.dp)

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 36.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colors.cosmicSurfaceHigh.copy(alpha = 0.70f),
                        colors.cosmicSurface.copy(alpha = 0.48f),
                    ),
                ),
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.58f),
                            Color.White.copy(alpha = 0.10f),
                        ),
                    ),
                ),
                shape = shape,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = accent,
            style = MaterialTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}
