package com.example.antihoroscope.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.feature.onboarding.ZodiacSignUiModel
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun ZodiacCard(
    zodiacSign: ZodiacSignUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val accent = Color(zodiacSign.accentColor)
    val shape = RoundedCornerShape(18.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            colors.cosmicSurfaceHigh.copy(alpha = if (isSelected) 0.90f else 0.64f),
            colors.cosmicSurface.copy(alpha = if (isSelected) 0.86f else 0.52f),
        ),
    )
    val borderBrush = Brush.horizontalGradient(
        colors = if (isSelected) {
            listOf(accent, colors.neonMagenta, colors.neonCyan)
        } else {
            listOf(
                colors.moonMuted.copy(alpha = 0.22f),
                colors.neonPurple.copy(alpha = 0.18f),
            )
        },
    )

    Column(
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 14.dp,
                        shape = shape,
                        ambientColor = accent,
                        spotColor = colors.neonPurple,
                    )
                } else {
                    Modifier
                },
            )
            .fillMaxWidth()
            .heightIn(min = 112.dp)
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
                contentDescription = zodiacSign.accessibilityLabel
                selected = isSelected
            }
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = if (isSelected) 0.34f else 0.18f),
                            Color.Transparent,
                        ),
                    ),
                    shape = RoundedCornerShape(999.dp),
                )
                .padding(horizontal = 13.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = zodiacSign.symbol,
                color = if (isSelected) colors.starWhite else accent,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        Text(
            text = zodiacSign.name,
            modifier = Modifier.padding(top = 10.dp),
            color = colors.starWhite,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        Text(
            text = zodiacSign.dateRange,
            modifier = Modifier.padding(top = 4.dp),
            color = colors.moonMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
