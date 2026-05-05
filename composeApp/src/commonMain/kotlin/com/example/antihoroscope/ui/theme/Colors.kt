package com.example.antihoroscope.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val CosmicBlack = Color(0xFF07030F)
val CosmicDeepPurple = Color(0xFF160A2F)
val CosmicSurface = Color(0xFF1D1238)
val CosmicSurfaceHigh = Color(0xFF28184C)
val NeonPurple = Color(0xFF9B5CFF)
val NeonMagenta = Color(0xFFFF4FD8)
val NeonCyan = Color(0xFF45E6FF)
val StarWhite = Color(0xFFF7F1FF)
val MoonMuted = Color(0xFFB9A7D8)
val WarningRose = Color(0xFFFF6B9A)

@Immutable
data class AntiHoroscopeColors(
    val cosmicBlack: Color = CosmicBlack,
    val cosmicDeepPurple: Color = CosmicDeepPurple,
    val cosmicSurface: Color = CosmicSurface,
    val cosmicSurfaceHigh: Color = CosmicSurfaceHigh,
    val neonPurple: Color = NeonPurple,
    val neonMagenta: Color = NeonMagenta,
    val neonCyan: Color = NeonCyan,
    val starWhite: Color = StarWhite,
    val moonMuted: Color = MoonMuted,
    val warningRose: Color = WarningRose,
)
