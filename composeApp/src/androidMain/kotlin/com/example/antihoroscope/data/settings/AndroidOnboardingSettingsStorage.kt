package com.example.antihoroscope.data.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private class AndroidOnboardingSettingsStorage(
    context: Context,
) : OnboardingSettingsStorage {
    private val preferences = context.applicationContext.getSharedPreferences(
        OnboardingSettingsKeys.STORE_NAME,
        Context.MODE_PRIVATE,
    )

    override fun getSnapshot(): OnboardingSettingsSnapshot {
        return OnboardingSettingsSnapshot(
            onboardingCompleted = preferences.getBoolean(
                OnboardingSettingsKeys.ONBOARDING_COMPLETED,
                false,
            ),
            zodiacSignId = preferences.getString(OnboardingSettingsKeys.ZODIAC_SIGN_ID, null),
            notificationsEnabled = preferences.getBoolean(
                OnboardingSettingsKeys.NOTIFICATIONS_ENABLED,
                false,
            ),
            notificationHour = preferences.getInt(
                OnboardingSettingsKeys.NOTIFICATION_HOUR,
                DEFAULT_NOTIFICATION_HOUR,
            ),
            notificationMinute = preferences.getInt(
                OnboardingSettingsKeys.NOTIFICATION_MINUTE,
                DEFAULT_NOTIFICATION_MINUTE,
            ),
        )
    }

    override fun saveOnboardingCompleted(
        zodiacSignId: String,
        notificationsEnabled: Boolean,
        notificationHour: Int,
        notificationMinute: Int,
    ) {
        preferences.edit()
            .putBoolean(OnboardingSettingsKeys.ONBOARDING_COMPLETED, true)
            .putString(OnboardingSettingsKeys.ZODIAC_SIGN_ID, zodiacSignId)
            .putBoolean(OnboardingSettingsKeys.NOTIFICATIONS_ENABLED, notificationsEnabled)
            .putInt(OnboardingSettingsKeys.NOTIFICATION_HOUR, notificationHour)
            .putInt(OnboardingSettingsKeys.NOTIFICATION_MINUTE, notificationMinute)
            .apply()
    }
}

@Composable
actual fun rememberOnboardingSettingsStorage(): OnboardingSettingsStorage {
    val context = LocalContext.current
    return remember(context) { AndroidOnboardingSettingsStorage(context) }
}
