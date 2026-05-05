package com.example.antihoroscope.core.analytics

object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun track(
        eventName: String,
        params: Map<String, String>,
    ) = Unit
}
