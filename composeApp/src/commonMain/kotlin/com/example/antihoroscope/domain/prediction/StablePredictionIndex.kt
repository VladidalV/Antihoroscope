package com.example.antihoroscope.domain.prediction

import kotlin.math.absoluteValue

internal fun String.stablePredictionIndex(size: Int): Int {
    if (size <= 0) {
        return 0
    }

    return (stableHash().toLong().absoluteValue % size).toInt()
}

private fun String.stableHash(): Int {
    var hash = 0
    for (character in this) {
        hash = (hash * HashMultiplier) + character.code
    }
    return hash
}

private const val HashMultiplier = 31
