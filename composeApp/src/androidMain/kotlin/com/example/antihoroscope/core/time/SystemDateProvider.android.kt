package com.example.antihoroscope.core.time

import java.util.Calendar

actual fun createSystemDateProvider(): DateProvider = AndroidSystemDateProvider()

private class AndroidSystemDateProvider : DateProvider {
    override fun today(): DateSnapshot {
        val calendar = Calendar.getInstance()

        return createDateSnapshot(
            year = calendar.get(Calendar.YEAR),
            monthNumber = calendar.get(Calendar.MONTH) + 1,
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
        )
    }
}
