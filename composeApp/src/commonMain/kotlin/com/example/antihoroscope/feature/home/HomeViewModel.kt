package com.example.antihoroscope.feature.home

import androidx.lifecycle.ViewModel
import com.example.antihoroscope.core.analytics.AnalyticsTracker
import com.example.antihoroscope.core.analytics.NoOpAnalyticsTracker
import com.example.antihoroscope.data.settings.OnboardingSettingsStorage
import com.example.antihoroscope.domain.prediction.ConsumeGenerationLimitResult
import com.example.antihoroscope.domain.prediction.ConsumeGenerationLimitUseCase
import com.example.antihoroscope.domain.prediction.DailyPrediction
import com.example.antihoroscope.domain.prediction.GenerateDailyPredictionParams
import com.example.antihoroscope.domain.prediction.GenerateDailyPredictionResult
import com.example.antihoroscope.domain.prediction.GenerateDailyPredictionUseCase
import com.example.antihoroscope.domain.prediction.GenerateManualPredictionParams
import com.example.antihoroscope.domain.prediction.GenerateManualPredictionResult
import com.example.antihoroscope.domain.prediction.GenerateManualPredictionUseCase
import com.example.antihoroscope.domain.prediction.GetGenerationLimitUseCase
import com.example.antihoroscope.domain.prediction.PredictionCategory
import com.example.antihoroscope.domain.prediction.RecordPredictionViewUseCase
import com.example.antihoroscope.domain.prediction.analyticsName
import com.example.antihoroscope.feature.onboarding.ZodiacSignUiModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val onboardingSettingsStorage: OnboardingSettingsStorage,
    private val generateDailyPredictionUseCase: GenerateDailyPredictionUseCase,
    private val generateManualPredictionUseCase: GenerateManualPredictionUseCase,
    private val getGenerationLimitUseCase: GetGenerationLimitUseCase,
    private val consumeGenerationLimitUseCase: ConsumeGenerationLimitUseCase,
    private val recordPredictionViewUseCase: RecordPredictionViewUseCase? = null,
    private val analyticsTracker: AnalyticsTracker = NoOpAnalyticsTracker,
) : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ScreenShown -> loadHome()
            is HomeIntent.CategorySelected -> selectCategory(intent.category)
            HomeIntent.RefreshClicked -> refreshPrediction()
            HomeIntent.ShareClicked -> sharePrediction()
            is HomeIntent.PredictionClicked -> selectPrediction(intent.predictionId)
        }
    }

    private fun loadHome() {
        _state.value = HomeState.Loading
        val zodiacSign = getSelectedZodiacSign()

        if (zodiacSign == null) {
            analyticsTracker.track(HomeAnalyticsEvent.MissingZodiac)
            _state.value = HomeState.MissingZodiac()
            return
        }

        when (
            val result = generateDailyPredictionUseCase(
                GenerateDailyPredictionParams(
                    zodiacSignId = zodiacSign.id,
                    zodiacSignName = zodiacSign.name,
                ),
            )
        ) {
            is GenerateDailyPredictionResult.Success -> {
                val generationLimit = getGenerationLimitUseCase()
                analyticsTracker.track(
                    eventName = HomeAnalyticsEvent.HomeViewed,
                    params = mapOf(
                        "zodiac_sign" to zodiacSign.id,
                        "date_key" to result.dailyPrediction.dateKey,
                    ),
                )
                trackPredictionViewed(result.dailyPrediction)
                recordPredictionView(result.dailyPrediction)
                _state.value = HomeState.Content(
                    dailyPrediction = result.dailyPrediction,
                    generationLimit = generationLimit,
                )
            }
            GenerateDailyPredictionResult.Error.EmptyPredictionPool -> {
                analyticsTracker.track(HomeAnalyticsEvent.EmptyCatalog)
                _state.value = HomeState.EmptyCatalog()
            }
        }
    }

    private fun selectCategory(category: PredictionCategory) {
        val currentState = _state.value as? HomeState.Content ?: return

        analyticsTracker.track(
            eventName = HomeAnalyticsEvent.CategorySelected,
            params = mapOf("category" to category.analyticsName),
        )
        _state.value = currentState.copy(selectedCategory = category)
    }

    private fun refreshPrediction() {
        val currentState = _state.value as? HomeState.Content ?: return
        val currentLimit = getGenerationLimitUseCase()

        if (currentLimit.isExhausted) {
            _state.value = currentState.copy(generationLimit = currentLimit)
            _events.tryEmit(HomeEvent.ShowMessage(EXHAUSTED_MESSAGE))
            trackRefreshLimitReached(currentLimit.dateKey)
            return
        }

        val refreshingState = currentState.copy(
            generationLimit = currentLimit,
            isRefreshing = true,
        )
        _state.value = refreshingState

        val result = generateManualPredictionUseCase(
            GenerateManualPredictionParams(
                zodiacSignId = currentState.dailyPrediction.zodiacSignId,
                zodiacSignName = currentState.dailyPrediction.zodiacSignName,
                generationIndex = currentLimit.usedCount + 1,
                category = currentState.selectedCategory,
                currentPredictionId = currentState.dailyPrediction.prediction.id,
            ),
        )

        when (result) {
            is GenerateManualPredictionResult.Success -> {
                when (val consumeResult = consumeGenerationLimitUseCase()) {
                    is ConsumeGenerationLimitResult.Success -> {
                        analyticsTracker.track(
                            eventName = HomeAnalyticsEvent.PredictionRefreshed,
                            params = mapOf(
                                "generation_used_count" to consumeResult.generationLimit.usedCount.toString(),
                                "remaining_count" to consumeResult.generationLimit.remainingCount.toString(),
                                "category" to currentState.selectedCategory.analyticsName,
                            ),
                        )
                        trackPredictionViewed(result.dailyPrediction)
                        recordPredictionView(result.dailyPrediction)
                        _state.value = refreshingState.copy(
                            dailyPrediction = result.dailyPrediction,
                            generationLimit = consumeResult.generationLimit,
                            isRefreshing = false,
                        )
                    }
                    is ConsumeGenerationLimitResult.Blocked -> {
                        _state.value = currentState.copy(generationLimit = consumeResult.generationLimit)
                        _events.tryEmit(HomeEvent.ShowMessage(EXHAUSTED_MESSAGE))
                        trackRefreshLimitReached(consumeResult.generationLimit.dateKey)
                    }
                }
            }
            GenerateManualPredictionResult.Error.EmptyPredictionPool -> {
                analyticsTracker.track(HomeAnalyticsEvent.RefreshFailed)
                _state.value = HomeState.Error()
            }
        }
    }

    private fun sharePrediction() {
        val currentState = _state.value as? HomeState.Content ?: return

        analyticsTracker.track(
            eventName = HomeAnalyticsEvent.ShareClicked,
            params = mapOf("prediction_id" to currentState.dailyPrediction.prediction.id),
        )
        _events.tryEmit(HomeEvent.ShowMessage(SHARE_SOON_MESSAGE))
    }

    private fun selectPrediction(predictionId: String) {
        analyticsTracker.track(
            eventName = HomeAnalyticsEvent.PredictionClicked,
            params = mapOf("prediction_id" to predictionId),
        )
        _events.tryEmit(HomeEvent.PredictionSelected(predictionId))
    }

    private fun trackPredictionViewed(dailyPrediction: DailyPrediction) {
        val prediction = dailyPrediction.prediction
        analyticsTracker.track(
            eventName = HomeAnalyticsEvent.PredictionViewed,
            params = mapOf(
                "prediction_id" to prediction.id,
                "category" to prediction.category.analyticsName,
                "absurdity_level" to prediction.absurdityLevel.toString(),
                "zodiac_sign" to dailyPrediction.zodiacSignId,
            ),
        )
    }

    private fun recordPredictionView(dailyPrediction: DailyPrediction) {
        recordPredictionViewUseCase?.invoke(
            dailyPrediction = dailyPrediction,
            source = HISTORY_SOURCE_HOME,
        )
    }

    private fun trackRefreshLimitReached(dateKey: String) {
        analyticsTracker.track(
            eventName = HomeAnalyticsEvent.PredictionRefreshLimitReached,
            params = mapOf("date_key" to dateKey),
        )
    }

    private fun getSelectedZodiacSign(): ZodiacSignUiModel? {
        val zodiacSignId = onboardingSettingsStorage.getSnapshot().zodiacSignId
        return ZodiacSignUiModel.all.firstOrNull { zodiacSign ->
            zodiacSign.id == zodiacSignId
        }
    }

    private companion object {
        const val EXHAUSTED_MESSAGE = "На сегодня космос выдохся. Возвращайся завтра."
        const val SHARE_SOON_MESSAGE = "Скоро можно будет отправить это в чат."
        const val HISTORY_SOURCE_HOME = "home"
    }
}

private object HomeAnalyticsEvent {
    const val HomeViewed = "home_viewed"
    const val PredictionViewed = "prediction_viewed"
    const val MissingZodiac = "home_missing_zodiac"
    const val EmptyCatalog = "home_empty_catalog"
    const val CategorySelected = "prediction_category_selected"
    const val PredictionRefreshed = "prediction_refreshed"
    const val PredictionRefreshLimitReached = "prediction_refresh_limit_reached"
    const val RefreshFailed = "home_refresh_failed"
    const val ShareClicked = "prediction_share_clicked"
    const val PredictionClicked = "home_prediction_clicked"
}
