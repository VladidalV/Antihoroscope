package com.example.antihoroscope.data.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUserDefaults

private class IosHomeSettingsStorage(
    private val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : HomeSettingsStorage {
    override fun readGenerationLimit(): HomeGenerationLimitSnapshot {
        return HomeGenerationLimitSnapshot(
            dateKey = userDefaults.stringForKey(HomeSettingsKeys.GENERATION_LIMIT_DATE),
            usedCount = if (userDefaults.objectForKey(HomeSettingsKeys.GENERATION_LIMIT_USED_COUNT) != null) {
                userDefaults.integerForKey(HomeSettingsKeys.GENERATION_LIMIT_USED_COUNT).toInt()
            } else {
                0
            },
        )
    }

    override fun saveGenerationLimit(
        dateKey: String,
        usedCount: Int,
    ) {
        userDefaults.setObject(dateKey, HomeSettingsKeys.GENERATION_LIMIT_DATE)
        userDefaults.setInteger(usedCount.toLong(), HomeSettingsKeys.GENERATION_LIMIT_USED_COUNT)
    }

    override fun resetGenerationLimit() {
        userDefaults.removeObjectForKey(HomeSettingsKeys.GENERATION_LIMIT_DATE)
        userDefaults.removeObjectForKey(HomeSettingsKeys.GENERATION_LIMIT_USED_COUNT)
    }
}

@Composable
actual fun rememberHomeSettingsStorage(): HomeSettingsStorage {
    return remember { IosHomeSettingsStorage() }
}
