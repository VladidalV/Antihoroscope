package com.example.antihoroscope.feature.home

sealed interface HomeEvent {
    data class ShowMessage(
        val message: String,
    ) : HomeEvent

    data class PredictionSelected(
        val predictionId: String,
    ) : HomeEvent
}
