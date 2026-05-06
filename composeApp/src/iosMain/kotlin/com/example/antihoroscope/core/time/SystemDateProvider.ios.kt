package com.example.antihoroscope.core.time

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

actual fun createSystemDateProvider(): DateProvider = IosSystemDateProvider()

private class IosSystemDateProvider : DateProvider {
    override fun today(): DateSnapshot {
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            unitFlags = NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
            fromDate = NSDate(),
        )

        return createDateSnapshot(
            year = components.year.toInt(),
            monthNumber = components.month.toInt(),
            dayOfMonth = components.day.toInt(),
        )
    }
}
