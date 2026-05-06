package com.example.antihoroscope.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.domain.prediction.DailyPrediction
import com.example.antihoroscope.domain.prediction.PredictionCategory
import com.example.antihoroscope.domain.prediction.accentColorHex
import com.example.antihoroscope.domain.prediction.titleRu
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun GlowingPredictionCard(
    dailyPrediction: DailyPrediction,
    modifier: Modifier = Modifier,
) {
    val prediction = dailyPrediction.prediction

    GlowingPredictionCard(
        category = prediction.category,
        predictionText = prediction.text,
        absurdityLevel = prediction.absurdityLevel,
        zodiacSignName = dailyPrediction.zodiacSignName,
        dateLabel = dailyPrediction.dateLabel,
        modifier = modifier,
    )
}

@Composable
fun GlowingPredictionCard(
    category: PredictionCategory,
    predictionText: String,
    absurdityLevel: Int,
    zodiacSignName: String,
    dateLabel: String,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val accentColor = category.accentColor()
    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 22.dp,
                shape = shape,
                ambientColor = accentColor.copy(alpha = 0.46f),
                spotColor = colors.neonPurple.copy(alpha = 0.34f),
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.cosmicSurfaceHigh.copy(alpha = 0.82f),
                        colors.cosmicSurface.copy(alpha = 0.68f),
                        colors.cosmicBlack.copy(alpha = 0.34f),
                    ),
                ),
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.82f),
                            colors.neonPurple.copy(alpha = 0.52f),
                            colors.neonCyan.copy(alpha = 0.34f),
                        ),
                    ),
                ),
                shape = shape,
            )
            .defaultMinSize(minHeight = 238.dp)
            .padding(22.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            PredictionCardHeader(
                category = category,
                accentColor = accentColor,
                absurdityLevel = absurdityLevel,
            )

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = predictionText,
                color = colors.starWhite,
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Clip,
            )

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "$zodiacSignName · $dateLabel",
                color = colors.moonMuted,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun PredictionCardHeader(
    category: PredictionCategory,
    accentColor: Color,
    absurdityLevel: Int,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(
                        color = accentColor,
                        shape = CircleShape,
                    ),
            )
            Text(
                text = category.titleRu,
                color = accentColor,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }

        Text(
            text = "Абсурдность ${absurdityLevel.coerceIn(1, 5)}/5",
            color = colors.moonMuted,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

private fun PredictionCategory.accentColor(): Color {
    val hexColor = accentColorHex.removePrefix("#")
    val rgb = hexColor.toLongOrNull(radix = 16) ?: return Color.White

    return Color(0xFF000000L or rgb)
}
