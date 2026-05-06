package com.example.antihoroscope.domain.prediction

class RecordPredictionViewUseCase(
    private val repository: PredictionHistoryRepository,
    private val currentTimeMillis: () -> Long,
) {
    operator fun invoke(
        dailyPrediction: DailyPrediction,
        source: String,
    ) {
        repository.recordView(
            dailyPrediction.toHistoryItem(
                source = source,
                viewedAtEpochMillis = currentTimeMillis(),
            ),
        )
    }
}
