package com.example.antihoroscope.domain.prediction

import com.example.antihoroscope.core.time.FakeDateProvider
import com.example.antihoroscope.core.time.createDateSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GenerateDailyPredictionUseCaseTest {
    @Test
    fun sameDateAndSameSignReturnSamePrediction() {
        val useCase = GenerateDailyPredictionUseCase(
            predictionRepository = FakePredictionRepository(
                predictions = listOf(
                    prediction(id = "common-love", category = PredictionCategory.Love),
                    prediction(id = "common-chaos", category = PredictionCategory.Chaos),
                    prediction(id = "aries-money", category = PredictionCategory.Money, zodiacSignId = "aries"),
                ),
            ),
            dateProvider = FakeDateProvider(TestDateSnapshot),
        )
        val params = GenerateDailyPredictionParams(
            zodiacSignId = "aries",
            zodiacSignName = "Овен",
        )

        val firstResult = assertIs<GenerateDailyPredictionResult.Success>(useCase(params))
        val secondResult = assertIs<GenerateDailyPredictionResult.Success>(useCase(params))

        assertEquals(firstResult.dailyPrediction.prediction.id, secondResult.dailyPrediction.prediction.id)
        assertEquals(TestDateSnapshot.dateKey, firstResult.dailyPrediction.dateKey)
        assertEquals(TestDateSnapshot.displayLabel, firstResult.dailyPrediction.dateLabel)
    }

    @Test
    fun zodiacSpecificPredictionCanBeSelectedFromPool() {
        val useCase = GenerateDailyPredictionUseCase(
            predictionRepository = FakePredictionRepository(
                predictions = listOf(
                    prediction(id = "leo-only", category = PredictionCategory.Career, zodiacSignId = "leo"),
                    prediction(id = "aries-only", category = PredictionCategory.Career, zodiacSignId = "aries"),
                ),
            ),
            dateProvider = FakeDateProvider(TestDateSnapshot),
        )

        val result = assertIs<GenerateDailyPredictionResult.Success>(
            useCase(
                GenerateDailyPredictionParams(
                    zodiacSignId = "leo",
                    zodiacSignName = "Лев",
                ),
            ),
        )

        assertEquals("leo-only", result.dailyPrediction.prediction.id)
        assertEquals("leo", result.dailyPrediction.prediction.zodiacSignId)
    }

    @Test
    fun emptyPoolReturnsControlledError() {
        val useCase = GenerateDailyPredictionUseCase(
            predictionRepository = FakePredictionRepository(predictions = emptyList()),
            dateProvider = FakeDateProvider(TestDateSnapshot),
        )

        val result = useCase(
            GenerateDailyPredictionParams(
                zodiacSignId = "aries",
                zodiacSignName = "Овен",
            ),
        )

        assertEquals(GenerateDailyPredictionResult.Error.EmptyPredictionPool, result)
    }
}

internal class FakePredictionRepository(
    private val predictions: List<Prediction>,
) : PredictionRepository {
    override fun getAllPredictions(): List<Prediction> = predictions.toList()

    override fun getPredictionsForZodiac(zodiacSignId: String): List<Prediction> =
        predictions.filter { prediction ->
            prediction.zodiacSignId == null || prediction.zodiacSignId == zodiacSignId
        }

    override fun getPredictionsByCategory(category: PredictionCategory): List<Prediction> =
        predictions.filter { prediction ->
            prediction.category == category
        }
}

internal fun prediction(
    id: String,
    category: PredictionCategory,
    zodiacSignId: String? = null,
    absurdityLevel: Int = 3,
) = Prediction(
    id = id,
    text = "Тестовое предсказание $id",
    category = category,
    zodiacSignId = zodiacSignId,
    absurdityLevel = absurdityLevel,
)

internal val TestDateSnapshot = createDateSnapshot(
    year = 2026,
    monthNumber = 5,
    dayOfMonth = 6,
)
