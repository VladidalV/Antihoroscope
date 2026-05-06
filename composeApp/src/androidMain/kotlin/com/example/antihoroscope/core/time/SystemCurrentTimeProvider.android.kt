package com.example.antihoroscope.core.time

private object AndroidCurrentTimeProvider : CurrentTimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

actual fun createSystemCurrentTimeProvider(): CurrentTimeProvider = AndroidCurrentTimeProvider
