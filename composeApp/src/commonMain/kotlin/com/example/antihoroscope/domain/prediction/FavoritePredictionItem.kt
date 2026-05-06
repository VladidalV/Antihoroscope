package com.example.antihoroscope.domain.prediction

data class FavoritePredictionItem(
    val predictionId: String,
    val predictionText: String,
    val category: PredictionCategory,
    val absurdityLevel: Int,
    val zodiacSignId: String,
    val zodiacSignName: String,
    val dateKey: String,
    val dateLabel: String,
    val favoritedAtEpochMillis: Long,
)

fun DailyPrediction.toFavoriteItem(
    favoritedAtEpochMillis: Long,
): FavoritePredictionItem {
    val prediction = prediction

    return FavoritePredictionItem(
        predictionId = prediction.id,
        predictionText = prediction.text,
        category = prediction.category,
        absurdityLevel = prediction.absurdityLevel,
        zodiacSignId = zodiacSignId,
        zodiacSignName = zodiacSignName,
        dateKey = dateKey,
        dateLabel = dateLabel,
        favoritedAtEpochMillis = favoritedAtEpochMillis,
    )
}
