package com.example.antihoroscope.domain.prediction

interface PredictionRepository {
    fun getAllPredictions(): List<Prediction>

    fun getPredictionsForZodiac(zodiacSignId: String): List<Prediction>

    fun getPredictionsByCategory(category: PredictionCategory): List<Prediction>
}
