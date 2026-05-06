package com.example.antihoroscope.domain.prediction

data class Prediction(
    val id: String,
    val text: String,
    val category: PredictionCategory,
    val zodiacSignId: String?,
    /**
     * Absurdity level displayed to the user. Valid range: 1..5.
     */
    val absurdityLevel: Int,
)
