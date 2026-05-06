package com.example.antihoroscope.data.prediction.history

import com.example.antihoroscope.domain.prediction.FavoritePredictionItem
import com.example.antihoroscope.domain.prediction.PredictionHistoryItem

interface PredictionHistoryLocalDataSource {
    fun recordHistory(item: PredictionHistoryItem)

    fun getHistory(limit: Long): List<PredictionHistoryItem>

    fun addFavorite(item: FavoritePredictionItem)

    fun removeFavorite(predictionId: String)

    fun isFavorite(predictionId: String): Boolean

    fun getFavorites(): List<FavoritePredictionItem>
}
