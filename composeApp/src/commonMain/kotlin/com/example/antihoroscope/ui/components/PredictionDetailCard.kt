package com.example.antihoroscope.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun PredictionDetailCard(
    dailyPrediction: DailyPrediction,
    modifier: Modifier = Modifier,
) {
    val prediction = dailyPrediction.prediction

    PredictionDetailCard(
        category = prediction.category,
        predictionText = prediction.text,
        absurdityLevel = prediction.absurdityLevel,
        zodiacSignName = dailyPrediction.zodiacSignName,
        dateLabel = dailyPrediction.dateLabel,
        signature = predictionDetailSignature(prediction.id),
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PredictionDetailCard(
    category: PredictionCategory,
    predictionText: String,
    absurdityLevel: Int,
    zodiacSignName: String,
    dateLabel: String,
    signature: String,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val accentColor = category.accentColor()
    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = shape,
                ambientColor = accentColor.copy(alpha = 0.38f),
                spotColor = colors.neonPurple.copy(alpha = 0.28f),
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.cosmicSurfaceHigh.copy(alpha = 0.86f),
                        colors.cosmicSurface.copy(alpha = 0.74f),
                        colors.cosmicBlack.copy(alpha = 0.40f),
                    ),
                ),
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.78f),
                            colors.neonPurple.copy(alpha = 0.48f),
                            colors.neonCyan.copy(alpha = 0.30f),
                        ),
                    ),
                ),
                shape = shape,
            )
            .padding(22.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Полная сводка небесной канцелярии",
                color = accentColor,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PredictionMetadataItem(label = zodiacSignName)
                PredictionMetadataItem(label = category.titleRu)
                PredictionMetadataItem(label = dateLabel)
                PredictionMetadataItem(label = "Абсурдность ${absurdityLevel.coerceIn(1, 5)}/5")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = predictionText,
                color = colors.starWhite,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Clip,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = signature,
                color = colors.moonMuted,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Clip,
            )
        }
    }
}

@Composable
private fun PredictionMetadataItem(
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .background(
                color = colors.cosmicBlack.copy(alpha = 0.24f),
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = colors.starWhite.copy(alpha = 0.12f),
                ),
                shape = shape,
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
    ) {
        Text(
            text = label,
            color = colors.moonMuted,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
    }
}

private fun predictionDetailSignature(predictionId: String): String {
    val signatures = listOf(
        "Методика: три сомнительных транзита, один кофе и статистически значимое ощущение.",
        "Примечание астролаборатории: совпадения возможны, особенно если очень хочется.",
        "Расчёт проверен по внутренней шкале космической уверенности. Шкала пока не прошла peer review.",
    )
    val index = predictionId.fold(0) { accumulator, character ->
        accumulator + character.code
    } % signatures.size

    return signatures[index]
}

private fun PredictionCategory.accentColor(): Color {
    val hexColor = accentColorHex.removePrefix("#")
    val rgb = hexColor.toLongOrNull(radix = 16) ?: return Color.White

    return Color(0xFF000000L or rgb)
}
