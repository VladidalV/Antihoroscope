package com.example.antihoroscope.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.feature.onboarding.NotificationTimeUiModel
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun NotificationTimeSelector(
    notificationsEnabled: Boolean,
    selectedTime: NotificationTimeUiModel,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onTimeSelected: (NotificationTimeUiModel) -> Unit,
    modifier: Modifier = Modifier,
    presets: List<NotificationTimeUiModel> = NotificationTimeUiModel.presets,
) {
    val colors = AntiHoroscopeTheme.colors
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (notificationsEnabled) 12.dp else 0.dp,
                shape = shape,
                ambientColor = colors.neonPurple,
                spotColor = colors.neonMagenta,
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.cosmicSurfaceHigh.copy(alpha = 0.76f),
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
                            colors.neonPurple.copy(alpha = if (notificationsEnabled) 0.54f else 0.22f),
                            colors.neonCyan.copy(alpha = if (notificationsEnabled) 0.34f else 0.12f),
                        ),
                    ),
                ),
                shape = shape,
            )
            .padding(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ежедневные уведомления",
                    color = colors.starWhite,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = if (notificationsEnabled) {
                        "Космос постучится в ${selectedTime.label}"
                    } else {
                        "Судьба будет молчать. Подозрительно, но законно."
                    },
                    modifier = Modifier.padding(top = 6.dp),
                    color = colors.moonMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Switch(
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsEnabledChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.starWhite,
                    checkedTrackColor = colors.neonPurple,
                    uncheckedThumbColor = colors.moonMuted,
                    uncheckedTrackColor = colors.cosmicSurfaceHigh,
                ),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Время",
            color = colors.moonMuted,
            style = MaterialTheme.typography.labelLarge,
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            presets.forEach { time ->
                NotificationTimePresetChip(
                    time = time,
                    isSelected = selectedTime == time,
                    enabled = notificationsEnabled,
                    onClick = { onTimeSelected(time) },
                )
            }
        }
    }
}

@Composable
private fun NotificationTimePresetChip(
    time: NotificationTimeUiModel,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val shape = RoundedCornerShape(999.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Text(
        text = time.label,
        modifier = modifier
            .widthIn(min = 86.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isSelected && enabled) {
                        listOf(colors.neonPurple, colors.neonMagenta)
                    } else {
                        listOf(
                            colors.cosmicSurfaceHigh.copy(alpha = 0.68f),
                            colors.cosmicSurface.copy(alpha = 0.54f),
                        )
                    },
                ),
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected && enabled) {
                        colors.neonCyan.copy(alpha = 0.44f)
                    } else {
                        colors.moonMuted.copy(alpha = 0.18f)
                    },
                ),
                shape = shape,
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 11.dp),
        color = when {
            !enabled -> colors.moonMuted.copy(alpha = 0.46f)
            isSelected -> colors.starWhite
            else -> colors.moonMuted
        },
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
    )
}
