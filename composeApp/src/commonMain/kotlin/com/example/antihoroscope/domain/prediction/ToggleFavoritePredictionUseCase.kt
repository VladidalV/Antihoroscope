package com.example.antihoroscope.domain.prediction

class ToggleFavoritePredictionUseCase(
    private val repository: PredictionHistoryRepository,
    private val currentTimeMillis: () -> Long,
) {
    operator fun invoke(dailyPrediction: DailyPrediction): Boolean {
        return repository.toggleFavorite(
            dailyPrediction.toFavoriteItem(
                favoritedAtEpochMillis = currentTimeMillis(),
            ),
        )
    }
}
