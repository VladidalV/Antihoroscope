package com.example.antihoroscope.feature.home

import com.example.antihoroscope.core.analytics.AnalyticsTracker
import com.example.antihoroscope.core.time.FakeDateProvider
import com.example.antihoroscope.data.settings.HomeGenerationLimitSnapshot
import com.example.antihoroscope.data.settings.HomeSettingsStorage
import com.example.antihoroscope.data.settings.OnboardingSettingsSnapshot
import com.example.antihoroscope.data.settings.OnboardingSettingsStorage
import com.example.antihoroscope.domain.prediction.ConsumeGenerationLimitUseCase
import com.example.antihoroscope.domain.prediction.GenerateDailyPredictionUseCase
import com.example.antihoroscope.domain.prediction.GenerateManualPredictionUseCase
import com.example.antihoroscope.domain.prediction.GetGenerationLimitUseCase
import com.example.antihoroscope.domain.prediction.Prediction
import com.example.antihoroscope.domain.prediction.PredictionCategory
import com.example.antihoroscope.domain.prediction.FakePredictionRepository
import com.example.antihoroscope.domain.prediction.TestDateSnapshot
import com.example.antihoroscope.domain.prediction.prediction
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class HomeViewModelTest {
    @Test
    fun screenShownMovesInitialLoadingToContent() {
        val viewModel = createHomeViewModel()

        assertEquals(HomeState.Loading, viewModel.state.value)

        viewModel.onIntent(HomeIntent.ScreenShown)

        val content = assertIs<HomeState.Content>(viewModel.state.value)
        assertEquals("aries", content.dailyPrediction.zodiacSignId)
        assertEquals("Овен", content.dailyPrediction.zodiacSignName)
        assertEquals(3, content.generationLimit.remainingCount)
        assertFalse(content.isRefreshing)
    }

    @Test
    fun missingZodiacMovesToMissingZodiacState() {
        val viewModel = createHomeViewModel(
            onboardingSettingsStorage = FakeOnboardingSettingsStorage(zodiacSignId = null),
        )

        viewModel.onIntent(HomeIntent.ScreenShown)

        assertIs<HomeState.MissingZodiac>(viewModel.state.value)
    }

    @Test
    fun categorySelectedUpdatesContentState() {
        val viewModel = createHomeViewModel()
        viewModel.onIntent(HomeIntent.ScreenShown)

        viewModel.onIntent(HomeIntent.CategorySelected(PredictionCategory.Money))

        val content = assertIs<HomeState.Content>(viewModel.state.value)
        assertEquals(PredictionCategory.Money, content.selectedCategory)
    }

    @Test
    fun refreshSuccessUpdatesPredictionAndConsumesLimit() {
        val viewModel = createHomeViewModel(
            predictions = listOf(
                prediction(id = "daily-chaos", category = PredictionCategory.Chaos),
                prediction(id = "manual-chaos", category = PredictionCategory.Chaos),
            ),
        )
        viewModel.onIntent(HomeIntent.ScreenShown)
        val beforeRefresh = assertIs<HomeState.Content>(viewModel.state.value)

        viewModel.onIntent(HomeIntent.RefreshClicked)

        val afterRefresh = assertIs<HomeState.Content>(viewModel.state.value)
        assertNotEquals(
            beforeRefresh.dailyPrediction.prediction.id,
            afterRefresh.dailyPrediction.prediction.id,
        )
        assertEquals(1, afterRefresh.generationLimit.usedCount)
        assertEquals(2, afterRefresh.generationLimit.remainingCount)
        assertFalse(afterRefresh.isRefreshing)
    }

    @Test
    fun exhaustedRefreshShowsMessageAndKeepsContentState() = runBlocking {
        val viewModel = createHomeViewModel(
            homeSettingsStorage = FakeHomeSettingsStorage(
                snapshot = HomeGenerationLimitSnapshot(
                    dateKey = TestDateSnapshot.dateKey,
                    usedCount = 3,
                ),
            ),
        )
        viewModel.onIntent(HomeIntent.ScreenShown)

        val event = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) {
                viewModel.events.first()
            }
        }
        viewModel.onIntent(HomeIntent.RefreshClicked)

        val content = assertIs<HomeState.Content>(viewModel.state.value)
        assertTrue(content.generationLimit.isExhausted)
        assertEquals(
            HomeEvent.ShowMessage("На сегодня космос выдохся. Возвращайся завтра."),
            event.await(),
        )
    }

    @Test
    fun shareClickShowsDeferredShareMessageAndTracksAnalytics() = runBlocking {
        val analyticsTracker = FakeAnalyticsTracker()
        val viewModel = createHomeViewModel(
            analyticsTracker = analyticsTracker,
        )
        viewModel.onIntent(HomeIntent.ScreenShown)

        val event = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) {
                viewModel.events.first()
            }
        }
        viewModel.onIntent(HomeIntent.ShareClicked)

        assertEquals(
            HomeEvent.ShowMessage("Скоро можно будет отправить это в чат."),
            event.await(),
        )
        assertTrue(
            analyticsTracker.eventNames.contains("prediction_share_clicked"),
        )
    }

    private fun createHomeViewModel(
        onboardingSettingsStorage: OnboardingSettingsStorage = FakeOnboardingSettingsStorage(),
        homeSettingsStorage: HomeSettingsStorage = FakeHomeSettingsStorage(),
        predictions: List<Prediction> = DefaultPredictions,
        analyticsTracker: AnalyticsTracker = FakeAnalyticsTracker(),
    ): HomeViewModel {
        val dateProvider = FakeDateProvider(TestDateSnapshot)
        val predictionRepository = FakePredictionRepository(predictions)

        return HomeViewModel(
            onboardingSettingsStorage = onboardingSettingsStorage,
            generateDailyPredictionUseCase = GenerateDailyPredictionUseCase(
                predictionRepository = predictionRepository,
                dateProvider = dateProvider,
            ),
            generateManualPredictionUseCase = GenerateManualPredictionUseCase(
                predictionRepository = predictionRepository,
                dateProvider = dateProvider,
            ),
            getGenerationLimitUseCase = GetGenerationLimitUseCase(
                homeSettingsStorage = homeSettingsStorage,
                dateProvider = dateProvider,
            ),
            consumeGenerationLimitUseCase = ConsumeGenerationLimitUseCase(
                homeSettingsStorage = homeSettingsStorage,
                dateProvider = dateProvider,
            ),
            analyticsTracker = analyticsTracker,
        )
    }
}

private class FakeOnboardingSettingsStorage(
    private val zodiacSignId: String? = "aries",
) : OnboardingSettingsStorage {
    override fun getSnapshot(): OnboardingSettingsSnapshot {
        return OnboardingSettingsSnapshot(
            onboardingCompleted = zodiacSignId != null,
            zodiacSignId = zodiacSignId,
        )
    }

    override fun saveOnboardingCompleted(
        zodiacSignId: String,
        notificationsEnabled: Boolean,
        notificationHour: Int,
        notificationMinute: Int,
    ) = Unit
}

private class FakeHomeSettingsStorage(
    var snapshot: HomeGenerationLimitSnapshot = HomeGenerationLimitSnapshot(),
) : HomeSettingsStorage {
    override fun readGenerationLimit(): HomeGenerationLimitSnapshot = snapshot

    override fun saveGenerationLimit(
        dateKey: String,
        usedCount: Int,
    ) {
        snapshot = HomeGenerationLimitSnapshot(
            dateKey = dateKey,
            usedCount = usedCount,
        )
    }

    override fun resetGenerationLimit() {
        snapshot = HomeGenerationLimitSnapshot()
    }
}

private class FakeAnalyticsTracker : AnalyticsTracker {
    val eventNames = mutableListOf<String>()

    override fun track(
        eventName: String,
        params: Map<String, String>,
    ) {
        eventNames += eventName
    }
}

private val DefaultPredictions = listOf(
    prediction(id = "love-common", category = PredictionCategory.Love),
    prediction(id = "money-common", category = PredictionCategory.Money),
    prediction(id = "chaos-common", category = PredictionCategory.Chaos),
    prediction(id = "chaos-aries", category = PredictionCategory.Chaos, zodiacSignId = "aries"),
)
