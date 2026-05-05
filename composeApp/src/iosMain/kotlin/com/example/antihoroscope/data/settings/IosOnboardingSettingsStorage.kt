package com.example.antihoroscope.data.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUserDefaults

private class IosOnboardingSettingsStorage(
    private val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : OnboardingSettingsStorage {
    override fun getSnapshot(): OnboardingSettingsSnapshot {
        return OnboardingSettingsSnapshot(
            onboardingCompleted = userDefaults.boolForKey(OnboardingSettingsKeys.ONBOARDING_COMPLETED),
            zodiacSignId = userDefaults.stringForKey(OnboardingSettingsKeys.ZODIAC_SIGN_ID),
            notificationsEnabled = userDefaults.boolForKey(OnboardingSettingsKeys.NOTIFICATIONS_ENABLED),
            notificationHour = if (userDefaults.objectForKey(OnboardingSettingsKeys.NOTIFICATION_HOUR) != null) {
                userDefaults.integerForKey(OnboardingSettingsKeys.NOTIFICATION_HOUR).toInt()
            } else {
                DEFAULT_NOTIFICATION_HOUR
            },
            notificationMinute = if (userDefaults.objectForKey(OnboardingSettingsKeys.NOTIFICATION_MINUTE) != null) {
                userDefaults.integerForKey(OnboardingSettingsKeys.NOTIFICATION_MINUTE).toInt()
            } else {
                DEFAULT_NOTIFICATION_MINUTE
            },
        )
    }

    override fun saveOnboardingCompleted(
        zodiacSignId: String,
        notificationsEnabled: Boolean,
        notificationHour: Int,
        notificationMinute: Int,
    ) {
        userDefaults.setBool(true, OnboardingSettingsKeys.ONBOARDING_COMPLETED)
        userDefaults.setObject(zodiacSignId, OnboardingSettingsKeys.ZODIAC_SIGN_ID)
        userDefaults.setBool(notificationsEnabled, OnboardingSettingsKeys.NOTIFICATIONS_ENABLED)
        userDefaults.setInteger(notificationHour.toLong(), OnboardingSettingsKeys.NOTIFICATION_HOUR)
        userDefaults.setInteger(notificationMinute.toLong(), OnboardingSettingsKeys.NOTIFICATION_MINUTE)
    }
}

@Composable
actual fun rememberOnboardingSettingsStorage(): OnboardingSettingsStorage {
    return remember { IosOnboardingSettingsStorage() }
}
