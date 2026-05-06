package com.example.antihoroscope.domain.prediction

data class GenerationLimit(
    val dateKey: String,
    val maxCount: Int,
    val usedCount: Int,
) {
    val remainingCount: Int
        get() = (maxCount - usedCount).coerceAtLeast(0)

    val isExhausted: Boolean
        get() = remainingCount == 0
}
