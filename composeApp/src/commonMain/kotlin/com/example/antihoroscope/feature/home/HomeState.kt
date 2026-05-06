package com.example.antihoroscope.feature.home

import com.example.antihoroscope.domain.prediction.DailyPrediction
import com.example.antihoroscope.domain.prediction.GenerationLimit
import com.example.antihoroscope.domain.prediction.PredictionCategory

sealed interface HomeState {
    data object Loading : HomeState

    data class Content(
        val dailyPrediction: DailyPrediction,
        val categories: List<PredictionCategory> = PredictionCategory.entries,
        val selectedCategory: PredictionCategory = PredictionCategory.Chaos,
        val generationLimit: GenerationLimit,
        val isRefreshing: Boolean = false,
    ) : HomeState {
        val canRefresh: Boolean
            get() = !isRefreshing && !generationLimit.isExhausted
    }

    data class MissingZodiac(
        val title: String = "Космос потерял твой знак",
        val message: String = "Вернись в настройки или пройди онбординг заново.",
    ) : HomeState

    data class EmptyCatalog(
        val title: String = "Звёзды молчат",
        val message: String = "Похоже, локальный банк предсказаний не загрузился.",
    ) : HomeState

    data class Error(
        val title: String = "Предсказание застряло в ретрограде",
        val message: String = "Попробуй ещё раз чуть позже.",
    ) : HomeState
}
