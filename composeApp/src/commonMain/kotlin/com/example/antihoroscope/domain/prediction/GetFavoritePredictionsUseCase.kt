package com.example.antihoroscope.domain.prediction

class GetFavoritePredictionsUseCase(
    private val repository: PredictionHistoryRepository,
) {
    operator fun invoke(): List<FavoritePredictionItem> {
        return repository.getFavorites()
    }
}
