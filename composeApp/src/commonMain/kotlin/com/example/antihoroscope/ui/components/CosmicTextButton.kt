package com.example.antihoroscope.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun CosmicTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    val colors = AntiHoroscopeTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    Text(
        text = text,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(contentPadding),
        color = if (enabled) colors.neonCyan else colors.moonMuted.copy(alpha = 0.52f),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
    )
}
