package com.example.antihoroscope.feature.prediction.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.ui.components.CosmicBackground
import com.example.antihoroscope.ui.components.CosmicButton
import com.example.antihoroscope.ui.components.CosmicTextButton
import com.example.antihoroscope.ui.components.PredictionDetailCard
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun PredictionDetailScreen(
    state: PredictionDetailState,
    onIntent: (PredictionDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    CosmicBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            PredictionDetailContent(
                state = state,
                onIntent = onIntent,
            )

            state.feedbackMessage?.let { message ->
                PredictionDetailTransientMessage(
                    message = message,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun PredictionDetailContent(
    state: PredictionDetailState,
    onIntent: (PredictionDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 18.dp),
    ) {
        PredictionDetailTopBar(
            onBackClick = { onIntent(PredictionDetailIntent.BackClicked) },
        )

        Spacer(modifier = Modifier.height(18.dp))

        PredictionDetailHeader(state = state)

        Spacer(modifier = Modifier.height(18.dp))

        PredictionDetailCard(
            dailyPrediction = state.dailyPrediction,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(18.dp))

        PredictionDetailActions(
            isFavorite = state.isFavorite,
            onShareClick = { onIntent(PredictionDetailIntent.ShareClicked) },
            onFavoriteClick = { onIntent(PredictionDetailIntent.FavoriteClicked) },
            onNextClick = { onIntent(PredictionDetailIntent.NextClicked) },
        )

        Spacer(modifier = Modifier.height(92.dp))
    }
}

@Composable
private fun PredictionDetailTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CosmicTextButton(
            text = "Назад",
            onClick = onBackClick,
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun PredictionDetailHeader(
    state: PredictionDetailState,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = state.dailyPrediction.zodiacSignName,
            color = colors.starWhite,
            style = MaterialTheme.typography.headlineLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
        Text(
            text = state.dailyPrediction.dateLabel,
            color = colors.neonCyan,
            style = MaterialTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
        Text(
            text = "Детальное чтение прогноза без попыток сделать вид, что это наука.",
            color = colors.moonMuted,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Clip,
        )
    }
}

@Composable
private fun PredictionDetailActions(
    isFavorite: Boolean,
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CosmicButton(
            text = "Поделиться",
            onClick = onShareClick,
            modifier = Modifier.fillMaxWidth(),
        )
        CosmicButton(
            text = if (isFavorite) "В избранном" else "В избранное",
            onClick = onFavoriteClick,
            modifier = Modifier.fillMaxWidth(),
        )
        CosmicTextButton(
            text = "Следующее",
            onClick = onNextClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun PredictionDetailTransientMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .safeContentPadding()
            .padding(horizontal = 22.dp, vertical = 18.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.cosmicSurfaceHigh.copy(alpha = 0.94f),
                        colors.cosmicSurface.copy(alpha = 0.90f),
                    ),
                ),
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colors.neonCyan.copy(alpha = 0.52f),
                            colors.neonPurple.copy(alpha = 0.42f),
                        ),
                    ),
                ),
                shape = shape,
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = colors.starWhite,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
        )
    }
}
