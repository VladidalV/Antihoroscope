package com.example.antihoroscope.core.analytics

interface AnalyticsTracker {
    fun track(
        eventName: String,
        params: Map<String, String> = emptyMap(),
    )
}
