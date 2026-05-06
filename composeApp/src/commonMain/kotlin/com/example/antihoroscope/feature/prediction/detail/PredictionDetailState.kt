package com.example.antihoroscope.feature.prediction.detail

import com.example.antihoroscope.domain.prediction.DailyPrediction

data class PredictionDetailState(
    val dailyPrediction: DailyPrediction,
    val isFavorite: Boolean = false,
    val feedbackMessage: String? = null,
)
