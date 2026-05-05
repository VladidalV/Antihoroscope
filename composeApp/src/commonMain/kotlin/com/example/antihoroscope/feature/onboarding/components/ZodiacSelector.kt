package com.example.antihoroscope.feature.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 104.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            items = zodiacSigns,
            key = { zodiacSign -> zodiacSign.id },
        ) { zodiacSign ->
            ZodiacCard(
                zodiacSign = zodiacSign,
                isSelected = zodiacSign.id == selectedZodiacSignId,
                onClick = { onZodiacSelected(zodiacSign.id) },
            )
        }
    }
}
