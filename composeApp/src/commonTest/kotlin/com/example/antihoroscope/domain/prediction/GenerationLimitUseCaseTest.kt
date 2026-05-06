package com.example.antihoroscope.domain.prediction

import com.example.antihoroscope.core.time.FakeDateProvider
import com.example.antihoroscope.core.time.createDateSnapshot
import com.example.antihoroscope.data.settings.HomeGenerationLimitSnapshot
import com.example.antihoroscope.data.settings.HomeSettingsStorage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GenerationLimitUseCaseTest {
    @Test
    fun freshLimitHasThreeRefreshesAvailable() {
        val storage = FakeHomeSettingsStorage()
        val getLimitUseCase = GetGenerationLimitUseCase(
            homeSettingsStorage = storage,
            dateProvider = FakeDateProvider(MaySixDateSnapshot),
        )

        val limit = getLimitUseCase()

        assertEquals("2026-05-06", limit.dateKey)
        assertEquals(3, limit.maxCount)
        assertEquals(0, limit.usedCount)
        assertEquals(3, limit.remainingCount)
        assertFalse(limit.isExhausted)
    }

    @Test
    fun threeConsumesAreAvailableAndStored() {
        val storage = FakeHomeSettingsStorage()
        val consumeLimitUseCase = ConsumeGenerationLimitUseCase(
            homeSettingsStorage = storage,
            dateProvider = FakeDateProvider(MaySixDateSnapshot),
        )

        val firstResult = assertIs<ConsumeGenerationLimitResult.Success>(consumeLimitUseCase())
        val secondResult = assertIs<ConsumeGenerationLimitResult.Success>(consumeLimitUseCase())
        val thirdResult = assertIs<ConsumeGenerationLimitResult.Success>(consumeLimitUseCase())

        assertEquals(1, firstResult.generationLimit.usedCount)
        assertEquals(2, secondResult.generationLimit.usedCount)
        assertEquals(3, thirdResult.generationLimit.usedCount)
        assertEquals(
            HomeGenerationLimitSnapshot(
                dateKey = "2026-05-06",
                usedCount = 3,
            ),
            storage.snapshot,
        )
    }

    @Test
    fun fourthConsumeIsBlockedAndDoesNotStoreMoreUsage() {
        val storage = FakeHomeSettingsStorage(
            snapshot = HomeGenerationLimitSnapshot(
                dateKey = "2026-05-06",
                usedCount = 3,
            ),
        )
        val consumeLimitUseCase = ConsumeGenerationLimitUseCase(
            homeSettingsStorage = storage,
            dateProvider = FakeDateProvider(MaySixDateSnapshot),
        )

        val result = assertIs<ConsumeGenerationLimitResult.Blocked>(consumeLimitUseCase())

        assertTrue(result.generationLimit.isExhausted)
        assertEquals(0, result.generationLimit.remainingCount)
        assertEquals(0, storage.saveCallCount)
        assertEquals(3, storage.snapshot.usedCount)
    }

    @Test
    fun newDateResetsStoredUsage() {
        val dateProvider = FakeDateProvider(MaySixDateSnapshot)
        val storage = FakeHomeSettingsStorage(
            snapshot = HomeGenerationLimitSnapshot(
                dateKey = "2026-05-06",
                usedCount = 3,
            ),
        )
        val getLimitUseCase = GetGenerationLimitUseCase(
            homeSettingsStorage = storage,
            dateProvider = dateProvider,
        )

        dateProvider.setDateSnapshot(MaySevenDateSnapshot)
        val limit = getLimitUseCase()

        assertEquals("2026-05-07", limit.dateKey)
        assertEquals(0, limit.usedCount)
        assertEquals(3, limit.remainingCount)
        assertFalse(limit.isExhausted)
    }
}

private class FakeHomeSettingsStorage(
    var snapshot: HomeGenerationLimitSnapshot = HomeGenerationLimitSnapshot(),
) : HomeSettingsStorage {
    var saveCallCount: Int = 0
        private set

    override fun readGenerationLimit(): HomeGenerationLimitSnapshot = snapshot

    override fun saveGenerationLimit(
        dateKey: String,
        usedCount: Int,
    ) {
        saveCallCount += 1
        snapshot = HomeGenerationLimitSnapshot(
            dateKey = dateKey,
            usedCount = usedCount,
        )
    }

    override fun resetGenerationLimit() {
        snapshot = HomeGenerationLimitSnapshot()
    }
}

private val MaySixDateSnapshot = createDateSnapshot(
    year = 2026,
    monthNumber = 5,
    dayOfMonth = 6,
)

private val MaySevenDateSnapshot = createDateSnapshot(
    year = 2026,
    monthNumber = 5,
    dayOfMonth = 7,
)
