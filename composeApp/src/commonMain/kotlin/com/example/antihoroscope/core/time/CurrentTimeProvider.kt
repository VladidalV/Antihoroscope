package com.example.antihoroscope.core.time

interface CurrentTimeProvider {
    fun currentTimeMillis(): Long
}

expect fun createSystemCurrentTimeProvider(): CurrentTimeProvider
