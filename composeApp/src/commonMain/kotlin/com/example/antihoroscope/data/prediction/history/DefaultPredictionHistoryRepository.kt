package com.example.antihoroscope.data.prediction.history

import com.example.antihoroscope.domain.prediction.FavoritePredictionItem
import com.example.antihoroscope.domain.prediction.PredictionHistoryItem
import com.example.antihoroscope.domain.prediction.PredictionHistoryRepository

class DefaultPredictionHistoryRepository(
    private val localDataSource: PredictionHistoryLocalDataSource,
) : PredictionHistoryRepository {
    override fun recordView(item: PredictionHistoryItem) {
        localDataSource.recordHistory(item)
    }

    override fun getHistory(limit: Long): List<PredictionHistoryItem> {
        return localDataSource.getHistory(limit = limit)
    }

    override fun toggleFavorite(item: FavoritePredictionItem): Boolean {
        val nextFavoriteState = !localDataSource.isFavorite(item.predictionId)

        if (nextFavoriteState) {
            localDataSource.addFavorite(item)
        } else {
            localDataSource.removeFavorite(item.predictionId)
        }

        return nextFavoriteState
    }

    override fun isFavorite(predictionId: String): Boolean {
        return localDataSource.isFavorite(predictionId)
    }

    override fun getFavorites(): List<FavoritePredictionItem> {
        return localDataSource.getFavorites()
    }
}
