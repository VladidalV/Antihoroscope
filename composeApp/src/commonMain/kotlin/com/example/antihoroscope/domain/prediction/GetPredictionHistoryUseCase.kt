package com.example.antihoroscope.domain.prediction

class GetPredictionHistoryUseCase(
    private val repository: PredictionHistoryRepository,
) {
    operator fun invoke(limit: Long = DEFAULT_LIMIT): List<PredictionHistoryItem> {
        return repository.getHistory(limit = limit)
    }

    private companion object {
        const val DEFAULT_LIMIT = 50L
    }
}
