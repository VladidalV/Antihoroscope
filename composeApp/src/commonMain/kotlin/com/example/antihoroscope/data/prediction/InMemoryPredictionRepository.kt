package com.example.antihoroscope.data.prediction

import com.example.antihoroscope.domain.prediction.Prediction
import com.example.antihoroscope.domain.prediction.PredictionCategory
import com.example.antihoroscope.domain.prediction.PredictionRepository

class InMemoryPredictionRepository(
    private val predictions: List<Prediction> = LocalPredictionCatalog.predictions,
) : PredictionRepository {
    override fun getAllPredictions(): List<Prediction> = predictions.toList()

    override fun getPredictionsForZodiac(zodiacSignId: String): List<Prediction> =
        predictions.filter { prediction ->
            prediction.zodiacSignId == null || prediction.zodiacSignId == zodiacSignId
        }

    override fun getPredictionsByCategory(category: PredictionCategory): List<Prediction> =
        predictions.filter { prediction ->
            prediction.category == category
        }
}
