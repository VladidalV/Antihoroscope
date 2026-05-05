package com.example.antihoroscope.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun CosmicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
) {
    val colors = AntiHoroscopeTheme.colors
    val shape = RoundedCornerShape(18.dp)
    val backgroundBrush = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                colors.neonPurple,
                colors.neonMagenta,
            ),
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                colors.cosmicSurfaceHigh.copy(alpha = 0.64f),
                colors.cosmicSurface.copy(alpha = 0.64f),
            ),
        )
    }

    CosmicButtonSurface(
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        backgroundBrush = backgroundBrush,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (enabled) 0.32f else 0.12f),
                    colors.neonCyan.copy(alpha = if (enabled) 0.28f else 0.08f),
                ),
            ),
        ),
        contentPadding = contentPadding,
        onClick = onClick,
    ) {
        Text(
            text = text,
            color = if (enabled) colors.starWhite else colors.moonMuted.copy(alpha = 0.60f),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CosmicButtonSurface(
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    backgroundBrush: Brush,
    border: BorderStroke,
    contentPadding: PaddingValues,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shadowModifier = if (enabled) {
        Modifier.shadow(
            elevation = 14.dp,
            shape = shape,
            ambientColor = AntiHoroscopeTheme.colors.neonPurple,
            spotColor = AntiHoroscopeTheme.colors.neonMagenta,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(shadowModifier)
            .defaultMinSize(minHeight = 52.dp)
            .fillMaxWidth()
            .background(
                brush = backgroundBrush,
                shape = shape,
            )
            .border(
                border = border,
                shape = shape,
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
