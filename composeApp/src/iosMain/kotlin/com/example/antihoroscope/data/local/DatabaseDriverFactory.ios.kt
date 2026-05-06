package com.example.antihoroscope.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

@Composable
actual fun rememberDatabaseDriver(): SqlDriver {
    return remember {
        NativeSqliteDriver(
            schema = AntiHoroscopeDatabase.Schema,
            name = DATABASE_NAME,
        )
    }
}

private const val DATABASE_NAME = "antihoroscope.db"
