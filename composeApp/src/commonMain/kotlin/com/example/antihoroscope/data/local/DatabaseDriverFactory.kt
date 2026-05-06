package com.example.antihoroscope.data.local

import app.cash.sqldelight.db.SqlDriver
import androidx.compose.runtime.Composable

@Composable
expect fun rememberDatabaseDriver(): SqlDriver
