package com.example.antihoroscope.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = AntiHoroscopeTheme.colors
    val stars = remember { cosmicStars() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.cosmicBlack,
                        colors.cosmicDeepPurple,
                        colors.cosmicBlack,
                    ),
                ),
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGlowOrb(
                center = Offset(size.width * 0.18f, size.height * 0.18f),
                radius = size.minDimension * 0.42f,
                color = colors.neonPurple.copy(alpha = 0.16f),
            )
            drawGlowOrb(
                center = Offset(size.width * 0.86f, size.height * 0.38f),
                radius = size.minDimension * 0.34f,
                color = colors.neonMagenta.copy(alpha = 0.10f),
            )
            drawGlowOrb(
                center = Offset(size.width * 0.52f, size.height * 0.92f),
                radius = size.minDimension * 0.30f,
                color = colors.neonCyan.copy(alpha = 0.08f),
            )

            stars.forEach { star ->
                drawCircle(
                    color = Color.White.copy(alpha = star.alpha),
                    radius = star.radius,
                    center = Offset(
                        x = size.width * star.x,
                        y = size.height * star.y,
                    ),
                )
            }
        }

        content()
    }
}

private fun DrawScope.drawGlowOrb(
    center: Offset,
    radius: Float,
    color: Color,
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color,
                Color.Transparent,
            ),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}

private data class CosmicStar(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
)

private fun cosmicStars(): List<CosmicStar> = listOf(
    CosmicStar(x = 0.08f, y = 0.12f, radius = 1.3f, alpha = 0.66f),
    CosmicStar(x = 0.18f, y = 0.28f, radius = 0.9f, alpha = 0.42f),
    CosmicStar(x = 0.31f, y = 0.09f, radius = 1.1f, alpha = 0.54f),
    CosmicStar(x = 0.42f, y = 0.22f, radius = 0.8f, alpha = 0.36f),
    CosmicStar(x = 0.55f, y = 0.14f, radius = 1.4f, alpha = 0.58f),
    CosmicStar(x = 0.73f, y = 0.08f, radius = 0.9f, alpha = 0.46f),
    CosmicStar(x = 0.88f, y = 0.18f, radius = 1.2f, alpha = 0.62f),
    CosmicStar(x = 0.12f, y = 0.46f, radius = 1.0f, alpha = 0.38f),
    CosmicStar(x = 0.26f, y = 0.58f, radius = 1.5f, alpha = 0.55f),
    CosmicStar(x = 0.39f, y = 0.43f, radius = 0.8f, alpha = 0.34f),
    CosmicStar(x = 0.64f, y = 0.52f, radius = 1.1f, alpha = 0.50f),
    CosmicStar(x = 0.82f, y = 0.61f, radius = 1.4f, alpha = 0.60f),
    CosmicStar(x = 0.94f, y = 0.48f, radius = 0.8f, alpha = 0.40f),
    CosmicStar(x = 0.07f, y = 0.78f, radius = 1.0f, alpha = 0.48f),
    CosmicStar(x = 0.21f, y = 0.86f, radius = 0.7f, alpha = 0.32f),
    CosmicStar(x = 0.47f, y = 0.73f, radius = 1.2f, alpha = 0.52f),
    CosmicStar(x = 0.68f, y = 0.82f, radius = 0.9f, alpha = 0.44f),
    CosmicStar(x = 0.91f, y = 0.88f, radius = 1.3f, alpha = 0.58f),
)
