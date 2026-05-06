package com.example.antihoroscope.domain.prediction

enum class PredictionCategory {
    Love,
    Money,
    Career,
    Health,
    Chaos,
}

val PredictionCategory.titleRu: String
    get() = when (this) {
        PredictionCategory.Love -> "Любовь"
        PredictionCategory.Money -> "Деньги"
        PredictionCategory.Career -> "Карьера"
        PredictionCategory.Health -> "Здоровье"
        PredictionCategory.Chaos -> "Хаос"
    }

val PredictionCategory.analyticsName: String
    get() = when (this) {
        PredictionCategory.Love -> "love"
        PredictionCategory.Money -> "money"
        PredictionCategory.Career -> "career"
        PredictionCategory.Health -> "health"
        PredictionCategory.Chaos -> "chaos"
    }

val PredictionCategory.accentColorHex: String
    get() = when (this) {
        PredictionCategory.Love -> "#FF5C8A"
        PredictionCategory.Money -> "#63E6BE"
        PredictionCategory.Career -> "#9775FA"
        PredictionCategory.Health -> "#74C0FC"
        PredictionCategory.Chaos -> "#FFD166"
    }

val PredictionCategory.shortLabel: String
    get() = when (this) {
        PredictionCategory.Love -> "Л"
        PredictionCategory.Money -> "Д"
        PredictionCategory.Career -> "К"
        PredictionCategory.Health -> "З"
        PredictionCategory.Chaos -> "Х"
    }
