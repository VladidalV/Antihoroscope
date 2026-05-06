package com.example.antihoroscope.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.antihoroscope.feature.home.components.GenerationLimitIndicator
import com.example.antihoroscope.feature.home.components.PredictionCategoryRow
import com.example.antihoroscope.ui.components.CosmicBackground
import com.example.antihoroscope.ui.components.CosmicButton
import com.example.antihoroscope.ui.components.CosmicTextButton
import com.example.antihoroscope.ui.components.GlowingPredictionCard
import com.example.antihoroscope.ui.theme.AntiHoroscopeTheme

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    CosmicBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                HomeState.Loading -> HomeLoadingState()
                is HomeState.Content -> HomeContentState(
                    state = state,
                    onIntent = onIntent,
                )
                is HomeState.MissingZodiac -> HomeMessageState(
                    title = state.title,
                    message = state.message,
                )
                is HomeState.EmptyCatalog -> HomeMessageState(
                    title = state.title,
                    message = state.message,
                )
                is HomeState.Error -> HomeMessageState(
                    title = state.title,
                    message = state.message,
                )
            }

            if (message != null) {
                HomeTransientMessage(
                    message = message,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun HomeContentState(
    state: HomeState.Content,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 22.dp),
    ) {
        HomeHeader(state = state)

        Spacer(modifier = Modifier.height(22.dp))

        GlowingPredictionCard(
            dailyPrediction = state.dailyPrediction,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(18.dp))

        PredictionCategoryRow(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = { category ->
                onIntent(HomeIntent.CategorySelected(category))
            },
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GenerationLimitIndicator(generationLimit = state.generationLimit)
        }

        Spacer(modifier = Modifier.height(18.dp))

        CosmicButton(
            text = if (state.isRefreshing) "Космос думает..." else "Другое предсказание",
            onClick = { onIntent(HomeIntent.RefreshClicked) },
            enabled = state.canRefresh,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CosmicTextButton(
            text = "Поделиться",
            onClick = { onIntent(HomeIntent.ShareClicked) },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(88.dp))
    }
}

@Composable
private fun HomeHeader(
    state: HomeState.Content,
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
        )
        Text(
            text = state.dailyPrediction.dateLabel,
            color = colors.neonCyan,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = sarcasticSubtitle(state.dailyPrediction.dateKey),
            color = colors.moonMuted,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun HomeLoadingState(
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(color = colors.neonCyan)
            Text(
                text = "Звёзды шуршат бумагами",
                color = colors.starWhite,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun HomeMessageState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    val colors = AntiHoroscopeTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            color = colors.starWhite,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            color = colors.moonMuted,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HomeTransientMessage(
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
            maxLines = 2,
        )
    }
}

private fun sarcasticSubtitle(dateKey: String): String {
    val subtitles = listOf(
        "Звёзды посмотрели. Им есть что сказать.",
        "Космос сегодня настроен пассивно-агрессивно.",
        "Небесная канцелярия прислала записку.",
    )
    val index = dateKey.fold(0) { accumulator, character ->
        accumulator + character.code
    } % subtitles.size

    return subtitles[index]
}
