package com.example.antihoroscope.feature.prediction.detail

sealed interface PredictionDetailEvent {
    data object NavigateBack : PredictionDetailEvent

    data class ShowShareFeedback(
        val message: String,
    ) : PredictionDetailEvent

    data class ShowFavoriteFeedback(
        val message: String,
        val isFavorite: Boolean,
    ) : PredictionDetailEvent

    data class ShowNextFeedback(
        val message: String,
    ) : PredictionDetailEvent
}
