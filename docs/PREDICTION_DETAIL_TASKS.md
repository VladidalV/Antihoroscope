# Prediction Detail MVP: task breakdown

Документ декомпозирует Epic 3 — `Prediction Detail MVP`.

Базовое ТЗ: `docs/PREDICTION_DETAIL_TZ.md`.

## 1. Целевой результат

После выполнения эпика в приложении появляется детальный экран предсказания:

- Home card открывает detail с тем же предсказанием.
- Detail показывает полный текст и метаданные.
- Back возвращает на Home без потери текущего Home state.
- `Поделиться`, `В избранное` и `Следующее` дают MVP feedback.
- Основные действия покрыты analytics events.
- Реализация остаётся в текущей архитектуре `composeApp`, без Decompose/Koin, SQLDelight и persisted favorites.

## 2. Итерации

### Итерация 1: Docs и contract

- `DETAIL-001`: создать ТЗ и task breakdown.
- `DETAIL-002`: создать state contract detail-экрана.

### Итерация 2: State holder и тесты

- `DETAIL-003`: реализовать `PredictionDetailViewModel` или lightweight state holder.
- `DETAIL-004`: добавить unit-тесты detail state/viewmodel.

### Итерация 3: UI

- `DETAIL-005`: создать UI-компонент detail-карточки.
- `DETAIL-006`: реализовать `PredictionDetailScreen`.

### Итерация 4: Integration, feedback, analytics

- `DETAIL-007`: подключить переход из Home.
- `DETAIL-008`: добавить MVP feedback для share/favorite/next actions.
- `DETAIL-009`: подключить analytics events.

### Итерация 5: QA, verification, статус

- `DETAIL-010`: провести visual QA detail-экрана.
- `DETAIL-011`: запустить verification-команды.
- `DETAIL-012`: обновить статус документации.

## 3. Задачи

### DETAIL-001: Создать ТЗ и task breakdown для Prediction Detail

**Тип:** Documentation  
**Приоритет:** High  
**Зависимости:** нет  
**Файлы:**

- `docs/PREDICTION_DETAIL_TZ.md`
- `docs/PREDICTION_DETAIL_TASKS.md`

**Что сделать:**

- Описать продуктовую цель `Prediction Detail MVP`.
- Зафиксировать MVP scope и out of scope.
- Описать пользовательские сценарии.
- Описать UI composition.
- Описать архитектурные решения и ограничения.
- Зафиксировать analytics events.
- Декомпозировать эпик на задачи `DETAIL-001` - `DETAIL-012`.

**Definition of Done:**

- Оба документа созданы.
- Scope явно отделяет MVP от следующих эпиков.
- В документах есть Test Plan и Acceptance Criteria.

---

### DETAIL-002: Создать state contract для Prediction Detail

**Тип:** Architecture / State  
**Приоритет:** High  
**Зависимости:** `DETAIL-001`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailState.kt`
- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailIntent.kt`
- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailEvent.kt`

**Что сделать:**

- Создать package `feature/prediction/detail`.
- Описать `PredictionDetailState`.
- Описать `PredictionDetailIntent`.
- Описать `PredictionDetailEvent`.
- Использовать существующие модели `DailyPrediction`/`Prediction`.
- Не добавлять persistence-модели.

**Рекомендуемый contract:**

- State хранит выбранное предсказание, локальный favorite selected state и transient feedback message.
- Intent обрабатывает `BackClicked`, `ShareClicked`, `FavoriteClicked`, `NextClicked`, `FeedbackShown`.
- Event сообщает наружу `NavigateBack` и одноразовые feedback-события при необходимости.

**Definition of Done:**

- Contract компилируется в commonMain.
- Имена и структура соответствуют существующему стилю feature-контрактов.
- State можно тестировать без UI.

---

### DETAIL-003: Реализовать PredictionDetailViewModel или lightweight state holder

**Тип:** State management  
**Приоритет:** High  
**Зависимости:** `DETAIL-002`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailViewModel.kt`

**Что сделать:**

- Реализовать обработку intents.
- Хранить текущий detail state.
- Обрабатывать локальный favorite selected state.
- Формировать feedback для share/favorite/next.
- Отправлять navigation event при back.
- Не добавлять зависимость на SQLDelight, backend или real share APIs.

**Definition of Done:**

- ViewModel/state holder не зависит от UI.
- Все MVP-действия приводят к ожидаемому изменению state или event.
- Реализация остаётся commonMain-compatible.

---

### DETAIL-004: Добавить unit-тесты detail state/viewmodel

**Тип:** Tests  
**Приоритет:** High  
**Зависимости:** `DETAIL-003`  
**Файлы:**

- `composeApp/src/commonTest/kotlin/.../feature/prediction/detail/PredictionDetailViewModelTest.kt`

**Что сделать:**

- Проверить initial state с переданным предсказанием.
- Проверить `ShareClicked`.
- Проверить `FavoriteClicked`.
- Проверить `NextClicked`.
- Проверить `BackClicked`.
- Проверить очистку transient feedback, если она реализована через отдельный intent.

**Definition of Done:**

- Тесты запускаются через `./gradlew :composeApp:allTests`.
- Тесты не требуют Android/iOS runtime.
- Основные intents покрыты.

---

### DETAIL-005: Создать UI-компонент detail-карточки

**Тип:** UI  
**Приоритет:** High  
**Зависимости:** `DETAIL-002`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../ui/components/PredictionDetailCard.kt`

**Что сделать:**

- Создать компонент для основного detail-контента.
- Переиспользовать визуальный стиль `GlowingPredictionCard`.
- Показать полный текст предсказания.
- Показать metadata row.
- Учесть длинный русский текст.
- Избежать вложенных карточек и визуального шума.

**Definition of Done:**

- Компонент можно использовать внутри `PredictionDetailScreen`.
- Текст не обрезается без необходимости.
- Metadata переносится на узких экранах без overlap.
- Компонент не содержит navigation/business logic.

---

### DETAIL-006: Реализовать PredictionDetailScreen

**Тип:** UI / Screen  
**Приоритет:** High  
**Зависимости:** `DETAIL-003`, `DETAIL-005`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailScreen.kt`

**Что сделать:**

- Собрать экран на `CosmicBackground`.
- Добавить top bar/back action.
- Встроить detail-card или unframed detail content.
- Добавить action buttons: `Поделиться`, `В избранное`, `Следующее`.
- Поддержать scroll для длинного контента.
- Поддержать compact ширину.

**Definition of Done:**

- Экран отображает все данные выбранного предсказания.
- Back action вызывает соответствующий intent/event.
- Action buttons вызывают соответствующие intents.
- Long text layout визуально устойчив.

---

### DETAIL-007: Подключить переход из Home через HomeEvent.PredictionSelected

**Тип:** Navigation / Integration  
**Приоритет:** High  
**Зависимости:** `DETAIL-006`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../App.kt`
- `composeApp/src/commonMain/kotlin/.../feature/home/HomeScreen.kt`
- `composeApp/src/commonMain/kotlin/.../feature/home/HomeViewModel.kt`

**Что сделать:**

- Сделать Home-карточку кликабельной, если это ещё не подключено.
- По клику отправлять существующий `HomeIntent.PredictionClicked`.
- Использовать `HomeEvent.PredictionSelected`.
- В `App.kt` открыть detail-экран с текущим `DailyPrediction`.
- Не добавлять полноценный navigation framework.

**Рекомендуемая MVP-модель:**

- `HomeEvent.PredictionSelected(predictionId)` остаётся событием выбора.
- `App.kt` берёт текущий `HomeState.Content`.
- Если `predictionId` совпадает с `dailyPrediction.prediction.id`, в detail передаётся текущий `DailyPrediction`.
- Если Home state не содержит prediction, переход игнорируется или обрабатывается fallback-логикой.

**Definition of Done:**

- Тап по Home card открывает detail.
- Detail получает то же prediction, которое было на Home.
- Back возвращает на Home.
- Home state не теряется при возврате.

---

### DETAIL-008: Добавить MVP feedback для share/favorite actions

**Тип:** UX  
**Приоритет:** Medium  
**Зависимости:** `DETAIL-006`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailScreen.kt`
- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailViewModel.kt`

**Что сделать:**

- Для `Поделиться` показывать короткое MVP-сообщение.
- Для `В избранное` показывать короткое MVP-сообщение или переключать локальный selected state.
- Для `Следующее` показывать сообщение, что действие отложено.
- Очистить transient feedback после показа, если текущий UI-паттерн этого требует.

**Definition of Done:**

- Share click не открывает real share sheet.
- Favorite click не пишет в persistence.
- Пользователь получает понятный feedback.
- Feedback не показывается повторно после recomposition без нового действия.

---

### DETAIL-009: Подключить analytics events

**Тип:** Analytics  
**Приоритет:** High  
**Зависимости:** `DETAIL-003`, `DETAIL-007`, `DETAIL-008`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/.../analytics/AnalyticsTracker.kt`
- `composeApp/src/commonMain/kotlin/.../feature/prediction/detail/PredictionDetailViewModel.kt`
- `composeApp/src/commonMain/kotlin/.../App.kt`

**Что сделать:**

- Отправить `prediction_detail_viewed` при открытии detail.
- Отправить `prediction_detail_share_clicked` при share click.
- Отправить `prediction_favorite_clicked` при favorite click.
- Отправить `prediction_detail_back_clicked` при back click.
- Передавать полезные параметры, если это поддержано текущим `AnalyticsTracker`.

**Definition of Done:**

- Все обязательные события отправляются через `AnalyticsTracker`.
- События не дублируются на обычной recomposition.
- Unit-тесты или lightweight fake tracker проверяют ключевые действия, если текущая архитектура позволяет.

---

### DETAIL-010: Провести visual QA detail-экрана

**Тип:** QA / Visual  
**Приоритет:** Medium  
**Зависимости:** `DETAIL-006`, `DETAIL-008`  
**Файлы:**

- `docs/PREDICTION_DETAIL_VISUAL_QA.md`

**Что сделать:**

- Проверить экран на компактной мобильной ширине.
- Проверить экран на более широкой desktop/tablet ширине, если доступно.
- Проверить длинный русский текст.
- Проверить перенос metadata.
- Проверить action buttons.
- Проверить состояние после share/favorite/next feedback.
- Зафиксировать результаты в visual QA документе.

**Definition of Done:**

- Нет overlap текста и кнопок.
- Длинный текст скроллится и читается.
- Back/action зоны доступны.
- Результат visual QA задокументирован.

---

### DETAIL-011: Запустить verification

**Тип:** Verification  
**Приоритет:** High  
**Зависимости:** `DETAIL-004`, `DETAIL-009`, `DETAIL-010`  
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

### DETAIL-012: Обновить статус документации

**Тип:** Documentation / Status  
**Приоритет:** Medium  
**Зависимости:** `DETAIL-011`  
**Файлы:**

- `docs/PREDICTION_DETAIL_TASKS.md`
- `docs/PREDICTION_DETAIL_TZ.md`
- `docs/PREDICTION_DETAIL_VISUAL_QA.md`

**Что сделать:**

- Обновить статусы выполненных задач, если в документе используется статусная секция.
- Зафиксировать результаты verification.
- Уточнить отложенные пункты, если в процессе реализации были приняты решения.
- Оставить понятный след для следующего эпика.

**Definition of Done:**

- Документация отражает фактическое состояние реализации.
- Все отложенные пункты остаются явно вынесенными за scope MVP.
- Следующая задача после Prediction Detail понятна без чтения истории чата.

## 4. Test Plan

Обязательные проверки для эпика:

- Home card tap открывает detail с тем же prediction.
- Back возвращает на Home без потери Home state.
- Detail показывает длинный русский текст без overlap.
- Share click показывает MVP message.
- Favorite click показывает MVP message или локальный selected state.
- Analytics events отправляются через `AnalyticsTracker`.

Обязательные verification-команды:

```bash
./gradlew :composeApp:allTests
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

## 5. Definition of Done для Prediction Detail MVP

Эпик считается завершённым, когда:

- `PredictionDetailScreen` реализован и подключён.
- Переход из Home работает через `HomeEvent.PredictionSelected`.
- Detail получает и отображает выбранный `DailyPrediction`/`Prediction`.
- Back возвращает на Home без сброса состояния.
- Share/favorite/next actions имеют MVP feedback.
- Analytics events подключены.
- Unit-тесты detail state/viewmodel добавлены.
- Visual QA пройден и задокументирован.
- `allTests`, `assembleDebug` и iOS compile проходят.
- SQLDelight, persisted favorites, real share sheet и image share card не попали в MVP scope.

## 6. Рекомендуемый порядок реализации

1. Закрыть `DETAIL-001`.
2. Реализовать contract: `DETAIL-002`.
3. Реализовать state holder: `DETAIL-003`.
4. Добавить unit-тесты: `DETAIL-004`.
5. Собрать detail-card и screen: `DETAIL-005`, `DETAIL-006`.
6. Подключить Home navigation: `DETAIL-007`.
7. Добавить MVP feedback: `DETAIL-008`.
8. Подключить analytics: `DETAIL-009`.
9. Провести visual QA: `DETAIL-010`.
10. Запустить verification: `DETAIL-011`.
11. Обновить документацию по факту: `DETAIL-012`.

## 7. Что брать первым

Первым нужно выполнить `DETAIL-001`, затем сразу переходить к `DETAIL-002`.

Ключевая техническая развилка перед кодом: выбрать, будет ли detail получать `DailyPrediction` напрямую из текущего `HomeState.Content` в `App.kt` или будет выполнять lookup по `predictionId`. Для MVP предпочтительнее первый вариант: он проще, не требует новой persistence-модели и соответствует текущему scope.
