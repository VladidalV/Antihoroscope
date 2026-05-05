package com.example.antihoroscope.feature.onboarding

data class ZodiacSignUiModel(
    val id: String,
    val name: String,
    val dateRange: String,
    val symbol: String,
    val accentColor: ULong,
    val selectedCaption: String,
) {
    val accessibilityLabel: String
        get() = "Выбрать знак $name, $dateRange"

    companion object {
        val all = listOf(
            ZodiacSignUiModel(
                id = "aries",
                name = "Овен",
                dateRange = "21 марта - 19 апреля",
                symbol = "Ar",
                accentColor = 0xFFFF5C8AUL,
                selectedCaption = "Овен выбран. Смело, но спорно.",
            ),
            ZodiacSignUiModel(
                id = "taurus",
                name = "Телец",
                dateRange = "20 апреля - 20 мая",
                symbol = "Ta",
                accentColor = 0xFF63E6BEUL,
                selectedCaption = "Телец выбран. Космос уже накрыл стол.",
            ),
            ZodiacSignUiModel(
                id = "gemini",
                name = "Близнецы",
                dateRange = "21 мая - 20 июня",
                symbol = "Ge",
                accentColor = 0xFFFFD166UL,
                selectedCaption = "Близнецы выбраны. Обоим приготовиться.",
            ),
            ZodiacSignUiModel(
                id = "cancer",
                name = "Рак",
                dateRange = "21 июня - 22 июля",
                symbol = "Ca",
                accentColor = 0xFF74C0FCUL,
                selectedCaption = "Рак выбран. Эмоции сохранены в облако.",
            ),
            ZodiacSignUiModel(
                id = "leo",
                name = "Лев",
                dateRange = "23 июля - 22 августа",
                symbol = "Le",
                accentColor = 0xFFFF922BUL,
                selectedCaption = "Лев выбран. Аплодисменты включены.",
            ),
            ZodiacSignUiModel(
                id = "virgo",
                name = "Дева",
                dateRange = "23 августа - 22 сентября",
                symbol = "Vi",
                accentColor = 0xFFA9E34BUL,
                selectedCaption = "Дева выбрана. Таблица судьбы отсортирована.",
            ),
            ZodiacSignUiModel(
                id = "libra",
                name = "Весы",
                dateRange = "23 сентября - 22 октября",
                symbol = "Li",
                accentColor = 0xFFE599F7UL,
                selectedCaption = "Весы выбраны. Решение почти принято.",
            ),
            ZodiacSignUiModel(
                id = "scorpio",
                name = "Скорпион",
                dateRange = "23 октября - 21 ноября",
                symbol = "Sc",
                accentColor = 0xFFFF6B6BUL,
                selectedCaption = "Скорпион выбран. Все сделали вид, что спокойны.",
            ),
            ZodiacSignUiModel(
                id = "sagittarius",
                name = "Стрелец",
                dateRange = "22 ноября - 21 декабря",
                symbol = "Sa",
                accentColor = 0xFF9775FAUL,
                selectedCaption = "Стрелец выбран. Куда-то уже пора.",
            ),
            ZodiacSignUiModel(
                id = "capricorn",
                name = "Козерог",
                dateRange = "22 декабря - 19 января",
                symbol = "Co",
                accentColor = 0xFFADB5BDUL,
                selectedCaption = "Козерог выбран. План на хаос утверждён.",
            ),
            ZodiacSignUiModel(
                id = "aquarius",
                name = "Водолей",
                dateRange = "20 января - 18 февраля",
                symbol = "Aq",
                accentColor = 0xFF66D9E8UL,
                selectedCaption = "Водолей выбран. Логика временно отключена.",
            ),
            ZodiacSignUiModel(
                id = "pisces",
                name = "Рыбы",
                dateRange = "19 февраля - 20 марта",
                symbol = "Pi",
                accentColor = 0xFF91A7FFUL,
                selectedCaption = "Рыбы выбраны. Сон записан как аргумент.",
            ),
        )
    }
}
