package com.example.antihoroscope.core.time

class FakeDateProvider(
    private var dateSnapshot: DateSnapshot,
) : DateProvider {
    override fun today(): DateSnapshot = dateSnapshot

    fun setDateSnapshot(dateSnapshot: DateSnapshot) {
        this.dateSnapshot = dateSnapshot
    }
}
