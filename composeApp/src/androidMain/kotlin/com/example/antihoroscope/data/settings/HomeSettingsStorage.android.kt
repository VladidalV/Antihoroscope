package com.example.antihoroscope.data.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private class AndroidHomeSettingsStorage(
    context: Context,
) : HomeSettingsStorage {
    private val preferences = context.applicationContext.getSharedPreferences(
        HomeSettingsKeys.STORE_NAME,
        Context.MODE_PRIVATE,
    )

    override fun readGenerationLimit(): HomeGenerationLimitSnapshot {
        return HomeGenerationLimitSnapshot(
            dateKey = preferences.getString(HomeSettingsKeys.GENERATION_LIMIT_DATE, null),
            usedCount = preferences.getInt(HomeSettingsKeys.GENERATION_LIMIT_USED_COUNT, 0),
        )
    }

    override fun saveGenerationLimit(
        dateKey: String,
        usedCount: Int,
    ) {
        preferences.edit()
            .putString(HomeSettingsKeys.GENERATION_LIMIT_DATE, dateKey)
            .putInt(HomeSettingsKeys.GENERATION_LIMIT_USED_COUNT, usedCount)
            .apply()
    }

    override fun resetGenerationLimit() {
        preferences.edit()
            .remove(HomeSettingsKeys.GENERATION_LIMIT_DATE)
            .remove(HomeSettingsKeys.GENERATION_LIMIT_USED_COUNT)
            .apply()
    }
}

@Composable
actual fun rememberHomeSettingsStorage(): HomeSettingsStorage {
    val context = LocalContext.current
    return remember(context) { AndroidHomeSettingsStorage(context) }
}
