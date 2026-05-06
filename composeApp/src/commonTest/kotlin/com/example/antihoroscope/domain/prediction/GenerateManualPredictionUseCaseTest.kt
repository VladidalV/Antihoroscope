package com.example.antihoroscope.domain.prediction

import com.example.antihoroscope.core.time.FakeDateProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class GenerateManualPredictionUseCaseTest {
    @Test
    fun categoryFilterAffectsPredictionPool() {
        val useCase = GenerateManualPredictionUseCase(
            predictionRepository = FakePredictionRepository(
                predictions = listOf(
                    prediction(id = "love-common", category = PredictionCategory.Love),
                    prediction(id = "money-common", category = PredictionCategory.Money),
                    prediction(id = "chaos-common", category = PredictionCategory.Chaos),
                ),
            ),
            dateProvider = FakeDateProvider(TestDateSnapshot),
        )

        val result = assertIs<GenerateManualPredictionResult.Success>(
            useCase(
                GenerateManualPredictionParams(
                    zodiacSignId = "aries",
                    zodiacSignName = "Овен",
                    generationIndex = 1,
                    category = PredictionCategory.Money,
                ),
            ),
        )

        assertEquals(PredictionCategory.Money, result.dailyPrediction.prediction.category)
        assertEquals("money-common", result.dailyPrediction.prediction.id)
    }

    @Test
    fun manualGenerationExcludesCurrentPredictionWhenAlternativeExists() {
        val useCase = GenerateManualPredictionUseCase(
            predictionRepository = FakePredictionRepository(
                predictions = listOf(
                    prediction(id = "current-love", category = PredictionCategory.Love),
                    prediction(id = "next-love", category = PredictionCategory.Love),
                ),
            ),
            dateProvider = FakeDateProvider(TestDateSnapshot),
        )

        val result = assertIs<GenerateManualPredictionResult.Success>(
            useCase(
                GenerateManualPredictionParams(
                    zodiacSignId = "aries",
                    zodiacSignName = "Овен",
                    generationIndex = 1,
                    category = PredictionCategory.Love,
                    currentPredictionId = "current-love",
                ),
            ),
        )

        assertNotEquals("current-love", result.dailyPrediction.prediction.id)
        assertEquals("next-love", result.dailyPrediction.prediction.id)
    }

    @Test
    fun manualGenerationKeepsCurrentPredictionWhenItIsTheOnlyOption() {
        val useCase = GenerateManualPredictionUseCase(
            predictionRepository = FakePredictionRepository(
                predictions = listOf(
                    prediction(id = "only-chaos", category = PredictionCategory.Chaos),
                ),
            ),
            dateProvider = FakeDateProvider(TestDateSnapshot),
        )

        val result = assertIs<GenerateManualPredictionResult.Success>(
            useCase(
                GenerateManualPredictionParams(
                    zodiacSignId = "aries",
                    zodiacSignName = "Овен",
                    generationIndex = 1,
                    category = PredictionCategory.Chaos,
                    currentPredictionId = "only-chaos",
                ),
            ),
        )

        assertEquals("only-chaos", result.dailyPrediction.prediction.id)
    }

    @Test
    fun emptyPoolReturnsControlledError() {
        val useCase = GenerateManualPredictionUseCase(
            predictionRepository = FakePredictionRepository(
                predictions = listOf(
                    prediction(id = "love-common", category = PredictionCategory.Love),
                ),
            ),
            dateProvider = FakeDateProvider(TestDateSnapshot),
        )

        val result = useCase(
            GenerateManualPredictionParams(
                zodiacSignId = "aries",
                zodiacSignName = "Овен",
                generationIndex = 1,
                category = PredictionCategory.Money,
            ),
        )

        assertEquals(GenerateManualPredictionResult.Error.EmptyPredictionPool, result)
    }
}
