package com.example.antihoroscope.feature.prediction.detail

import com.example.antihoroscope.core.analytics.AnalyticsTracker
import com.example.antihoroscope.data.prediction.history.DefaultPredictionHistoryRepository
import com.example.antihoroscope.domain.prediction.DailyPrediction
import com.example.antihoroscope.domain.prediction.FakePredictionHistoryLocalDataSource
import com.example.antihoroscope.domain.prediction.IsFavoritePredictionUseCase
import com.example.antihoroscope.domain.prediction.PredictionCategory
import com.example.antihoroscope.domain.prediction.RecordPredictionViewUseCase
import com.example.antihoroscope.domain.prediction.ToggleFavoritePredictionUseCase
import com.example.antihoroscope.domain.prediction.prediction
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PredictionDetailViewModelTest {
    @Test
    fun initialStateContainsProvidedDailyPrediction() {
        val dailyPrediction = dailyPrediction()
        val analyticsTracker = FakeAnalyticsTracker()
        val viewModel = PredictionDetailViewModel(
            dailyPrediction = dailyPrediction,
            analyticsTracker = analyticsTracker,
        )

        assertEquals(dailyPrediction, viewModel.state.value.dailyPrediction)
        assertFalse(viewModel.state.value.isFavorite)
        assertNull(viewModel.state.value.feedbackMessage)
        assertEquals(
            AnalyticsEvent(
                name = "prediction_detail_viewed",
                params = expectedAnalyticsParams(),
            ),
            analyticsTracker.events.single(),
        )
    }

    @Test
    fun shareClickUpdatesFeedbackAndEmitsShareEvent() = runBlocking {
        val analyticsTracker = FakeAnalyticsTracker()
        val viewModel = PredictionDetailViewModel(
            dailyPrediction = dailyPrediction(),
            analyticsTracker = analyticsTracker,
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) {
                viewModel.events.first()
            }
        }

        viewModel.onIntent(PredictionDetailIntent.ShareClicked)

        assertEquals("Скоро можно будет отправить это в чат.", viewModel.state.value.feedbackMessage)
        assertEquals(
            PredictionDetailEvent.ShowShareFeedback("Скоро можно будет отправить это в чат."),
            event.await(),
        )
        assertEquals(
            AnalyticsEvent(
                name = "prediction_detail_share_clicked",
                params = expectedAnalyticsParams(),
            ),
            analyticsTracker.events.last(),
        )
    }

    @Test
    fun favoriteClickTogglesFavoriteStateAndEmitsFavoriteEvent() = runBlocking {
        val analyticsTracker = FakeAnalyticsTracker()
        val favoriteDependencies = createFavoriteDependencies()
        val viewModel = PredictionDetailViewModel(
            dailyPrediction = dailyPrediction(),
            isFavoritePredictionUseCase = favoriteDependencies.isFavoritePredictionUseCase,
            toggleFavoritePredictionUseCase = favoriteDependencies.toggleFavoritePredictionUseCase,
            analyticsTracker = analyticsTracker,
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) {
                viewModel.events.first()
            }
        }

        viewModel.onIntent(PredictionDetailIntent.FavoriteClicked)

        assertTrue(viewModel.state.value.isFavorite)
        assertTrue(favoriteDependencies.isFavoritePredictionUseCase("detail-chaos"))
        assertEquals("Сохранено в избранное.", viewModel.state.value.feedbackMessage)
        assertEquals(
            PredictionDetailEvent.ShowFavoriteFeedback(
                message = "Сохранено в избранное.",
                isFavorite = true,
            ),
            event.await(),
        )
        assertEquals(
            AnalyticsEvent(
                name = "prediction_favorite_clicked",
                params = expectedAnalyticsParams() + ("is_favorite" to "true"),
            ),
            analyticsTracker.events.last(),
        )
    }

    @Test
    fun secondFavoriteClickRemovesFavoriteState() {
        val favoriteDependencies = createFavoriteDependencies()
        val viewModel = PredictionDetailViewModel(
            dailyPrediction = dailyPrediction(),
            isFavoritePredictionUseCase = favoriteDependencies.isFavoritePredictionUseCase,
            toggleFavoritePredictionUseCase = favoriteDependencies.toggleFavoritePredictionUseCase,
        )

        viewModel.onIntent(PredictionDetailIntent.FavoriteClicked)
        viewModel.onIntent(PredictionDetailIntent.FavoriteClicked)

        assertFalse(viewModel.state.value.isFavorite)
        assertFalse(favoriteDependencies.isFavoritePredictionUseCase("detail-chaos"))
        assertEquals("Убрано из избранного.", viewModel.state.value.feedbackMessage)
    }

    @Test
    fun initialFavoriteStateIsReadFromPersistence() {
        val favoriteDependencies = createFavoriteDependencies()
        favoriteDependencies.toggleFavoritePredictionUseCase(dailyPrediction())

        val viewModel = PredictionDetailViewModel(
            dailyPrediction = dailyPrediction(),
            isFavoritePredictionUseCase = favoriteDependencies.isFavoritePredictionUseCase,
            toggleFavoritePredictionUseCase = favoriteDependencies.toggleFavoritePredictionUseCase,
        )

        assertTrue(viewModel.state.value.isFavorite)
    }

    @Test
    fun initRecordsDetailHistoryWithIdempotentSource() {
        val favoriteDependencies = createFavoriteDependencies()
        val recordPredictionViewUseCase = RecordPredictionViewUseCase(
            repository = favoriteDependencies.repository,
            currentTimeMillis = { 3_000L },
        )

        PredictionDetailViewModel(
            dailyPrediction = dailyPrediction(),
            recordPredictionViewUseCase = recordPredictionViewUseCase,
        )
        PredictionDetailViewModel(
            dailyPrediction = dailyPrediction(),
            recordPredictionViewUseCase = recordPredictionViewUseCase,
        )

        val history = favoriteDependencies.repository.getHistory(limit = 10)
        assertEquals(1, history.size)
        assertEquals("detail", history.single().source)
    }

    @Test
    fun nextClickUpdatesFeedbackAndEmitsNextEvent() = runBlocking {
        val viewModel = PredictionDetailViewModel(dailyPrediction())
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) {
                viewModel.events.first()
            }
        }

        viewModel.onIntent(PredictionDetailIntent.NextClicked)

        assertEquals(
            "Следующее предсказание появится в одном из следующих релизов.",
            viewModel.state.value.feedbackMessage,
        )
        assertEquals(
            PredictionDetailEvent.ShowNextFeedback(
                message = "Следующее предсказание появится в одном из следующих релизов.",
            ),
            event.await(),
        )
    }

    @Test
    fun backClickEmitsNavigateBackEvent() = runBlocking {
        val analyticsTracker = FakeAnalyticsTracker()
        val viewModel = PredictionDetailViewModel(
            dailyPrediction = dailyPrediction(),
            analyticsTracker = analyticsTracker,
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) {
                viewModel.events.first()
            }
        }

        viewModel.onIntent(PredictionDetailIntent.BackClicked)

        assertEquals(PredictionDetailEvent.NavigateBack, event.await())
        assertEquals(
            AnalyticsEvent(
                name = "prediction_detail_back_clicked",
                params = expectedAnalyticsParams(),
            ),
            analyticsTracker.events.last(),
        )
    }

    @Test
    fun feedbackShownClearsTransientFeedbackMessage() {
        val viewModel = PredictionDetailViewModel(dailyPrediction())
        viewModel.onIntent(PredictionDetailIntent.ShareClicked)

        viewModel.onIntent(PredictionDetailIntent.FeedbackShown)

        assertNull(viewModel.state.value.feedbackMessage)
    }
}

private data class FavoriteDependencies(
    val repository: DefaultPredictionHistoryRepository,
    val isFavoritePredictionUseCase: IsFavoritePredictionUseCase,
    val toggleFavoritePredictionUseCase: ToggleFavoritePredictionUseCase,
)

private fun createFavoriteDependencies(): FavoriteDependencies {
    val repository = DefaultPredictionHistoryRepository(FakePredictionHistoryLocalDataSource())
    return FavoriteDependencies(
        repository = repository,
        isFavoritePredictionUseCase = IsFavoritePredictionUseCase(repository),
        toggleFavoritePredictionUseCase = ToggleFavoritePredictionUseCase(
            repository = repository,
            currentTimeMillis = { 1_000L },
        ),
    )
}

private class FakeAnalyticsTracker : AnalyticsTracker {
    val events = mutableListOf<AnalyticsEvent>()

    override fun track(
        eventName: String,
        params: Map<String, String>,
    ) {
        events += AnalyticsEvent(
            name = eventName,
            params = params,
        )
    }
}

private data class AnalyticsEvent(
    val name: String,
    val params: Map<String, String>,
)

private fun expectedAnalyticsParams() = mapOf(
    "prediction_id" to "detail-chaos",
    "zodiac_sign" to "aries",
    "category" to "chaos",
    "absurdity_level" to "3",
    "date" to "2026-05-06",
    "source" to "home",
)

private fun dailyPrediction() = DailyPrediction(
    prediction = prediction(
        id = "detail-chaos",
        category = PredictionCategory.Chaos,
        zodiacSignId = "aries",
    ),
    dateKey = "2026-05-06",
    dateLabel = "6 мая 2026",
    zodiacSignId = "aries",
    zodiacSignName = "Овен",
)
