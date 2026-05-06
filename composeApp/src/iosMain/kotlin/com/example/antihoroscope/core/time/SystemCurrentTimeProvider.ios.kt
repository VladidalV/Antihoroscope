package com.example.antihoroscope.core.time

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.time

private object IosCurrentTimeProvider : CurrentTimeProvider {
    @OptIn(ExperimentalForeignApi::class)
    override fun currentTimeMillis(): Long {
        return time(null) * MILLIS_PER_SECOND
    }

    private const val MILLIS_PER_SECOND = 1_000L
}

actual fun createSystemCurrentTimeProvider(): CurrentTimeProvider = IosCurrentTimeProvider
