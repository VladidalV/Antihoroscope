package com.example.antihoroscope.feature.prediction.detail

sealed interface PredictionDetailIntent {
    data object BackClicked : PredictionDetailIntent
    data object ShareClicked : PredictionDetailIntent
    data object FavoriteClicked : PredictionDetailIntent
    data object NextClicked : PredictionDetailIntent
    data object FeedbackShown : PredictionDetailIntent
}
