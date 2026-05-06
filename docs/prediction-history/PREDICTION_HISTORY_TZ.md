# ТЗ: Epic 4 — Prediction History & Persisted Favorites MVP

Документ описывает следующий продуктовый блок после Home и Prediction Detail: локальную историю просмотренных предсказаний и сохранение избранного между экранами и перезапусками приложения.

Связанный task breakdown: `docs/prediction-history/PREDICTION_HISTORY_TASKS.md`.

## 1. Цель блока

Дать приложению первый устойчивый persistence-слой для пользовательской ценности после чтения предсказаний.

Prediction History & Persisted Favorites MVP должен подготовить основу для Profile MVP:

- Home и Detail уже показывают предсказания.
- History сохраняет факт просмотра и snapshot предсказания.
- Favorites сохраняют пользовательский выбор не только в рамках текущего detail-экрана.
- Будущий Profile сможет показать историю, избранное и базовую статистику без повторного проектирования storage.

## 2. Scope MVP

### 2.1 Входит

- SQLDelight setup для локального KMP storage.
- SQLDelight schema для history и favorites.
- Domain-модели для history/favorite items, если `DailyPrediction` и `Prediction` недостаточны для списков.
- Local data source для записи и чтения history/favorites.
- Repository/use cases:
  - `RecordPredictionViewUseCase`;
  - `GetPredictionHistoryUseCase`;
  - `ToggleFavoritePredictionUseCase`;
  - `IsFavoritePredictionUseCase`;
  - `GetFavoritePredictionsUseCase`.
- Интеграция записи history в Home/Detail без дублей на recomposition.
- Persisted favorite state в `PredictionDetailViewModel`.
- Unit-тесты storage/repository/use cases/detail state.
- Verification-команды для Android и iOS targets.

### 2.2 Не входит

- `ProfileScreen`.
- Full `SettingsScreen`.
- Bottom navigation.
- Cloud sync/backend.
- Real account/user model.
- Real platform share sheet.
- Image share card.
- Remote migration или синхронизация истории.
- Koin/Decompose или большая переработка текущей навигации.

## 3. Пользовательские сценарии

### 3.1 Открыть Home и сохранить daily prediction в history

Пользователь попадает на Home после онбординга или обычного запуска. Когда дневное предсказание успешно показано, приложение сохраняет просмотр в локальную history.

Ожидаемое поведение:

- запись history создаётся один раз для текущего prediction/date/zodiac/source;
- обычная recomposition не создаёт дубликаты;
- Home UI не блокируется на записи history;
- существующий daily prediction flow не меняется визуально.

### 3.2 Открыть Detail и не продублировать просмотр без необходимости

Пользователь открывает detail по тапу на Home-карточку. Detail получает тот же `DailyPrediction`, что был на Home.

Ожидаемое поведение:

- detail может зафиксировать отдельный source, если это нужно продуктово;
- повторные recomposition/detail state collection не создают новые записи;
- если выбран MVP-вариант без отдельной detail-записи, Home history остаётся единственным просмотром для этого prediction/date/zodiac.

### 3.3 Добавить prediction в избранное

Пользователь нажимает `В избранное` на detail-экране.

Ожидаемое поведение:

- favorite сохраняется в SQLDelight;
- кнопка меняет состояние на `В избранном`;
- пользователь видит MVP feedback;
- событие `prediction_favorite_clicked` продолжает отправляться;
- после закрытия detail и повторного открытия state восстанавливается из persistence.

### 3.4 Снять prediction из избранного

Пользователь повторно нажимает favorite action на detail-экране.

Ожидаемое поведение:

- favorite удаляется или помечается как inactive в storage;
- кнопка возвращается в состояние `В избранное`;
- пользователь видит MVP feedback;
- список будущих favorites больше не содержит это предсказание.

### 3.5 Получить favorites для будущего Profile

Будущий `ProfileScreen` или state holder запрашивает список избранных предсказаний.

Ожидаемое поведение:

- use case возвращает список favorite items;
- список содержит достаточный snapshot для отображения без дополнительного lookup;
- порядок по умолчанию: новые favorites выше старых.

### 3.6 Получить последние history items для будущего Profile

Будущий `ProfileScreen` или state holder запрашивает последние просмотры.

Ожидаемое поведение:

- use case возвращает history items;
- список отсортирован от новых к старым;
- количество можно ограничить параметром use case или repository;
- данные доступны офлайн.

## 4. Data Model

### 4.1 History identity

Для idempotency history используется стабильный ключ:

`predictionId + dateKey + zodiacSignId + source`

Требования:

- обычная recomposition не должна создавать дубликаты;
- повторная запись того же ключа должна быть no-op или update существующей записи;
- source должен быть строковым значением, например `home` или `detail`.

### 4.2 History snapshot

History item должен хранить минимальный snapshot для будущего отображения:

- `historyId` или составной stable key;
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

### 4.3 Favorite identity

Favorite хранится по `predictionId`.

Требования:

- один prediction не должен иметь несколько active favorite-записей;
- повторный toggle снимает favorite;
- для отображения favorites хранится snapshot нужных полей prediction/daily context;
- порядок списка favorites по умолчанию: `favoritedAtEpochMillis DESC`.

### 4.4 Favorite snapshot

Favorite item должен хранить:

- `predictionId`;
- `predictionText`;
- `category`;
- `absurdityLevel`;
- `zodiacSignId`;
- `zodiacSignName`;
- `dateKey`;
- `dateLabel`;
- `favoritedAtEpochMillis`.

## 5. Архитектура

### 5.1 SQLDelight

В рамках MVP нужно добавить SQLDelight в текущий `composeApp`.

Требования:

- common schema доступна для Android и iOS targets;
- platform-specific driver создаётся через expect/actual или другой существующий KMP-compatible паттерн;
- schema и generated queries не должны требовать backend или аккаунта;
- migrations должны быть подготовлены так, чтобы будущие изменения history/favorites не ломали локальные данные.

### 5.2 Local data source

Рекомендуемый package:

`data/prediction/history`

Рекомендуемые сущности:

- `PredictionHistoryLocalDataSource`;
- `SqlDelightPredictionHistoryLocalDataSource`;
- mapper между SQLDelight rows и domain-моделями.

Конкретные имена можно адаптировать под фактический style проекта, но storage-граница должна быть явной и тестируемой.

### 5.3 Repository / use cases

MVP может использовать отдельный repository для history/favorites, чтобы не перегружать текущий `PredictionRepository`.

Рекомендуемый contract:

- `PredictionHistoryRepository`;
- `RecordPredictionViewUseCase`;
- `GetPredictionHistoryUseCase`;
- `ToggleFavoritePredictionUseCase`;
- `IsFavoritePredictionUseCase`;
- `GetFavoritePredictionsUseCase`.

Use cases должны оставаться commonMain-compatible и не зависеть от UI.

### 5.4 Integration в Home/Detail

Текущая архитектура остаётся без Koin/Decompose.

Требования:

- зависимости собираются вручную в `App.kt`, как Home/Detail сейчас;
- запись history вызывается один раз на пользовательски значимый просмотр, а не на каждую recomposition;
- `PredictionDetailViewModel` получает initial favorite state из persistence;
- `FavoriteClicked` вызывает persisted toggle и обновляет state;
- локальный favorite state из Detail MVP заменяется persisted source of truth.

### 5.5 Compatibility

Эпик не должен ломать:

- onboarding flow;
- Home daily prediction;
- Home manual refresh и лимит;
- переход Home -> Detail;
- Detail MVP feedback;
- текущие analytics events.

## 6. Analytics

Обязательное событие уже существует:

- `prediction_favorite_clicked`.

MVP должен сохранить его поведение при переходе на persisted favorite.

Опциональное событие:

- `prediction_history_recorded`.

Это событие можно добавить, если оно ложится в текущий `AnalyticsTracker` без расширения инфраструктуры. Отсутствие отдельного history analytics event не блокирует MVP, если запись history покрыта тестами.

Рекомендуемые параметры:

- `prediction_id`;
- `zodiac_sign`;
- `category`;
- `absurdity_level`;
- `date`;
- `source`;
- `is_favorite` для favorite toggle.

## 7. Test Plan

Обязательные unit-тесты:

- запись history создаёт запись;
- повторная запись того же просмотра не создаёт дубль;
- history list сортируется от новых к старым;
- toggle favorite добавляет favorite;
- повторный toggle снимает favorite;
- favorites list возвращает сохранённые items;
- favorite state восстанавливается после создания нового `PredictionDetailViewModel`;
- `FavoriteClicked` вызывает persisted use case и обновляет state;
- текущие detail analytics tests продолжают проходить.

Обязательные verification-команды:

```bash
./gradlew :composeApp:allTests
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

## 8. Acceptance Criteria

Prediction History & Persisted Favorites MVP считается готовым, если:

- SQLDelight подключён для Android и iOS targets;
- history сохраняется локально;
- повторные recomposition не создают дубликаты history;
- favorites сохраняются локально;
- favorite state переживает закрытие и повторное открытие detail;
- `PredictionDetailViewModel` использует persisted favorite state;
- Home/Detail сценарии не регрессируют;
- Profile-ready use cases для history и favorites доступны;
- unit-тесты покрывают storage/repository/use cases/detail state;
- visual/code-level QA сценариев history/favorite зафиксирован;
- `allTests`, `assembleDebug` и iOS compile проходят.

## 9. Что явно отложено

Следующие блоки не должны попадать в реализацию этого MVP:

- `ProfileScreen`;
- полноценная статистика профиля;
- bottom navigation;
- real share sheet;
- image share card;
- cloud sync/backend;
- account/auth model;
- remote migration;
- Koin/Decompose migration;
- визуальный favorites/history UI, кроме состояния favorite на detail.
