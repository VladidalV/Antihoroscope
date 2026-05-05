package com.example.antihoroscope.feature.onboarding

data class ZodiacSignUiModel(
    val id: String,
    val name: String,
    val dateRange: String,
    val symbol: String,
    val accentColor: Long,
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
                symbol = "♈",
                accentColor = 0xFFFF5C8A,
                selectedCaption = "Овен выбран. Смело, но спорно.",
            ),
            ZodiacSignUiModel(
                id = "taurus",
                name = "Телец",
                dateRange = "20 апреля - 20 мая",
                symbol = "♉",
                accentColor = 0xFF63E6BE,
                selectedCaption = "Телец выбран. Космос уже накрыл стол.",
            ),
            ZodiacSignUiModel(
                id = "gemini",
                name = "Близнецы",
                dateRange = "21 мая - 20 июня",
                symbol = "♊",
                accentColor = 0xFFFFD166,
                selectedCaption = "Близнецы выбраны. Обоим приготовиться.",
            ),
            ZodiacSignUiModel(
                id = "cancer",
                name = "Рак",
                dateRange = "21 июня - 22 июля",
                symbol = "♋",
                accentColor = 0xFF74C0FC,
                selectedCaption = "Рак выбран. Эмоции сохранены в облако.",
            ),
            ZodiacSignUiModel(
                id = "leo",
                name = "Лев",
                dateRange = "23 июля - 22 августа",
                symbol = "♌",
                accentColor = 0xFFFF922B,
                selectedCaption = "Лев выбран. Аплодисменты включены.",
            ),
            ZodiacSignUiModel(
                id = "virgo",
                name = "Дева",
                dateRange = "23 августа - 22 сентября",
                symbol = "♍",
                accentColor = 0xFFA9E34B,
                selectedCaption = "Дева выбрана. Таблица судьбы отсортирована.",
            ),
            ZodiacSignUiModel(
                id = "libra",
                name = "Весы",
                dateRange = "23 сентября - 22 октября",
                symbol = "♎",
                accentColor = 0xFFE599F7,
                selectedCaption = "Весы выбраны. Решение почти принято.",
            ),
            ZodiacSignUiModel(
                id = "scorpio",
                name = "Скорпион",
                dateRange = "23 октября - 21 ноября",
                symbol = "♏",
                accentColor = 0xFFFF6B6B,
                selectedCaption = "Скорпион выбран. Все сделали вид, что спокойны.",
            ),
            ZodiacSignUiModel(
                id = "sagittarius",
                name = "Стрелец",
                dateRange = "22 ноября - 21 декабря",
                symbol = "♐",
                accentColor = 0xFF9775FA,
                selectedCaption = "Стрелец выбран. Куда-то уже пора.",
            ),
            ZodiacSignUiModel(
                id = "capricorn",
                name = "Козерог",
                dateRange = "22 декабря - 19 января",
                symbol = "♑",
                accentColor = 0xFFADB5BD,
                selectedCaption = "Козерог выбран. План на хаос утверждён.",
            ),
            ZodiacSignUiModel(
                id = "aquarius",
                name = "Водолей",
                dateRange = "20 января - 18 февраля",
                symbol = "♒",
                accentColor = 0xFF66D9E8,
                selectedCaption = "Водолей выбран. Логика временно отключена.",
            ),
            ZodiacSignUiModel(
                id = "pisces",
                name = "Рыбы",
                dateRange = "19 февраля - 20 марта",
                symbol = "♓",
                accentColor = 0xFF91A7FF,
                selectedCaption = "Рыбы выбраны. Сон записан как аргумент.",
            ),
        )
    }
}
