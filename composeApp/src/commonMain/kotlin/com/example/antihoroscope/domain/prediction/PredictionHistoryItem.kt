package com.example.antihoroscope.domain.prediction

data class PredictionHistoryItem(
    val historyId: String,
    val predictionId: String,
    val predictionText: String,
    val category: PredictionCategory,
    val absurdityLevel: Int,
    val zodiacSignId: String,
    val zodiacSignName: String,
    val dateKey: String,
    val dateLabel: String,
    val source: String,
    val viewedAtEpochMillis: Long,
)

fun DailyPrediction.toHistoryItem(
    source: String,
    viewedAtEpochMillis: Long,
): PredictionHistoryItem {
    val prediction = prediction

    return PredictionHistoryItem(
        historyId = predictionHistoryId(
            predictionId = prediction.id,
            dateKey = dateKey,
            zodiacSignId = zodiacSignId,
            source = source,
        ),
        predictionId = prediction.id,
        predictionText = prediction.text,
        category = prediction.category,
        absurdityLevel = prediction.absurdityLevel,
        zodiacSignId = zodiacSignId,
        zodiacSignName = zodiacSignName,
        dateKey = dateKey,
        dateLabel = dateLabel,
        source = source,
        viewedAtEpochMillis = viewedAtEpochMillis,
    )
}

fun predictionHistoryId(
    predictionId: String,
    dateKey: String,
    zodiacSignId: String,
    source: String,
): String = listOf(predictionId, dateKey, zodiacSignId, source).joinToString("|")
