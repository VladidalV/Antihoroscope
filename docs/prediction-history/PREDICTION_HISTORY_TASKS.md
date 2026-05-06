# Prediction History & Persisted Favorites MVP: task breakdown

Документ декомпозирует Epic 4 — `Prediction History & Persisted Favorites MVP`.

Базовое ТЗ: `docs/prediction-history/PREDICTION_HISTORY_TZ.md`.

## 1. Целевой результат

После выполнения эпика в приложении появляется локальная persistence-основа для истории и избранного:

- Home/Detail сохраняют просмотренные предсказания в history.
- History не дублируется на обычной recomposition.
- Detail использует persisted favorite state.
- Favorite сохраняется между закрытием и повторным открытием detail.
- Будущий Profile сможет получить history и favorites через готовые use cases.
- Реализация использует SQLDelight и остаётся в текущей архитектуре `composeApp`, без Koin/Decompose/bottom navigation.

## 2. Итерации

### Итерация 1: Docs и SQLDelight foundation

- `HISTORY-001`: создать ТЗ и task breakdown.
- `HISTORY-002`: добавить SQLDelight dependency/configuration для KMP.
- `HISTORY-003`: создать SQLDelight schema для prediction history/favorites.

### Итерация 2: Domain, data source, use cases

- `HISTORY-004`: создать domain-модели для history/favorite items.
- `HISTORY-005`: реализовать local data source для history/favorites.
- `HISTORY-006`: реализовать repository/use cases для history/favorites.
- `HISTORY-007`: добавить unit-тесты storage/repository/use cases.

### Итерация 3: Integration

- `HISTORY-008`: интегрировать запись history в Home/Detail без дублей на recomposition.
- `HISTORY-009`: подключить persisted favorite state к `PredictionDetailViewModel`.
- `HISTORY-010`: обновить detail-тесты под persisted favorite.

### Итерация 4: QA, verification, статус

- `HISTORY-011`: провести QA сценариев history/favorite.
- `HISTORY-012`: запустить verification-команды.
- `HISTORY-013`: обновить статус документации.

## 2.1 Фактический статус реализации

| Task | Статус | Комментарий |
| --- | --- | --- |
| `HISTORY-001` | Done | ТЗ и task breakdown уже были созданы. |
| `HISTORY-002` | Done | Подключён SQLDelight `app.cash.sqldelight` 2.3.2 для KMP, Android и iOS drivers. |
| `HISTORY-003` | Done | Создана schema `PredictionHistory.sq` для history/favorites. History duplicate key делает no-op через `INSERT OR IGNORE`. |
| `HISTORY-004` | Done | Добавлены `PredictionHistoryItem` и `FavoritePredictionItem` с factories из `DailyPrediction`. |
| `HISTORY-005` | Done | Добавлены local data source interface, SQLDelight implementation и platform driver creation. |
| `HISTORY-006` | Done | Добавлены repository contract, default repository и use cases для history/favorites. |
| `HISTORY-007` | Done | Добавлены common unit-тесты repository/use cases. |
| `HISTORY-008` | Done | Home пишет `source=home`, Detail пишет `source=detail`; дубли отсекаются stable key. |
| `HISTORY-009` | Done | Detail читает persisted favorite state и toggles через use case. |
| `HISTORY-010` | Done | Detail-тесты обновлены под persisted favorite. |
| `HISTORY-011` | Done | Создан `PREDICTION_HISTORY_QA.md`. |
| `HISTORY-012` | Done | `allTests`, `assembleDebug`, `compileKotlinIosSimulatorArm64` и SQLDelight migration verify проходят. |
| `HISTORY-013` | Done | Документация обновлена по факту реализации. |

## 3. Задачи

### HISTORY-001: Создать ТЗ и task breakdown для Prediction History & Persisted Favorites

**Тип:** Documentation  
**Приоритет:** High  
**Статус:** Done  
**Зависимости:** нет  
**Файлы:**

- `docs/prediction-history/PREDICTION_HISTORY_TZ.md`
- `docs/prediction-history/PREDICTION_HISTORY_TASKS.md`

**Что сделать:**

- Описать продуктовую цель history/favorites MVP.
- Зафиксировать MVP scope и out of scope.
- Описать пользовательские сценарии.
- Описать data model для history/favorites.
- Описать архитектурные решения и ограничения.
- Зафиксировать analytics expectations.
- Декомпозировать эпик на задачи `HISTORY-001` - `HISTORY-013`.

**Definition of Done:**

- Оба документа созданы.
- Scope явно отделяет persistence MVP от Profile/Settings/Share эпиков.
- В документах есть Test Plan и Acceptance Criteria.

---

### HISTORY-002: Добавить SQLDelight dependency/configuration для KMP

**Тип:** Infrastructure / Database  
**Приоритет:** High  
**Зависимости:** `HISTORY-001`  
**Файлы:**

- `gradle/libs.versions.toml`
- `build.gradle.kts`
- `composeApp/build.gradle.kts`

**Что сделать:**

- Добавить SQLDelight plugin/dependencies.
- Настроить database package/name для `composeApp`.
- Добавить Android driver dependency.
- Добавить native/iOS driver dependency.
- Убедиться, что generated SQLDelight code доступен из `commonMain`.
- Не добавлять Koin/Decompose и не менять текущую сборку зависимостей приложения.

**Definition of Done:**

- Gradle sync/configuration проходит.
- `commonMain` может использовать generated database API.
- Android и iOS targets компилируются.

---

### HISTORY-003: Создать SQLDelight schema для prediction history/favorites

**Тип:** Database / Schema  
**Приоритет:** High  
**Зависимости:** `HISTORY-002`  
**Файлы:**

- `composeApp/src/commonMain/sqldelight/.../PredictionHistory.sq`

**Что сделать:**

- Создать таблицу history.
- Создать таблицу favorites.
- Добавить queries для insert/upsert history.
- Добавить queries для чтения history от новых к старым.
- Добавить queries для toggle/add/remove favorite.
- Добавить query проверки favorite по `predictionId`.
- Добавить query чтения favorites от новых к старым.

**Рекомендуемая модель history:**

- stable key: `predictionId + dateKey + zodiacSignId + source`;
- `predictionId`;
- `predictionText`;
- `category`;
- `absurdityLevel`;
- `zodiacSignId`;
- `zodiacSignName`;
- `dateKey`;
- `dateLabel`;
- `source`;
- `viewedAtEpochMillis`.

**Рекомендуемая модель favorites:**

- primary key: `predictionId`;
- snapshot полей prediction/daily context;
- `favoritedAtEpochMillis`.

**Definition of Done:**

- Schema компилируется.
- Insert/upsert не создаёт дубликаты history.
- Favorites можно добавить, удалить, проверить и прочитать списком.

---

### HISTORY-004: Создать domain-модели для history/favorite items

**Тип:** Domain  
**Приоритет:** High  
**Зависимости:** `HISTORY-003`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../domain/prediction/PredictionHistoryItem.kt`
- `composeApp/src/commonMain/kotlin/.../domain/prediction/FavoritePredictionItem.kt`

**Что сделать:**

- Создать domain-модель history item.
- Создать domain-модель favorite item.
- Использовать существующие `PredictionCategory`, `DailyPrediction`, `Prediction`, где это уместно.
- Не добавлять account/user/backend модели.
- Добавить helper/factory из `DailyPrediction`, если это снижает дублирование.

**Definition of Done:**

- Модели commonMain-compatible.
- Модели содержат snapshot, достаточный для будущего Profile.
- UI не зависит от SQLDelight row types.

---

### HISTORY-005: Реализовать local data source для history/favorites

**Тип:** Data  
**Приоритет:** High  
**Зависимости:** `HISTORY-003`, `HISTORY-004`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../data/prediction/history/PredictionHistoryLocalDataSource.kt`
- `composeApp/src/commonMain/kotlin/.../data/prediction/history/SqlDelightPredictionHistoryLocalDataSource.kt`
- platform driver factory files при необходимости

**Что сделать:**

- Описать interface local data source.
- Реализовать SQLDelight-backed data source.
- Добавить mapper SQLDelight rows -> domain models.
- Добавить mapper domain snapshot -> SQLDelight insert params.
- Обработать idempotent запись history.
- Обработать add/remove/isFavorite/listFavorites.

**Definition of Done:**

- Data source не зависит от Compose UI.
- Data source работает из commonMain через platform driver.
- Ошибки маппинга category не приводят к crash; неизвестные значения должны иметь безопасный fallback или контролируемую ошибку.

---

### HISTORY-006: Реализовать repository/use cases

**Тип:** Domain / State support  
**Приоритет:** High  
**Зависимости:** `HISTORY-005`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../domain/prediction/PredictionHistoryRepository.kt`
- `composeApp/src/commonMain/kotlin/.../domain/prediction/RecordPredictionViewUseCase.kt`
- `composeApp/src/commonMain/kotlin/.../domain/prediction/GetPredictionHistoryUseCase.kt`
- `composeApp/src/commonMain/kotlin/.../domain/prediction/ToggleFavoritePredictionUseCase.kt`
- `composeApp/src/commonMain/kotlin/.../domain/prediction/IsFavoritePredictionUseCase.kt`
- `composeApp/src/commonMain/kotlin/.../domain/prediction/GetFavoritePredictionsUseCase.kt`

**Что сделать:**

- Создать repository contract для history/favorites.
- Реализовать use case записи просмотра.
- Реализовать use case чтения history.
- Реализовать use case toggle favorite.
- Реализовать use case проверки favorite state.
- Реализовать use case чтения favorites.
- Сохранить use cases синхронными или suspend в одном стиле, выбранном для SQLDelight access.

**Definition of Done:**

- Use cases не зависят от UI.
- Use cases тестируются без Android/iOS runtime, где это возможно.
- Future Profile может использовать history/favorites без доступа к SQLDelight напрямую.

---

### HISTORY-007: Добавить unit-тесты storage/repository/use cases

**Тип:** Tests  
**Приоритет:** High  
**Зависимости:** `HISTORY-006`  
**Файлы:**

- `composeApp/src/commonTest/kotlin/.../domain/prediction/PredictionHistoryRepositoryTest.kt`
- `composeApp/src/commonTest/kotlin/.../domain/prediction/PredictionHistoryUseCaseTest.kt`

**Что сделать:**

- Проверить запись history.
- Проверить отсутствие дубля при повторной записи того же key.
- Проверить сортировку history от новых к старым.
- Проверить add favorite.
- Проверить remove favorite.
- Проверить `isFavorite`.
- Проверить сортировку favorites от новых к старым.
- Проверить snapshot полей для будущего Profile.

**Definition of Done:**

- Тесты запускаются через `./gradlew :composeApp:allTests`.
- Тесты не требуют реального backend/account.
- Основная persistence-логика покрыта.

---

### HISTORY-008: Интегрировать запись history в Home/Detail без дублей на recomposition

**Тип:** Integration  
**Приоритет:** High  
**Зависимости:** `HISTORY-006`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../App.kt`
- `composeApp/src/commonMain/kotlin/.../feature/home/HomeViewModel.kt`
- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailViewModel.kt`

**Что сделать:**

- Передать history dependencies через текущую ручную сборку в `App.kt`.
- Записывать Home daily/manual prediction view один раз на фактическое новое prediction state.
- Избежать записи на каждую recomposition.
- Решить, пишет ли detail отдельный source `detail`; если пишет, сделать idempotent key.
- Не менять визуальное поведение Home/Detail.

**Definition of Done:**

- Home просмотр сохраняется в history.
- Recomposition не создаёт дубли.
- Detail open не ломает history.
- Home/Detail существующие тесты проходят.

---

### HISTORY-009: Подключить persisted favorite state к PredictionDetailViewModel

**Тип:** State management  
**Приоритет:** High  
**Зависимости:** `HISTORY-006`, `HISTORY-008`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailViewModel.kt`
- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailState.kt`
- `composeApp/src/commonMain/kotlin/.../App.kt`

**Что сделать:**

- Загружать initial `isFavorite` через `IsFavoritePredictionUseCase`.
- По `FavoriteClicked` вызывать `ToggleFavoritePredictionUseCase`.
- Обновлять `PredictionDetailState.isFavorite` из результата persisted toggle.
- Сохранить MVP feedback.
- Сохранить analytics event `prediction_favorite_clicked`.
- Убрать зависимость favorite behavior от чисто локального state.

**Definition of Done:**

- Favorite state восстанавливается после повторного открытия detail.
- Favorite переживает перезапуск приложения.
- Existing detail actions продолжают работать.

---

### HISTORY-010: Обновить detail-тесты под persisted favorite

**Тип:** Tests  
**Приоритет:** High  
**Зависимости:** `HISTORY-009`  
**Файлы:**

- `composeApp/src/commonTest/kotlin/.../feature/prediction/detail/PredictionDetailViewModelTest.kt`

**Что сделать:**

- Обновить fake dependencies для favorite use cases.
- Проверить initial favorite false.
- Проверить initial favorite true.
- Проверить `FavoriteClicked` вызывает persisted toggle.
- Проверить repeated `FavoriteClicked` снимает favorite.
- Проверить feedback и analytics после перехода на persisted favorite.

**Definition of Done:**

- Detail-тесты отражают persisted source of truth.
- Старые локальные-only assumptions удалены.
- `./gradlew :composeApp:allTests` проходит.

---

### HISTORY-011: Провести QA сценариев history/favorite

**Тип:** QA  
**Приоритет:** Medium  
**Зависимости:** `HISTORY-008`, `HISTORY-009`, `HISTORY-010`  
**Файлы:**

- `docs/prediction-history/PREDICTION_HISTORY_QA.md`

**Что сделать:**

- Проверить сценарий Home view -> history record.
- Проверить отсутствие дублей history.
- Проверить add/remove favorite на Detail.
- Проверить восстановление favorite после повторного открытия detail.
- Проверить, что текущие Home/Detail UI не получили regressions.
- Зафиксировать ограничения QA, если нет screenshot/device harness.

**Definition of Done:**

- QA документ создан.
- Основные сценарии history/favorite отмечены.
- Остаточные риски явно записаны.

---

### HISTORY-012: Запустить verification

**Тип:** Verification  
**Приоритет:** High  
**Зависимости:** `HISTORY-007`, `HISTORY-010`, `HISTORY-011`  
**Файлы:** нет обязательных

**Что сделать:**

Запустить:

```bash
./gradlew :composeApp:allTests
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

**Definition of Done:**

- Все три команды проходят.
- Если команда падает по внешней причине, причина явно зафиксирована.
- Перед push нет известных regressions.

---

### HISTORY-013: Обновить статус документации

**Тип:** Documentation / Status  
**Приоритет:** Medium  
**Зависимости:** `HISTORY-012`  
**Файлы:**

- `docs/prediction-history/PREDICTION_HISTORY_TASKS.md`
- `docs/prediction-history/PREDICTION_HISTORY_TZ.md`
- `docs/prediction-history/PREDICTION_HISTORY_QA.md`

**Что сделать:**

- Обновить статусы выполненных задач.
- Зафиксировать результаты verification.
- Уточнить отложенные пункты, если в процессе реализации были приняты решения.
- Оставить понятный след для Profile MVP.

**Definition of Done:**

- Документация отражает фактическое состояние реализации.
- Все отложенные пункты остаются явно вынесенными за scope MVP.
- Следующий шаг после history/favorites понятен без чтения истории чата.

## 4. Test Plan

Обязательные проверки для эпика:

- Home view создаёт history record.
- Повторный record с тем же key не создаёт дубль.
- History list сортируется от новых к старым.
- Detail initial favorite state берётся из persistence.
- Favorite click добавляет persisted favorite.
- Повторный favorite click снимает persisted favorite.
- Favorites list возвращает сохранённые items.
- Existing Home/Detail flows не регрессируют.

Обязательные verification-команды:

```bash
./gradlew :composeApp:allTests
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

## 5. Definition of Done для Prediction History & Persisted Favorites MVP

Эпик считается завершённым, когда:

- SQLDelight подключён в `composeApp`.
- Schema history/favorites создана и компилируется.
- History сохраняется локально и idempotent.
- Favorites сохраняются локально.
- Detail использует persisted favorite state.
- Use cases для future Profile готовы.
- Unit-тесты storage/repository/use cases/detail state добавлены.
- QA history/favorite сценариев задокументирован.
- `allTests`, `assembleDebug` и iOS compile проходят.
- Profile UI, bottom navigation, real share sheet и image share card не попали в этот MVP scope.

## 6. Рекомендуемый порядок реализации

1. Закрыть `HISTORY-001`.
2. Подключить SQLDelight: `HISTORY-002`.
3. Создать schema: `HISTORY-003`.
4. Создать domain-модели: `HISTORY-004`.
5. Реализовать data source: `HISTORY-005`.
6. Реализовать repository/use cases: `HISTORY-006`.
7. Добавить тесты persistence/use cases: `HISTORY-007`.
8. Интегрировать запись history: `HISTORY-008`.
9. Подключить persisted favorite в detail: `HISTORY-009`.
10. Обновить detail-тесты: `HISTORY-010`.
11. Провести QA: `HISTORY-011`.
12. Запустить verification: `HISTORY-012`.
13. Обновить документацию по факту: `HISTORY-013`.

## 7. Что брать первым

Первым нужно выполнить `HISTORY-001`, затем сразу переходить к `HISTORY-002`.

Ключевая техническая развилка перед кодом уже выбрана: использовать SQLDelight для history/favorites, а не Multiplatform Settings. Это соответствует `PLAN.md` и снижает риск будущей миграции перед Profile MVP.
