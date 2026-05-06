package com.example.antihoroscope.core.time

interface DateProvider {
    fun today(): DateSnapshot
}

data class DateSnapshot(
    val dateKey: String,
    val displayLabel: String,
)

expect fun createSystemDateProvider(): DateProvider

fun createDateSnapshot(
    year: Int,
    monthNumber: Int,
    dayOfMonth: Int,
): DateSnapshot = DateSnapshot(
    dateKey = buildDateKey(
        year = year,
        monthNumber = monthNumber,
        dayOfMonth = dayOfMonth,
    ),
    displayLabel = buildDateDisplayLabel(
        monthNumber = monthNumber,
        dayOfMonth = dayOfMonth,
        year = year,
    ),
)

private fun buildDateKey(
    year: Int,
    monthNumber: Int,
    dayOfMonth: Int,
): String = buildString {
    append(year)
    append("-")
    append(monthNumber.toPaddedDatePart())
    append("-")
    append(dayOfMonth.toPaddedDatePart())
}

private fun buildDateDisplayLabel(
    monthNumber: Int,
    dayOfMonth: Int,
    year: Int,
): String {
    val monthName = RussianMonthNames.getOrElse(monthNumber - 1) { "" }

    return if (monthName.isBlank()) {
        "$dayOfMonth.$monthNumber.$year"
    } else {
        "$dayOfMonth $monthName $year"
    }
}

private fun Int.toPaddedDatePart(): String =
    if (this < 10) {
        "0$this"
    } else {
        toString()
    }

private val RussianMonthNames = listOf(
    "января",
    "февраля",
    "марта",
    "апреля",
    "мая",
    "июня",
    "июля",
    "августа",
    "сентября",
    "октября",
    "ноября",
    "декабря",
)
