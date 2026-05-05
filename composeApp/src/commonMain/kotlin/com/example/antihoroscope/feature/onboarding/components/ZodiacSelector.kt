package com.example.antihoroscope.feature.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.feature.onboarding.ZodiacSignUiModel

@Composable
fun ZodiacSelector(
    zodiacSigns: List<ZodiacSignUiModel>,
    selectedZodiacSignId: String?,
    onZodiacSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        zodiacSigns.chunked(SIGNS_PER_ROW).forEach { rowSigns ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowSigns.forEach { zodiacSign ->
                    ZodiacCard(
                        zodiacSign = zodiacSign,
                        isSelected = zodiacSign.id == selectedZodiacSignId,
                        onClick = { onZodiacSelected(zodiacSign.id) },
                        modifier = Modifier.weight(1f),
                    )
                }

                repeat(SIGNS_PER_ROW - rowSigns.size) {
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }
        }
    }
}

private const val SIGNS_PER_ROW = 3
