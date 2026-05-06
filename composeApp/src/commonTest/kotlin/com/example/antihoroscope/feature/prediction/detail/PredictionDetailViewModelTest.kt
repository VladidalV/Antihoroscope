package com.example.antihoroscope.feature.prediction.detail

import com.example.antihoroscope.core.analytics.AnalyticsTracker
import com.example.antihoroscope.domain.prediction.DailyPrediction
import com.example.antihoroscope.domain.prediction.PredictionCategory
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
        val viewModel = PredictionDetailViewModel(
            dailyPrediction = dailyPrediction(),
            analyticsTracker = analyticsTracker,
        )
        val event = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) {
                viewModel.events.first()
            }
        }

        viewModel.onIntent(PredictionDetailIntent.FavoriteClicked)

        assertTrue(viewModel.state.value.isFavorite)
        assertEquals("Сохранено в избранное на этом экране.", viewModel.state.value.feedbackMessage)
        assertEquals(
            PredictionDetailEvent.ShowFavoriteFeedback(
                message = "Сохранено в избранное на этом экране.",
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
        val viewModel = PredictionDetailViewModel(dailyPrediction())

        viewModel.onIntent(PredictionDetailIntent.FavoriteClicked)
        viewModel.onIntent(PredictionDetailIntent.FavoriteClicked)

        assertFalse(viewModel.state.value.isFavorite)
        assertEquals("Убрано из избранного на этом экране.", viewModel.state.value.feedbackMessage)
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
