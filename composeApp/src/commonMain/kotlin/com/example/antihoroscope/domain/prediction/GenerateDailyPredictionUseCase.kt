package com.example.antihoroscope.domain.prediction

import com.example.antihoroscope.core.time.DateProvider

class GenerateDailyPredictionUseCase(
    private val predictionRepository: PredictionRepository,
    private val dateProvider: DateProvider,
) {
    operator fun invoke(params: GenerateDailyPredictionParams): GenerateDailyPredictionResult {
        val dateSnapshot = dateProvider.today()
        val predictionPool = predictionRepository.getPredictionsForZodiac(params.zodiacSignId)

        if (predictionPool.isEmpty()) {
            return GenerateDailyPredictionResult.Error.EmptyPredictionPool
        }

        val seed = "${dateSnapshot.dateKey}:${params.zodiacSignId}:daily"
        val selectedPrediction = predictionPool[seed.stablePredictionIndex(predictionPool.size)]

        return GenerateDailyPredictionResult.Success(
            dailyPrediction = DailyPrediction(
                prediction = selectedPrediction,
                dateKey = dateSnapshot.dateKey,
                dateLabel = dateSnapshot.displayLabel,
                zodiacSignId = params.zodiacSignId,
                zodiacSignName = params.zodiacSignName,
            ),
        )
    }
}

data class GenerateDailyPredictionParams(
    val zodiacSignId: String,
    val zodiacSignName: String,
)

sealed interface GenerateDailyPredictionResult {
    data class Success(
        val dailyPrediction: DailyPrediction,
    ) : GenerateDailyPredictionResult

    sealed interface Error : GenerateDailyPredictionResult {
        data object EmptyPredictionPool : Error
    }
}
