package com.example.antihoroscope.data.settings

import androidx.compose.runtime.Composable

interface HomeSettingsStorage {
    fun readGenerationLimit(): HomeGenerationLimitSnapshot

    fun saveGenerationLimit(
        dateKey: String,
        usedCount: Int,
    )

    fun resetGenerationLimit()
}

data class HomeGenerationLimitSnapshot(
    val dateKey: String? = null,
    val usedCount: Int = 0,
)

@Composable
expect fun rememberHomeSettingsStorage(): HomeSettingsStorage

object HomeSettingsKeys {
    const val STORE_NAME = "antihoroscope_home"
    const val GENERATION_LIMIT_DATE = "home_generation_limit_date"
    const val GENERATION_LIMIT_USED_COUNT = "home_generation_limit_used_count"
}
