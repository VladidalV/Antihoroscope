package com.example.antihoroscope.data.prediction.history

import com.example.antihoroscope.data.local.AntiHoroscopeDatabase
import com.example.antihoroscope.data.local.Favorite_predictions
import com.example.antihoroscope.data.local.Prediction_history
import com.example.antihoroscope.domain.prediction.FavoritePredictionItem
import com.example.antihoroscope.domain.prediction.PredictionCategory
import com.example.antihoroscope.domain.prediction.PredictionHistoryItem

class SqlDelightPredictionHistoryLocalDataSource(
    database: AntiHoroscopeDatabase,
) : PredictionHistoryLocalDataSource {
    private val queries = database.predictionHistoryQueries

    override fun recordHistory(item: PredictionHistoryItem) {
        queries.upsertHistory(
            historyId = item.historyId,
            predictionId = item.predictionId,
            predictionText = item.predictionText,
            category = item.category.storageName,
            absurdityLevel = item.absurdityLevel.toLong(),
            zodiacSignId = item.zodiacSignId,
            zodiacSignName = item.zodiacSignName,
            dateKey = item.dateKey,
            dateLabel = item.dateLabel,
            source = item.source,
            viewedAtEpochMillis = item.viewedAtEpochMillis,
        )
    }

    override fun getHistory(limit: Long): List<PredictionHistoryItem> {
        return queries.selectHistory(limit)
            .executeAsList()
            .map { row -> row.toDomain() }
    }

    override fun addFavorite(item: FavoritePredictionItem) {
        queries.upsertFavorite(
            predictionId = item.predictionId,
            predictionText = item.predictionText,
            category = item.category.storageName,
            absurdityLevel = item.absurdityLevel.toLong(),
            zodiacSignId = item.zodiacSignId,
            zodiacSignName = item.zodiacSignName,
            dateKey = item.dateKey,
            dateLabel = item.dateLabel,
            favoritedAtEpochMillis = item.favoritedAtEpochMillis,
        )
    }

    override fun removeFavorite(predictionId: String) {
        queries.deleteFavorite(predictionId)
    }

    override fun isFavorite(predictionId: String): Boolean {
        return queries.isFavorite(predictionId).executeAsOne()
    }

    override fun getFavorites(): List<FavoritePredictionItem> {
        return queries.selectFavorites()
            .executeAsList()
            .map { row -> row.toDomain() }
    }
}

private fun Prediction_history.toDomain(): PredictionHistoryItem {
    return PredictionHistoryItem(
        historyId = historyId,
        predictionId = predictionId,
        predictionText = predictionText,
        category = category.toPredictionCategory(),
        absurdityLevel = absurdityLevel.toInt(),
        zodiacSignId = zodiacSignId,
        zodiacSignName = zodiacSignName,
        dateKey = dateKey,
        dateLabel = dateLabel,
        source = source,
        viewedAtEpochMillis = viewedAtEpochMillis,
    )
}

private fun Favorite_predictions.toDomain(): FavoritePredictionItem {
    return FavoritePredictionItem(
        predictionId = predictionId,
        predictionText = predictionText,
        category = category.toPredictionCategory(),
        absurdityLevel = absurdityLevel.toInt(),
        zodiacSignId = zodiacSignId,
        zodiacSignName = zodiacSignName,
        dateKey = dateKey,
        dateLabel = dateLabel,
        favoritedAtEpochMillis = favoritedAtEpochMillis,
    )
}

private val PredictionCategory.storageName: String
    get() = name

private fun String.toPredictionCategory(): PredictionCategory {
    return PredictionCategory.entries.firstOrNull { category ->
        category.name == this
    } ?: PredictionCategory.Chaos
}
