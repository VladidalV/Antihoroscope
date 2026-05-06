package com.example.antihoroscope.feature.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.domain.prediction.PredictionCategory

@Composable
fun PredictionCategoryRow(
    categories: List<PredictionCategory>,
    selectedCategory: PredictionCategory,
    onCategorySelected: (PredictionCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        categories.forEach { category ->
            PredictionCategoryCard(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
            )
        }
    }
}
