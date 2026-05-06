package com.example.antihoroscope.domain.prediction

import com.example.antihoroscope.data.prediction.history.DefaultPredictionHistoryRepository
import com.example.antihoroscope.data.prediction.history.PredictionHistoryLocalDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PredictionHistoryUseCaseTest {
    @Test
    fun recordPredictionViewCreatesHistoryItemWithoutDuplicatingStableKey() {
        val repository = DefaultPredictionHistoryRepository(FakePredictionHistoryLocalDataSource())
        var now = 1_000L
        val recordPredictionView = RecordPredictionViewUseCase(
            repository = repository,
            currentTimeMillis = { now },
        )

        recordPredictionView(dailyPrediction(id = "same"), source = "home")
        now = 2_000L
        recordPredictionView(dailyPrediction(id = "same"), source = "home")

        val history = GetPredictionHistoryUseCase(repository)()
        assertEquals(1, history.size)
        assertEquals("same", history.single().predictionId)
        assertEquals(1_000L, history.single().viewedAtEpochMillis)
    }

    @Test
    fun historyIsSortedFromNewestToOldestAndCanBeLimited() {
        val repository = DefaultPredictionHistoryRepository(FakePredictionHistoryLocalDataSource())
        var now = 1_000L
        val recordPredictionView = RecordPredictionViewUseCase(
            repository = repository,
            currentTimeMillis = { now },
        )

        recordPredictionView(dailyPrediction(id = "older"), source = "home")
        now = 2_000L
        recordPredictionView(dailyPrediction(id = "newer"), source = "home")

        val history = GetPredictionHistoryUseCase(repository)(limit = 1)
        assertEquals(listOf("newer"), history.map { item -> item.predictionId })
    }

    @Test
    fun favoriteToggleAddsRemovesAndRestoresSnapshotState() {
        val localDataSource = FakePredictionHistoryLocalDataSource()
        val repository = DefaultPredictionHistoryRepository(localDataSource)
        var now = 1_000L
        val toggleFavorite = ToggleFavoritePredictionUseCase(
            repository = repository,
            currentTimeMillis = { now },
        )
        val isFavorite = IsFavoritePredictionUseCase(repository)
        val getFavorites = GetFavoritePredictionsUseCase(repository)

        val added = toggleFavorite(dailyPrediction(id = "favorite"))

        assertTrue(added)
        assertTrue(isFavorite("favorite"))
        assertEquals(1, getFavorites().size)
        assertEquals("Тестовое предсказание favorite", getFavorites().single().predictionText)
        assertEquals(PredictionCategory.Chaos, getFavorites().single().category)
        assertEquals("Овен", getFavorites().single().zodiacSignName)

        now = 2_000L
        val removed = toggleFavorite(dailyPrediction(id = "favorite"))

        assertFalse(removed)
        assertFalse(isFavorite("favorite"))
        assertEquals(emptyList(), getFavorites())
    }

    @Test
    fun favoritesAreSortedFromNewestToOldest() {
        val repository = DefaultPredictionHistoryRepository(FakePredictionHistoryLocalDataSource())
        var now = 1_000L
        val toggleFavorite = ToggleFavoritePredictionUseCase(
            repository = repository,
            currentTimeMillis = { now },
        )

        toggleFavorite(dailyPrediction(id = "older"))
        now = 2_000L
        toggleFavorite(dailyPrediction(id = "newer"))

        assertEquals(
            listOf("newer", "older"),
            GetFavoritePredictionsUseCase(repository)().map { item -> item.predictionId },
        )
    }
}

internal class FakePredictionHistoryLocalDataSource : PredictionHistoryLocalDataSource {
    private val history = mutableMapOf<String, PredictionHistoryItem>()
    private val favorites = mutableMapOf<String, FavoritePredictionItem>()

    override fun recordHistory(item: PredictionHistoryItem) {
        if (!history.containsKey(item.historyId)) {
            history[item.historyId] = item
        }
    }

    override fun getHistory(limit: Long): List<PredictionHistoryItem> {
        return history.values
            .sortedByDescending { item -> item.viewedAtEpochMillis }
            .take(limit.toInt())
    }

    override fun addFavorite(item: FavoritePredictionItem) {
        favorites[item.predictionId] = item
    }

    override fun removeFavorite(predictionId: String) {
        favorites.remove(predictionId)
    }

    override fun isFavorite(predictionId: String): Boolean {
        return favorites.containsKey(predictionId)
    }

    override fun getFavorites(): List<FavoritePredictionItem> {
        return favorites.values.sortedByDescending { item -> item.favoritedAtEpochMillis }
    }
}

private fun dailyPrediction(
    id: String,
    category: PredictionCategory = PredictionCategory.Chaos,
) = DailyPrediction(
    prediction = prediction(
        id = id,
        category = category,
        zodiacSignId = "aries",
    ),
    dateKey = "2026-05-06",
    dateLabel = "6 мая 2026",
    zodiacSignId = "aries",
    zodiacSignName = "Овен",
)
