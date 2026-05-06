package com.example.antihoroscope.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.domain.prediction.PredictionCategory
import com.example.antihoroscope.domain.prediction.accentColorHex
import com.example.antihoroscope.domain.prediction.shortLabel
import com.example.antihoroscope.domain.prediction.titleRu
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun PredictionCategoryCard(
    category: PredictionCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val accent = category.accentColor()
    val shape = RoundedCornerShape(18.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "prediction-category-card-scale",
    )
    val shadowElevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 0.dp,
        animationSpec = tween(durationMillis = 180),
        label = "prediction-category-card-shadow",
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) colors.starWhite else colors.moonMuted,
        animationSpec = tween(durationMillis = 180),
        label = "prediction-category-card-label",
    )
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            colors.cosmicSurfaceHigh.copy(alpha = if (isSelected) 0.86f else 0.56f),
            colors.cosmicSurface.copy(alpha = if (isSelected) 0.80f else 0.46f),
        ),
    )
    val borderBrush = Brush.horizontalGradient(
        colors = if (isSelected) {
            listOf(accent, colors.neonMagenta.copy(alpha = 0.82f), colors.neonCyan.copy(alpha = 0.62f))
        } else {
            listOf(
                colors.moonMuted.copy(alpha = 0.22f),
                colors.neonPurple.copy(alpha = 0.16f),
            )
        },
    )

    Column(
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = shadowElevation,
                        shape = shape,
                        ambientColor = accent,
                        spotColor = colors.neonPurple,
                    )
                } else {
                    Modifier
                },
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .defaultMinSize(minWidth = 92.dp, minHeight = 70.dp)
            .background(
                brush = backgroundBrush,
                shape = shape,
            )
            .border(
                border = BorderStroke(width = if (isSelected) 1.5.dp else 1.dp, brush = borderBrush),
                shape = shape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = "Категория ${category.titleRu}"
                selected = isSelected
            }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = category.shortLabel,
            color = accent,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        Text(
            text = category.titleRu,
            modifier = Modifier.padding(top = 4.dp),
            color = labelColor,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
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
