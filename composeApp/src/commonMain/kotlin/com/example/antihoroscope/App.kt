package com.example.antihoroscope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.antihoroscope.core.time.createSystemDateProvider
import com.example.antihoroscope.data.prediction.InMemoryPredictionRepository
import com.example.antihoroscope.data.settings.rememberHomeSettingsStorage
import com.example.antihoroscope.data.settings.rememberOnboardingSettingsStorage
import com.example.antihoroscope.domain.onboarding.CompleteOnboardingParams
import com.example.antihoroscope.domain.onboarding.CompleteOnboardingResult
import com.example.antihoroscope.domain.onboarding.CompleteOnboardingUseCase
import com.example.antihoroscope.domain.onboarding.ObserveOnboardingStatusUseCase
import com.example.antihoroscope.domain.prediction.ConsumeGenerationLimitUseCase
import com.example.antihoroscope.domain.prediction.DailyPrediction
import com.example.antihoroscope.domain.prediction.GenerateDailyPredictionUseCase
import com.example.antihoroscope.domain.prediction.GenerateManualPredictionUseCase
import com.example.antihoroscope.domain.prediction.GetGenerationLimitUseCase
import com.example.antihoroscope.feature.home.HomeEvent
import com.example.antihoroscope.feature.home.HomeIntent
import com.example.antihoroscope.feature.home.HomeScreen
import com.example.antihoroscope.feature.home.HomeState
import com.example.antihoroscope.feature.home.HomeViewModel
import com.example.antihoroscope.feature.onboarding.OnboardingScreen
import com.example.antihoroscope.feature.prediction.detail.PredictionDetailEvent
import com.example.antihoroscope.feature.prediction.detail.PredictionDetailIntent
import com.example.antihoroscope.feature.prediction.detail.PredictionDetailScreen
import com.example.antihoroscope.feature.prediction.detail.PredictionDetailViewModel
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {
    AntiHoroscopeTheme {
        val onboardingSettingsStorage = rememberOnboardingSettingsStorage()
        val homeSettingsStorage = rememberHomeSettingsStorage()
        val dateProvider = remember { createSystemDateProvider() }
        val predictionRepository = remember { InMemoryPredictionRepository() }
        val observeOnboardingStatusUseCase = remember(onboardingSettingsStorage) {
            ObserveOnboardingStatusUseCase(onboardingSettingsStorage)
        }
        val completeOnboardingUseCase = remember(onboardingSettingsStorage) {
            CompleteOnboardingUseCase(onboardingSettingsStorage)
        }
        val generateDailyPredictionUseCase = remember(predictionRepository, dateProvider) {
            GenerateDailyPredictionUseCase(
                predictionRepository = predictionRepository,
                dateProvider = dateProvider,
            )
        }
        val generateManualPredictionUseCase = remember(predictionRepository, dateProvider) {
            GenerateManualPredictionUseCase(
                predictionRepository = predictionRepository,
                dateProvider = dateProvider,
            )
        }
        val getGenerationLimitUseCase = remember(homeSettingsStorage, dateProvider) {
            GetGenerationLimitUseCase(
                homeSettingsStorage = homeSettingsStorage,
                dateProvider = dateProvider,
            )
        }
        val consumeGenerationLimitUseCase = remember(homeSettingsStorage, dateProvider) {
            ConsumeGenerationLimitUseCase(
                homeSettingsStorage = homeSettingsStorage,
                dateProvider = dateProvider,
            )
        }
        val homeViewModel = remember(
            onboardingSettingsStorage,
            generateDailyPredictionUseCase,
            generateManualPredictionUseCase,
            getGenerationLimitUseCase,
            consumeGenerationLimitUseCase,
        ) {
            HomeViewModel(
                onboardingSettingsStorage = onboardingSettingsStorage,
                generateDailyPredictionUseCase = generateDailyPredictionUseCase,
                generateManualPredictionUseCase = generateManualPredictionUseCase,
                getGenerationLimitUseCase = getGenerationLimitUseCase,
                consumeGenerationLimitUseCase = consumeGenerationLimitUseCase,
            )
        }
        val onboardingStatus = remember(observeOnboardingStatusUseCase) {
            observeOnboardingStatusUseCase()
        }
        var isOnboardingCompleted by remember {
            mutableStateOf(onboardingStatus.isCompleted)
        }
        var homeMessage by remember {
            mutableStateOf<String?>(null)
        }
        var selectedDetailPrediction by remember {
            mutableStateOf<DailyPrediction?>(null)
        }

        if (isOnboardingCompleted) {
            LaunchedEffect(homeViewModel) {
                homeViewModel.onIntent(HomeIntent.ScreenShown)
            }
            LaunchedEffect(homeViewModel) {
                homeViewModel.events.collect { event ->
                    when (event) {
                        is HomeEvent.ShowMessage -> {
                            homeMessage = event.message
                            delay(HOME_MESSAGE_DURATION_MILLIS)
                            if (homeMessage == event.message) {
                                homeMessage = null
                            }
                        }
                        is HomeEvent.PredictionSelected -> {
                            val contentState = homeViewModel.state.value as? HomeState.Content
                            val dailyPrediction = contentState?.dailyPrediction

                            if (dailyPrediction?.prediction?.id == event.predictionId) {
                                homeMessage = null
                                selectedDetailPrediction = dailyPrediction
                            }
                        }
                    }
                }
            }

            val homeState by homeViewModel.state.collectAsState()
            val detailPrediction = selectedDetailPrediction

            if (detailPrediction != null) {
                val detailViewModel = remember(
                    detailPrediction.prediction.id,
                    detailPrediction.dateKey,
                ) {
                    PredictionDetailViewModel(detailPrediction)
                }
                val detailState by detailViewModel.state.collectAsState()

                LaunchedEffect(detailViewModel) {
                    detailViewModel.events.collect { event ->
                        when (event) {
                            PredictionDetailEvent.NavigateBack -> {
                                selectedDetailPrediction = null
                            }
                            is PredictionDetailEvent.ShowFavoriteFeedback -> {
                                detailViewModel.clearFeedbackAfterDelay(event.message)
                            }
                            is PredictionDetailEvent.ShowNextFeedback -> {
                                detailViewModel.clearFeedbackAfterDelay(event.message)
                            }
                            is PredictionDetailEvent.ShowShareFeedback -> {
                                detailViewModel.clearFeedbackAfterDelay(event.message)
                            }
                        }
                    }
                }

                PredictionDetailScreen(
                    state = detailState,
                    onIntent = detailViewModel::onIntent,
                )
            } else {
                HomeScreen(
                    state = homeState,
                    message = homeMessage,
                    onIntent = homeViewModel::onIntent,
                )
            }
        } else {
            OnboardingScreen(
                onCompleted = { result ->
                    val completionParams = CompleteOnboardingParams(
                        zodiacSignId = result.zodiacSignId,
                        notificationsEnabled = result.notificationsEnabled,
                        notificationHour = result.notificationTime.hour,
                        notificationMinute = result.notificationTime.minute,
                    )

                    when (val completionResult = completeOnboardingUseCase(completionParams)) {
                        is CompleteOnboardingResult.Success -> {
                            isOnboardingCompleted = true
                        }
                        CompleteOnboardingResult.Error.UnknownZodiacSign -> Unit
                    }
                },
            )
        }
    }
}

private const val HOME_MESSAGE_DURATION_MILLIS = 2_200L
private const val DETAIL_MESSAGE_DURATION_MILLIS = 2_200L

private suspend fun PredictionDetailViewModel.clearFeedbackAfterDelay(message: String) {
    delay(DETAIL_MESSAGE_DURATION_MILLIS)
    if (state.value.feedbackMessage == message) {
        onIntent(PredictionDetailIntent.FeedbackShown)
    }
}
