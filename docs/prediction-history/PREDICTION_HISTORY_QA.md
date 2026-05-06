# Prediction History & Persisted Favorites MVP: QA

Дата проверки: 2026-05-06.

## Проверенные сценарии

- Home view -> history record: покрыто unit-тестом `screenShownRecordsHomeHistoryWithoutDuplicatingStableKey`.
- Повторный Home record с тем же stable key не создаёт дубль: покрыто тем же тестом и `recordPredictionViewCreatesHistoryItemWithoutDuplicatingStableKey`.
- Detail view -> history record с `source=detail`: покрыто тестом `initRecordsDetailHistoryWithIdempotentSource`.
- Add favorite на Detail: покрыто тестом `favoriteClickTogglesFavoriteStateAndEmitsFavoriteEvent`.
- Remove favorite на Detail: покрыто тестом `secondFavoriteClickRemovesFavoriteState`.
- Восстановление favorite state при повторном создании Detail VM: покрыто тестом `initialFavoriteStateIsReadFromPersistence`.
- Favorites для будущего Profile сортируются от новых к старым: покрыто тестом `favoritesAreSortedFromNewestToOldest`.
- History для будущего Profile сортируется от новых к старым и поддерживает limit: покрыто тестом `historyIsSortedFromNewestToOldestAndCanBeLimited`.
- Существующие Home/Detail feedback и analytics-сценарии: обновлённые тесты Home/Detail проходят в `allTests`.

## Code-level QA

- SQLDelight schema использует `INSERT OR IGNORE` для history. Повторная запись stable key является no-op и сохраняет первый `viewedAtEpochMillis`.
- Favorite хранится по `predictionId` через primary key и toggle удаляет запись при повторном клике.
- `PredictionDetailViewModel` читает initial favorite из `IsFavoritePredictionUseCase`.
- `PredictionDetailViewModel` вызывает `ToggleFavoritePredictionUseCase` при `FavoriteClicked` и сохраняет analytics event `prediction_favorite_clicked`.
- Dependencies собираются вручную в `App.kt`; Koin/Decompose/bottom navigation не добавлялись.

## Verification

Запущено:

```bash
./gradlew :composeApp:allTests
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
./gradlew :composeApp:verifyCommonMainAntiHoroscopeDatabaseMigration
```

Результат: pass для всех команд.

## Остаточные риски

- Визуальный device/screenshot QA не проводился: эпик не меняет layout и не добавляет новый экран history/favorites.
- iOS persistence compile проверяется, но ручной запуск приложения на устройстве/симуляторе не выполнялся в рамках этой QA-сессии.
