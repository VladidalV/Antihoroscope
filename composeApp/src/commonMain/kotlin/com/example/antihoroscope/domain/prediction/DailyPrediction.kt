package com.example.antihoroscope.domain.prediction

data class DailyPrediction(
    val prediction: Prediction,
    val dateKey: String,
    val dateLabel: String,
    val zodiacSignId: String,
    val zodiacSignName: String,
)
