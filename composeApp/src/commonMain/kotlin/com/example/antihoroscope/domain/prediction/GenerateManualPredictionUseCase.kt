package com.example.antihoroscope.domain.prediction

import com.example.antihoroscope.core.time.DateProvider

class GenerateManualPredictionUseCase(
    private val predictionRepository: PredictionRepository,
    private val dateProvider: DateProvider,
) {
    operator fun invoke(params: GenerateManualPredictionParams): GenerateManualPredictionResult {
        val dateSnapshot = dateProvider.today()
        val predictionPool = predictionRepository
            .getPredictionsForZodiac(params.zodiacSignId)
            .filterByCategory(params.category)
            .withoutCurrentPredictionIfPossible(params.currentPredictionId)

        if (predictionPool.isEmpty()) {
            return GenerateManualPredictionResult.Error.EmptyPredictionPool
        }

        val seed = buildManualSeed(
            dateKey = dateSnapshot.dateKey,
            zodiacSignId = params.zodiacSignId,
            generationIndex = params.generationIndex,
            category = params.category,
        )
        val selectedPrediction = predictionPool[seed.stablePredictionIndex(predictionPool.size)]

        return GenerateManualPredictionResult.Success(
            dailyPrediction = DailyPrediction(
                prediction = selectedPrediction,
                dateKey = dateSnapshot.dateKey,
                dateLabel = dateSnapshot.displayLabel,
                zodiacSignId = params.zodiacSignId,
                zodiacSignName = params.zodiacSignName,
            ),
        )
    }

    private fun List<Prediction>.filterByCategory(category: PredictionCategory?): List<Prediction> =
        if (category == null) {
            this
        } else {
            filter { prediction -> prediction.category == category }
        }

    private fun List<Prediction>.withoutCurrentPredictionIfPossible(
        currentPredictionId: String?,
    ): List<Prediction> {
        if (currentPredictionId == null) {
            return this
        }

        val poolWithoutCurrent = filter { prediction -> prediction.id != currentPredictionId }
        return poolWithoutCurrent.ifEmpty { this }
    }

    private fun buildManualSeed(
        dateKey: String,
        zodiacSignId: String,
        generationIndex: Int,
        category: PredictionCategory?,
    ): String = "$dateKey:$zodiacSignId:manual:$generationIndex:${category?.analyticsName.orEmpty()}"
}

data class GenerateManualPredictionParams(
    val zodiacSignId: String,
    val zodiacSignName: String,
    val generationIndex: Int,
    val category: PredictionCategory? = null,
    val currentPredictionId: String? = null,
)

sealed interface GenerateManualPredictionResult {
    data class Success(
        val dailyPrediction: DailyPrediction,
    ) : GenerateManualPredictionResult

    sealed interface Error : GenerateManualPredictionResult {
        data object EmptyPredictionPool : Error
    }
}
