package com.example.antihoroscope.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val AntiHoroscopeColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = StarWhite,
    secondary = NeonMagenta,
    onSecondary = StarWhite,
    tertiary = NeonCyan,
    onTertiary = CosmicBlack,
    background = CosmicBlack,
    onBackground = StarWhite,
    surface = CosmicSurface,
    onSurface = StarWhite,
    surfaceVariant = CosmicSurfaceHigh,
    onSurfaceVariant = MoonMuted,
    error = WarningRose,
    onError = StarWhite,
    outline = NeonPurple.copy(alpha = 0.44f),
    scrim = Color.Black.copy(alpha = 0.72f),
)

private val LocalAntiHoroscopeColors = staticCompositionLocalOf {
    AntiHoroscopeColors()
}

object AntiHoroscopeTheme {
    val colors: AntiHoroscopeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAntiHoroscopeColors.current
}

@Composable
fun AntiHoroscopeTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAntiHoroscopeColors provides AntiHoroscopeColors(),
    ) {
        MaterialTheme(
            colorScheme = AntiHoroscopeColorScheme,
            typography = AntiHoroscopeTypography,
            content = content,
        )
    }
}
