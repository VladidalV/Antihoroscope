package com.example.antihoroscope.feature.prediction.detail

import androidx.lifecycle.ViewModel
import com.example.antihoroscope.core.analytics.AnalyticsTracker
import com.example.antihoroscope.core.analytics.NoOpAnalyticsTracker
import com.example.antihoroscope.domain.prediction.DailyPrediction
import com.example.antihoroscope.domain.prediction.IsFavoritePredictionUseCase
import com.example.antihoroscope.domain.prediction.RecordPredictionViewUseCase
import com.example.antihoroscope.domain.prediction.ToggleFavoritePredictionUseCase
import com.example.antihoroscope.domain.prediction.analyticsName
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class PredictionDetailViewModel(
    private val dailyPrediction: DailyPrediction,
    private val isFavoritePredictionUseCase: IsFavoritePredictionUseCase? = null,
    private val toggleFavoritePredictionUseCase: ToggleFavoritePredictionUseCase? = null,
    private val recordPredictionViewUseCase: RecordPredictionViewUseCase? = null,
    private val analyticsTracker: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    private val _state = MutableStateFlow(
        PredictionDetailState(
            dailyPrediction = dailyPrediction,
            isFavorite = isFavoritePredictionUseCase?.invoke(dailyPrediction.prediction.id) ?: false,
        ),
    )
    val state: StateFlow<PredictionDetailState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PredictionDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PredictionDetailEvent> = _events.asSharedFlow()

    init {
        recordPredictionViewUseCase?.invoke(
            dailyPrediction = dailyPrediction,
            source = HISTORY_SOURCE_DETAIL,
        )
        analyticsTracker.track(
            eventName = PredictionDetailAnalyticsEvent.DetailViewed,
            params = analyticsParams(),
        )
    }

    fun onIntent(intent: PredictionDetailIntent) {
        when (intent) {
            PredictionDetailIntent.BackClicked -> navigateBack()
            PredictionDetailIntent.ShareClicked -> showShareFeedback()
            PredictionDetailIntent.FavoriteClicked -> toggleFavorite()
            PredictionDetailIntent.NextClicked -> showNextFeedback()
            PredictionDetailIntent.FeedbackShown -> clearFeedback()
        }
    }

    private fun navigateBack() {
        analyticsTracker.track(
            eventName = PredictionDetailAnalyticsEvent.BackClicked,
            params = analyticsParams(),
        )
        _events.tryEmit(PredictionDetailEvent.NavigateBack)
    }

    private fun showShareFeedback() {
        analyticsTracker.track(
            eventName = PredictionDetailAnalyticsEvent.ShareClicked,
            params = analyticsParams(),
        )
        _state.value = _state.value.copy(feedbackMessage = SHARE_MESSAGE)
        _events.tryEmit(
            PredictionDetailEvent.ShowShareFeedback(
                message = SHARE_MESSAGE,
            ),
        )
    }

    private fun toggleFavorite() {
        val nextFavoriteState = toggleFavoritePredictionUseCase?.invoke(dailyPrediction)
            ?: !_state.value.isFavorite
        val message = if (nextFavoriteState) {
            FAVORITE_ADDED_MESSAGE
        } else {
            FAVORITE_REMOVED_MESSAGE
        }

        analyticsTracker.track(
            eventName = PredictionDetailAnalyticsEvent.FavoriteClicked,
            params = analyticsParams() + ("is_favorite" to nextFavoriteState.toString()),
        )
        _state.value = _state.value.copy(
            isFavorite = nextFavoriteState,
            feedbackMessage = message,
        )
        _events.tryEmit(
            PredictionDetailEvent.ShowFavoriteFeedback(
                message = message,
                isFavorite = nextFavoriteState,
            ),
        )
    }

    private fun showNextFeedback() {
        _state.value = _state.value.copy(feedbackMessage = NEXT_MESSAGE)
        _events.tryEmit(
            PredictionDetailEvent.ShowNextFeedback(
                message = NEXT_MESSAGE,
            ),
        )
    }

    private fun clearFeedback() {
        _state.value = _state.value.copy(feedbackMessage = null)
    }

    private fun analyticsParams(): Map<String, String> {
        val dailyPrediction = _state.value.dailyPrediction
        val prediction = dailyPrediction.prediction

        return mapOf(
            "prediction_id" to prediction.id,
            "zodiac_sign" to dailyPrediction.zodiacSignId,
            "category" to prediction.category.analyticsName,
            "absurdity_level" to prediction.absurdityLevel.toString(),
            "date" to dailyPrediction.dateKey,
            "source" to "home",
        )
    }

    private companion object {
        const val SHARE_MESSAGE = "Скоро можно будет отправить это в чат."
        const val FAVORITE_ADDED_MESSAGE = "Сохранено в избранное."
        const val FAVORITE_REMOVED_MESSAGE = "Убрано из избранного."
        const val NEXT_MESSAGE = "Следующее предсказание появится в одном из следующих релизов."
        const val HISTORY_SOURCE_DETAIL = "detail"
    }
}

private object PredictionDetailAnalyticsEvent {
    const val DetailViewed = "prediction_detail_viewed"
    const val ShareClicked = "prediction_detail_share_clicked"
    const val FavoriteClicked = "prediction_favorite_clicked"
    const val BackClicked = "prediction_detail_back_clicked"
}
