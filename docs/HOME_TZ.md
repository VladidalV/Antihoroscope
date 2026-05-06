# ТЗ: Epic 2 — Дневное предсказание / Home MVP

Документ описывает следующий продуктовый блок после завершённого онбординга: главный экран приложения с дневным антипредсказанием, локальной генерацией, категориями и лимитом ручной перегенерации.

Связанный breakdown: `docs/HOME_TASKS.md`.

## 1. Цель блока

Сделать первый полноценный пользовательский опыт после онбординга:

- пользователь видит выбранный знак зодиака;
- каждый день получает стабильное смешное антипредсказание;
- может вручную запросить другое предсказание до 3 раз в день;
- может выбрать категорию настроения: любовь, деньги, карьера, здоровье, хаос;
- видит визуально законченный Home в стиле приложения: тёмный космос, неон, glowing-карточка, ироничная мистическая подача.

Главный результат: `HomePlaceholderScreen` заменён на реальный `HomeScreen`, а приложение после онбординга становится usable MVP, а не набором заготовок.

## 2. Scope MVP

### Входит

- `HomeScreen` после завершения онбординга.
- Header с выбранным знаком, датой и короткой ироничной подписью.
- Glowing-карточка дневного предсказания.
- Локальный банк предсказаний в commonMain.
- Детерминированная генерация по seed.
- Категории:
  - любовь;
  - деньги;
  - карьера;
  - здоровье;
  - хаос.
- Уровень абсурдности `1..5`.
- Ручная перегенерация с дневным лимитом `3`.
- Сохранение счётчика лимита между перезапусками.
- Сброс лимита при смене даты.
- MVP-заглушка для share action.
- Unit-тесты генератора и лимита.
- Базовые analytics events через существующий `AnalyticsTracker`.

### Не входит

- SQLDelight и история предсказаний.
- Детальный экран предсказания.
- Избранное.
- Реальный platform share sheet.
- Генерация image card для шаринга.
- Remote Config / backend.
- Community/feed.
- Premium prediction packs.
- Реклама.
- Полноценная bottom navigation, если она не нужна для Home MVP end-to-end.

Важно: в `PLAN.md` SQLDelight и история указаны как целевая архитектура, но для Home MVP лучше не блокировать первый usable экран на миграции хранилища. Историю и кеш можно добавить следующим эпиком.

## 3. Пользовательские сценарии

### 3.1 Первый вход после онбординга

1. Пользователь завершает онбординг.
2. Приложение открывает `HomeScreen`.
3. `HomeViewModel` читает выбранный `zodiacSignId` из существующего settings storage.
4. `GetDailyPredictionUseCase` получает текущий `dateKey`.
5. `GenerateDailyPredictionUseCase` выбирает предсказание по `dateKey + zodiacSignId`.
6. Пользователь видит карточку дневного антипредсказания.

### 3.2 Повторный запуск в тот же день

1. Пользователь открывает приложение.
2. Показывается тот же daily prediction.
3. Счётчик ручных генераций сохраняется.
4. Если лимит исчерпан, кнопка перегенерации остаётся заблокированной до следующего дня.

### 3.3 Новый день

1. Дата изменилась.
2. Daily prediction меняется за счёт нового seed.
3. Счётчик ручных генераций сбрасывается до `3`.

### 3.4 Выбор категории

1. Пользователь выбирает категорию в горизонтальном списке.
2. Категория подсвечивается.
3. Следующая ручная генерация использует выбранную категорию как фильтр.
4. Daily prediction по умолчанию не обязан меняться сразу при выборе категории.

Такой сценарий сохраняет смысл "дневного" предсказания и при этом даёт пользователю контроль при ручном refresh.

### 3.5 Ручная перегенерация

1. Пользователь нажимает `Другое предсказание`.
2. Если лимит доступен:
   - генерируется новое предсказание;
   - текущее предсказание заменяется на экране;
   - счётчик использованных генераций увеличивается;
   - remaining count уменьшается.
3. Если лимит исчерпан:
   - новое предсказание не генерируется;
   - показывается inline-сообщение;
   - кнопка становится disabled или визуально exhausted.

Текст exhausted state:

```text
На сегодня космос выдохся. Возвращайся завтра.
```

## 4. HomeScreen

### 4.1 Композиция экрана

Экран строится сверху вниз:

1. `CosmicBackground`.
2. Safe area / status bar padding.
3. Header:
   - знак зодиака;
   - дата;
   - короткая подпись.
4. Main content:
   - `GlowingPredictionCard`;
   - category row;
   - limit indicator;
   - actions.
5. Нижний отступ под системную навигацию.

### 4.2 Header

Показывает:

- `zodiacSign.nameRu`;
- текущую дату в человекочитаемом формате;
- короткий sarcastic subtitle.

Примеры subtitle:

- `Звёзды посмотрели. Им есть что сказать.`
- `Космос сегодня настроен пассивно-агрессивно.`
- `Небесная канцелярия прислала записку.`

### 4.3 Daily prediction card

Показывает:

- category label;
- основной текст предсказания;
- absurdity level;
- zodiac sign;
- date label.

Примеры:

```text
Хаос · Абсурдность 4/5
Сегодня нельзя брить ноги.
Овен · 5 мая 2026
```

### 4.4 Category row

Категории:

- `Любовь`;
- `Деньги`;
- `Карьера`;
- `Здоровье`;
- `Хаос`.

Поведение:

- один selected item;
- повторный тап по выбранной категории оставляет её выбранной;
- категория влияет на manual generation;
- category row не должен ломать layout на маленьких экранах, поэтому нужен horizontal scroll.

### 4.5 Actions

Основная кнопка:

```text
Другое предсказание
```

Состояния:

- enabled, если `remainingGenerations > 0`;
- disabled/exhausted, если `remainingGenerations == 0`;
- loading, пока идёт обработка refresh.

Вторичная кнопка:

```text
Поделиться
```

Для MVP:

- не открывает реальный share sheet;
- отправляет `HomeEvent.ShowMessage`;
- текст: `Скоро можно будет отправить это в чат.`

### 4.6 Empty / error states

Если знак не найден:

```text
Космос потерял твой знак
Вернись в настройки или пройди онбординг заново.
```

Если каталог пуст:

```text
Звёзды молчат
Похоже, локальный банк предсказаний не загрузился.
```

Если generation failed:

```text
Предсказание застряло в ретрограде
Попробуй ещё раз чуть позже.
```

## 5. Архитектура

### 5.1 Слои

Реализация должна оставаться в текущей KMP/Compose структуре:

- `domain/prediction` — чистые модели и use cases;
- `data/prediction` — локальный каталог и repository implementation;
- `feature/home` — screen, state, view model;
- `feature/home/components` — home-specific UI components;
- `ui/components` — переиспользуемые visual components;
- `core/time` — date provider;
- `core/analytics` — analytics events через существующий tracker.

### 5.2 Dependency flow

```text
HomeScreen
  -> HomeViewModel
    -> GetDailyPredictionUseCase
    -> GenerateManualPredictionUseCase
    -> GetGenerationLimitUseCase
    -> ConsumeGenerationLimitUseCase
      -> PredictionRepository
      -> HomeSettingsStorage
      -> DateProvider
```

UI не должен:

- сам читать settings;
- сам считать seed;
- сам выбирать prediction из списка;
- сам управлять лимитом.

### 5.3 DI подход

Для Home MVP можно использовать ручную сборку зависимостей в `App.kt`, как уже сделано в онбординге.

Целевой следующий шаг после MVP:

- вынести создание зависимостей в `AppContainer`;
- позже подключить Koin, если количество feature modules начнёт расти.

## 6. Domain-модели

### 6.1 `PredictionCategory`

```kotlin
enum class PredictionCategory {
    Love,
    Money,
    Career,
    Health,
    Chaos
}
```

Нужны extension-свойства:

- `titleRu`;
- `analyticsName`;
- `accentColorHex`;
- `shortLabel`.

### 6.2 `Prediction`

```kotlin
data class Prediction(
    val id: String,
    val text: String,
    val category: PredictionCategory,
    val absurdityLevel: Int,
    val zodiacSignId: String?,
    val tags: List<String>,
)
```

Правила:

- `id` уникальный;
- `text` не пустой;
- `absurdityLevel` в диапазоне `1..5`;
- `zodiacSignId = null` означает, что предсказание подходит всем знакам.

### 6.3 `DailyPrediction`

```kotlin
data class DailyPrediction(
    val dateKey: String,
    val zodiacSignId: String,
    val prediction: Prediction,
    val generatedManually: Boolean,
)
```

### 6.4 `GenerationLimit`

```kotlin
data class GenerationLimit(
    val dateKey: String,
    val usedCount: Int,
    val maxCount: Int = 3,
) {
    val remainingCount: Int
        get() = (maxCount - usedCount).coerceAtLeast(0)
}
```

## 7. Prediction bank

### 7.1 MVP-формат

Для первого шага допустим Kotlin object:

```text
LocalPredictionCatalog.predictions: List<Prediction>
```

Причина: быстрее собрать end-to-end flow и unit-тесты без добавления JSON parser/Compose resources нюансов.

### 7.2 Целевой формат

Позже каталог можно перенести в JSON:

```json
{
  "id": "chaos_001",
  "text": "Сегодня нельзя брить ноги.",
  "category": "chaos",
  "absurdityLevel": 4,
  "zodiacSignId": null,
  "tags": ["daily", "absurd"]
}
```

### 7.3 Минимум для Home MVP

- минимум 50 предсказаний;
- минимум 10 на категорию;
- часть общих;
- часть знако-специфичных;
- стиль: смешно, абсурдно, без токсичности и персональных оскорблений.

### 7.4 Цель перед релизом

- 200+ предсказаний;
- около 40 на категорию;
- регулярная редактура повторов;
- отдельный content QA.

## 8. Генерация

### 8.1 Date key

Формат:

```text
YYYY-MM-DD
```

На MVP можно реализовать `DateProvider` через platform-specific actual или через доступную common time API, если она уже подключена.

Требование: use cases получают дату через injectable provider, чтобы тесты не зависели от системных часов.

### 8.2 Daily prediction

Алгоритм:

1. Получить `dateKey`.
2. Получить `zodiacSignId`.
3. Собрать pool:
   - prediction с `zodiacSignId == null`;
   - prediction с `zodiacSignId == currentZodiacSignId`.
4. Посчитать seed:

```text
hash("$dateKey:$zodiacSignId:daily")
```

5. Выбрать индекс:

```text
abs(seed) % pool.size
```

6. Вернуть `DailyPrediction`.

Требования:

- одинаковый `dateKey + zodiacSignId` всегда возвращает одинаковый prediction;
- другой знак может получить другое prediction;
- другая дата может получить другое prediction;
- при пустом pool возвращается controlled error/result, а не crash.

### 8.3 Manual prediction

Алгоритм:

1. Проверить лимит.
2. Собрать pool по знаку.
3. Если выбрана категория, отфильтровать pool по категории.
4. Исключить текущий prediction, если после исключения pool не пустой.
5. Seed:

```text
hash("$dateKey:$zodiacSignId:manual:$generationIndex:$category")
```

6. Выбрать prediction.
7. Увеличить лимит только после успешного выбора.

### 8.4 Absurdity level

В Home MVP absurdity level хранится как поле контента и отображается в UI.

Фильтрацию по absurdity slider не реализуем в этом эпике. Slider относится к будущему генератору/расширенному режиму.

## 9. Local storage

### 9.1 Уже есть после онбординга

Нужно переиспользовать существующее хранилище:

- selected zodiac sign;
- onboarding completed;
- notification settings.

### 9.2 Нужно добавить

Для лимита:

- `home_generation_limit_date`;
- `home_generation_limit_used_count`.

Опционально для восстановления manual state:

- `home_last_manual_prediction_id`;
- `home_last_manual_prediction_date`.

Для MVP можно не сохранять последнее manual prediction. Допустимое поведение: после перезапуска снова показывается daily prediction, но лимит ручной генерации остаётся потраченным.

## 10. Presentation state

### 10.1 `HomeState`

Состояния:

- `Loading`;
- `Content`;
- `MissingZodiac`;
- `EmptyCatalog`;
- `Error`.

`Content` содержит:

- `zodiacSign`;
- `dateLabel`;
- `dailyPrediction`;
- `selectedCategory`;
- `generationLimit`;
- `isRefreshing`;
- `message`.

### 10.2 `HomeIntent`

- `ScreenShown`;
- `CategorySelected(category)`;
- `RefreshClicked`;
- `ShareClicked`;
- `PredictionClicked`.

### 10.3 `HomeEvent`

- `ShowMessage(text)`;
- `OpenShareStub`;
- `OpenPredictionDetail(predictionId)` — можно подготовить, но не подключать navigation.

## 11. UI-компоненты

### 11.1 `GlowingPredictionCard`

Файл:

```text
composeApp/src/commonMain/kotlin/com/example/antihoroscope/ui/components/GlowingPredictionCard.kt
```

Требования:

- dark glass background;
- neon gradient border;
- category accent;
- крупный readable text;
- стабильные размеры и padding;
- корректная работа с длинными текстами.

### 11.2 `PredictionCategoryRow`

Файл:

```text
composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/components/PredictionCategoryRow.kt
```

Требования:

- horizontal scroll;
- selected state;
- compact cards;
- без layout shift при переключении.

### 11.3 `GenerationLimitIndicator`

Показывает:

- `Осталось: 3`;
- `Осталось: 2`;
- `Осталось: 1`;
- `Космос выдохся`;

В exhausted state цвет должен отличаться, но не спорить с общей палитрой.

### 11.4 `HomeActionBar`

Содержит:

- primary button refresh;
- secondary share text/icon button.

## 12. Analytics

Через текущий `AnalyticsTracker` добавить события:

- `home_viewed`
  - `zodiac_sign`;
  - `date_key`;
- `prediction_viewed`
  - `prediction_id`;
  - `category`;
  - `absurdity_level`;
  - `zodiac_sign`;
- `prediction_refreshed`
  - `generation_used_count`;
  - `remaining_count`;
  - `category`;
- `prediction_refresh_limit_reached`
  - `date_key`;
- `prediction_category_selected`
  - `category`;
- `prediction_share_clicked`
  - `prediction_id`.

Для MVP это может быть no-op implementation, главное — события должны быть заведены в коде.

## 13. Тестирование

### 13.1 Unit tests

Покрыть:

- same date + same sign = same prediction;
- same date + different sign = допускается другой result;
- different date = допускается другой result;
- category filter работает;
- zodiac-specific prediction попадает в pool;
- empty pool возвращает error;
- manual generation исключает текущий prediction, если возможно;
- 3 manual refresh разрешены;
- 4-й manual refresh запрещён;
- новая дата сбрасывает лимит;
- consume лимита не происходит при ошибке генерации.

### 13.2 Presentation tests

Покрыть:

- initial loading -> content;
- missing zodiac -> `MissingZodiac`;
- category click обновляет selected category;
- refresh success обновляет prediction и remaining count;
- refresh exhausted показывает exhausted state.

### 13.3 Manual visual QA

Проверить:

- маленький Android экран;
- iPhone SE-like экран;
- длинное предсказание;
- exhausted state;
- выбранную категорию;
- dark theme контрастность;
- отсутствие overlap в header/card/actions.

## 14. Acceptance Criteria

- После завершённого онбординга открывается настоящий `HomeScreen`.
- Home показывает выбранный знак зодиака.
- Home показывает дату.
- Home показывает дневное предсказание.
- Daily prediction стабилен в течение одного дня.
- Daily prediction может измениться на следующий день.
- Ручная перегенерация работает.
- Лимит 3 генерации в день соблюдается.
- Лимит сохраняется между перезапусками.
- Лимит сбрасывается на новую дату.
- Категории отображаются и влияют на manual generation.
- Share button не ломает экран и даёт понятный MVP feedback.
- Controlled error states не приводят к crash.
- Unit-тесты генератора и лимита проходят.
- Android debug build проходит.
- Common tests проходят.

## 15. Риски

- Предсказания быстро начнут повторяться при маленьком каталоге.
- Seed может часто давать визуально похожие результаты, если контента мало.
- DateProvider в KMP может потребовать аккуратного expect/actual решения.
- Существующее settings storage может стать перегруженным, если туда добавить слишком много home state.
- Длинные русские тексты могут ломать карточку на маленьких экранах.
- Категории могут быть неочевидны, если пользователь ожидает мгновенной смены daily prediction при тапе.

## 16. Рекомендуемое техническое решение MVP

Самый прагматичный порядок:

1. Domain-модели.
2. Kotlin-based local catalog на 50 предсказаний.
3. Repository interface + local implementation.
4. DateProvider.
5. Daily/manual generation use cases.
6. Limit storage/use cases.
7. HomeViewModel.
8. UI components.
9. Подключение вместо placeholder.
10. Tests.

JSON, SQLDelight, history и real sharing не тащить в первый проход Home MVP.
