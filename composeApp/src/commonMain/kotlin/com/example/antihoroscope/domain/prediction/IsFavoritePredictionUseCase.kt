package com.example.antihoroscope.domain.prediction

class IsFavoritePredictionUseCase(
    private val repository: PredictionHistoryRepository,
) {
    operator fun invoke(predictionId: String): Boolean {
        return repository.isFavorite(predictionId)
    }
}
