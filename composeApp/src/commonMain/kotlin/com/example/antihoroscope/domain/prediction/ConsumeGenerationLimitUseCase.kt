package com.example.antihoroscope.domain.prediction

import com.example.antihoroscope.core.time.DateProvider
import com.example.antihoroscope.data.settings.HomeSettingsStorage

class ConsumeGenerationLimitUseCase(
    private val homeSettingsStorage: HomeSettingsStorage,
    private val dateProvider: DateProvider,
) {
    operator fun invoke(): ConsumeGenerationLimitResult {
        val todayDateKey = dateProvider.today().dateKey
        val currentLimit = homeSettingsStorage
            .readGenerationLimit()
            .toGenerationLimit(todayDateKey)

        if (currentLimit.isExhausted) {
            return ConsumeGenerationLimitResult.Blocked(currentLimit)
        }

        val consumedLimit = currentLimit.copy(
            usedCount = currentLimit.usedCount + 1,
        )
        homeSettingsStorage.saveGenerationLimit(
            dateKey = consumedLimit.dateKey,
            usedCount = consumedLimit.usedCount,
        )

        return ConsumeGenerationLimitResult.Success(consumedLimit)
    }
}

sealed interface ConsumeGenerationLimitResult {
    data class Success(
        val generationLimit: GenerationLimit,
    ) : ConsumeGenerationLimitResult

    data class Blocked(
        val generationLimit: GenerationLimit,
    ) : ConsumeGenerationLimitResult
}
