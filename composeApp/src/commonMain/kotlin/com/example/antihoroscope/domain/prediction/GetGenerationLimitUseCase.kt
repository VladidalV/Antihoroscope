package com.example.antihoroscope.domain.prediction

import com.example.antihoroscope.core.time.DateProvider
import com.example.antihoroscope.data.settings.HomeGenerationLimitSnapshot
import com.example.antihoroscope.data.settings.HomeSettingsStorage

class GetGenerationLimitUseCase(
    private val homeSettingsStorage: HomeSettingsStorage,
    private val dateProvider: DateProvider,
) {
    operator fun invoke(): GenerationLimit {
        val todayDateKey = dateProvider.today().dateKey
        val snapshot = homeSettingsStorage.readGenerationLimit()

        return snapshot.toGenerationLimit(todayDateKey)
    }
}

internal fun HomeGenerationLimitSnapshot.toGenerationLimit(todayDateKey: String): GenerationLimit {
    val usedCountForDate = if (dateKey == todayDateKey) {
        usedCount.coerceIn(0, DailyManualGenerationLimit)
    } else {
        0
    }

    return GenerationLimit(
        dateKey = todayDateKey,
        maxCount = DailyManualGenerationLimit,
        usedCount = usedCountForDate,
    )
}

internal const val DailyManualGenerationLimit = 3
