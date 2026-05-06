package com.example.antihoroscope.feature.home

import com.example.antihoroscope.domain.prediction.PredictionCategory

sealed interface HomeIntent {
    data object ScreenShown : HomeIntent

    data class CategorySelected(
        val category: PredictionCategory,
    ) : HomeIntent

    data object RefreshClicked : HomeIntent

    data object ShareClicked : HomeIntent

    data class PredictionClicked(
        val predictionId: String,
    ) : HomeIntent
}
