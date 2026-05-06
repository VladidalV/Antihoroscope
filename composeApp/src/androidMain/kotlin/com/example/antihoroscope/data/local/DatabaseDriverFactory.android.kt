package com.example.antihoroscope.data.local

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

@Composable
actual fun rememberDatabaseDriver(): SqlDriver {
    val context = LocalContext.current
    return remember(context) { createAndroidDatabaseDriver(context) }
}

private fun createAndroidDatabaseDriver(context: Context): SqlDriver {
    return AndroidSqliteDriver(
        schema = AntiHoroscopeDatabase.Schema,
        context = context.applicationContext,
        name = DATABASE_NAME,
    )
}

private const val DATABASE_NAME = "antihoroscope.db"
