package com.example.antihoroscope.domain.prediction

interface PredictionHistoryRepository {
    fun recordView(item: PredictionHistoryItem)

    fun getHistory(limit: Long): List<PredictionHistoryItem>

    fun toggleFavorite(item: FavoritePredictionItem): Boolean

    fun isFavorite(predictionId: String): Boolean

    fun getFavorites(): List<FavoritePredictionItem>
}
