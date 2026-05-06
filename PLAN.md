# АНТИГОРОСКОП — Полный технический и продуктовый план

---

## 1. Структура продукта — Экраны приложения

### 1.1 Онбординг (`OnboardingScreen`)
- **Слайд 1** — Приветствие: "Добро пожаловать в единственный честный гороскоп" + анимированный космос
- **Слайд 2** — Объяснение концепции: "Мы не знаем будущего. Но звёзды точно знают, что тебе сегодня нельзя солёное"
- **Слайд 3** — Выбор знака зодиака (встроен прямо в онбординг)
- **Слайд 4** — Разрешение на уведомления + выбор времени
- Кнопка "Пропустить" на каждом слайде (кроме выбора знака — обязательный шаг)
- Индикатор прогресса (точки внизу)

### 1.2 Выбор знака зодиака (`ZodiacSelectionScreen`)
- Сетка 4×3 с иконками знаков зодиака
- Каждая карточка: иконка + название + даты
- Тап — выделение + лёгкая анимация свечения
- Возможность смены знака из настроек
- Иконки — кастомные, в нео-космическом стиле

### 1.3 Главный экран (`HomeScreen`)
- Хедер: знак зодиака текущего пользователя + дата
- **Главная карточка** — дневное антипредсказание (большая, glowing, с анимацией появления)
- Категории предсказаний: Любовь / Деньги / Карьера / Здоровье / Хаос
- Кнопка "Перегенерировать" (с лимитом в MVP)
- Кнопка "Поделиться"
- Нижняя навигация: Главная / Категории / Лента / Профиль

**Статус Home MVP на 2026-05-06:** реализован первый usable Home без bottom navigation: дневное предсказание, категории, лимит 3 manual refresh в день, локальный каталог 100 предсказаний, MVP share feedback, analytics events и verification. Подробный статус: `docs/HOME_TASKS.md`; visual QA: `docs/HOME_VISUAL_QA.md`.

### 1.4 Детальный экран предсказания (`PredictionDetailScreen`)
- Полный текст предсказания
- Иконка категории + цвет категории
- Анимированный фон (космические частицы)
- Мета-информация: знак зодиака, дата, уровень абсурдности
- Кнопки: Поделиться / Сохранить в избранное / Следующее
- "Научное обоснование" — рандомная псевдонаучная фраза мелким шрифтом

### 1.5 Генератор предсказаний (`PredictionGeneratorScreen`)
- Слайдер уровня абсурдности (от "почти нормально" до "полный космос")
- Выбор категории
- Выбор знака (не только свой)
- Кнопка "Сгенерировать" с анимацией звёздного вихря
- Результат — карточка с предсказанием
- Лимит генераций в день (3 для free, ∞ для premium)

### 1.6 Лента сообщества (`CommunityFeedScreen`)
- Список карточек с предсказаниями других пользователей
- Фильтры: По знаку / По категории / Самые смешные
- Лайки (реакции: 😂 🤯 🔮 💀)
- Кнопка поделиться чужим предсказанием
- В MVP — только локальный контент (предсказания из базы), без реального UGC

### 1.7 Профиль (`ProfileScreen`)
- Аватар (генерируется на основе знака зодиака — астро-арт)
- Имя / никнейм
- Знак зодиака + его описание (абсурдное)
- Статистика: прочитано предсказаний / дней подряд / поделился N раз
- Избранные предсказания
- История предсказаний (последние 30 дней)

### 1.8 Настройки (`SettingsScreen`)
- Смена знака зодиака
- Настройка уведомлений (время, вкл/выкл)
- Тема оформления (cosmic dark / midnight purple / neon chaos)
- Уровень абсурдности по умолчанию
- Язык
- Поддержка / Обратная связь
- О приложении
- Удалить данные / аккаунт

---

## 2. MVP-скоуп

### Входит в MVP v1.0
- Онбординг (3 экрана + выбор знака)
- Экран выбора знака зодиака
- Главный экран с дневным предсказанием
- Детальный экран предсказания
- Категории (5 штук)
- Профиль (базовый: знак, статистика, история)
- Настройки (знак, уведомления, тема)
- Локальные уведомления
- Шаринг (текст + карточка-изображение)
- Локальный банк предсказаний (≥200 штук)
- Хранение истории локально (SQLDelight)
- Базовая аналитика (Firebase)
- Лимит генераций (3 в день для free)

### Откладывается на после MVP
- Лента сообщества / UGC
- Генератор предсказаний (экран)
- Монетизация / Premium
- Кастомные темы (кроме одной базовой)
- Backend / Remote config для предсказаний
- Авторизация пользователей
- Социальные функции (лайки, комментарии)
- Виджет для домашнего экрана
- Реальный platform share sheet и image share card для текущего Home MVP
- SQLDelight history/cache для дневных предсказаний

---

## 3. User Flow

```
Первый запуск
│
├─► Онбординг Слайд 1 → Слайд 2
│       └─► Выбор знака зодиака [ОБЯЗАТЕЛЬНО]
│               └─► Запрос разрешения на уведомления
│                       └─► Главный экран
│
Ежедневное использование
│
├─► Пуш-уведомление (утром) → тап → Главный экран
│
├─► Главный экран
│   ├─► Просмотр дневного предсказания
│   ├─► Тап на карточку → Детальный экран
│   │       ├─► Поделиться (текст / карточка)
│   │       └─► Сохранить в избранное
│   ├─► Выбор категории → карточка категории
│   └─► Кнопка "Другое предсказание" (лимит 3/день)
│
├─► Профиль
│   ├─► История предсказаний
│   └─► Избранное
│
└─► Настройки → смена знака / уведомления
```

**Retention-петля:**
- Ежедневное уведомление → открытие → новое предсказание → шаринг → возврат завтра

---

## 4. Архитектура

### 4.1 Общая структура модулей

```
:app:android          ← Android-specific entry point
:app:ios              ← iOS-specific entry point (Kotlin/Swift bridge)
:shared               ← Общий Kotlin Multiplatform модуль
  :shared:data        ← Data layer (репозитории, sources, DTO)
  :shared:domain      ← Domain layer (use cases, entities, interfaces)
  :shared:presentation← Presentation layer (ViewModels, StateFlow)
  :shared:database    ← SQLDelight схема и generated queries
:ui                   ← Compose Multiplatform UI компоненты
:features
  :features:onboarding
  :features:home
  :features:prediction
  :features:profile
  :features:settings
  :features:community
:core
  :core:analytics
  :core:notifications
  :core:sharing
```

### 4.2 Data Layer

- **LocalDataSource** — SQLDelight + Multiplatform Settings (KMP-преференсы)
- **RemoteDataSource** — Ktor HTTP клиент (для будущего backend)
- **PredictionRepository** — единая точка доступа к предсказаниям
- **UserRepository** — профиль пользователя, настройки
- **DTO ↔ Entity mapping** — отдельные mapper-классы

### 4.3 Domain Layer

- **Use Cases** (один класс — одна операция):
  - `GetDailyPredictionUseCase`
  - `GeneratePredictionUseCase`
  - `SaveFavoritePredictionUseCase`
  - `GetPredictionHistoryUseCase`
  - `UpdateZodiacSignUseCase`
  - `GetUserProfileUseCase`
  - `ScheduleNotificationUseCase`
- **Entities** — чистые Kotlin data classes без Android/iOS зависимостей
- **Repository interfaces** — контракты, реализуемые в data layer

### 4.4 Presentation Layer

- **ViewModel** на базе `kotlinx.coroutines` + `StateFlow`
- Паттерн **MVI**: `Intent → ViewModel → State → UI`
- Каждый экран: `Screen.kt` (Composable) + `ScreenViewModel.kt` + `ScreenState.kt` + `ScreenIntent.kt`
- ViewModels создаются через DI, передаются в Composable через параметры

### 4.5 Dependency Injection

- **Koin** (лучшая поддержка KMP из коробки)
- Модули: `dataModule`, `domainModule`, `presentationModule`, `platformModule`
- `platformModule` — platform-specific провайдеры (контекст Android, iOS-специфика)
- Инициализация в `Application` (Android) и `main` (iOS)

### 4.6 Navigation

- **Voyager** (Compose Multiplatform navigation) или **Decompose**
- Рекомендация: **Decompose** — лучше поддерживает back stack на iOS, глубокие ссылки
- Navigation граф определяется в shared модуле
- Deep links: `antihoroscope://prediction/{id}`, `antihoroscope://zodiac`

---

## 5. Compose Multiplatform UI — Компоненты

### `CosmicBackground`
- Анимированный градиентный фон (deep purple → black)
- Particle system — звёзды/частицы (Canvas API)
- `Modifier.cosmicBackground()` — extension для любого экрана

### `GlowingPredictionCard`
- Параметры: `prediction: Prediction`, `onClick`, `onShare`
- Rounded corners (24dp), glassmorphism эффект
- Glow border — градиентная обводка с `BlurMaskFilter`
- Анимация появления: `AnimatedVisibility` + scale + fade
- Внутри: иконка категории, текст предсказания, footer с датой и знаком

### `ZodiacSelector`
- LazyVerticalGrid 4×3
- `ZodiacCard` — иконка + название + hover-эффект свечения
- Состояние выбора — `selectedSign: ZodiacSign?`
- Анимация выбора: нео-пульс вокруг карточки

### `BottomNavigation`
- 4 таба: Главная / Категории / Лента / Профиль
- Иконки кастомные (SVG → ImageVector)
- Активная вкладка — neon-подсветка снизу
- `NavigationBar` из Material3, кастомный цвет

### `AbsurditySlider`
- Кастомный `Slider` с метками: "Скучно" → "Нормально" → "Космос" → "Полный хаос"
- Thumb-иконка меняется с уровнем (🌙 → 🔮 → 🌌 → 🤯)
- Gradient track

### `PredictionCategoryCard`
- Горизонтальный список (LazyRow) или сетка
- Каждая карточка: иконка + название категории + цвет-акцент
- Категории: Любовь ❤️ / Деньги 💸 / Карьера 💼 / Здоровье 🌿 / Хаос 🌀

### `CosmicButton`
- Primary: gradient fill (purple → pink)
- Secondary: outline с glow
- Loading state — анимированные звёздочки вместо spinner
- `onClick` с debounce (защита от двойного тапа)

### `ZodiacAvatarWidget`
- Кружок с иконкой знака + ореол (цвет зависит от знака)
- Используется в профиле и хедере главного экрана

### `PredictionHistoryItem`
- Компактная строка: иконка категории + текст (truncated) + дата
- Swipe-to-favorite жест

### `ShareCard` (офлайн-рендеринг для шаринга)
- Статичный Composable, рендерится в Bitmap
- Лого + знак + предсказание + дата + watermark "Антигороскоп"

---

## 6. Модель данных

```kotlin
// Знак зодиака
data class ZodiacSign(
    val id: String,           // "aries", "taurus", ...
    val nameRu: String,       // "Овен"
    val dateRange: String,    // "21 марта — 19 апреля"
    val iconResId: String,    // ресурс иконки
    val accentColor: Long,    // hex color
    val absurdDescription: String // "Известны тем, что едят суп вилкой"
)

// Предсказание
data class Prediction(
    val id: String,
    val text: String,
    val category: PredictionCategory,
    val absurdityLevel: Int,  // 1–5
    val zodiacSign: ZodiacSign?,  // null = для всех знаков
    val tags: List<String>
)

// Категория предсказания
enum class PredictionCategory(
    val nameRu: String,
    val emoji: String,
    val colorHex: Long
) {
    LOVE("Любовь", "❤️", 0xFFE91E63),
    MONEY("Деньги", "💸", 0xFF4CAF50),
    CAREER("Карьера", "💼", 0xFF2196F3),
    HEALTH("Здоровье", "🌿", 0xFF8BC34A),
    CHAOS("Хаос", "🌀", 0xFF9C27B0)
}

// Дневное предсказание (кешированное)
data class DailyPrediction(
    val date: LocalDate,
    val zodiacSign: ZodiacSign,
    val prediction: Prediction,
    val generatedAt: Instant,
    val isFavorite: Boolean = false
)

// История предсказаний
data class PredictionHistory(
    val id: Long,
    val predictionId: String,
    val zodiacSignId: String,
    val date: LocalDate,
    val category: PredictionCategory,
    val predictionText: String,
    val isFavorite: Boolean,
    val sharedCount: Int
)

// Профиль пользователя
data class UserProfile(
    val zodiacSign: ZodiacSign,
    val nickname: String?,
    val totalPredictionsViewed: Int,
    val streakDays: Int,
    val totalShared: Int,
    val joinedDate: LocalDate,
    val isPremium: Boolean
)

// Настройки
data class AppSettings(
    val notificationsEnabled: Boolean,
    val notificationTime: LocalTime,  // HH:MM
    val defaultAbsurdityLevel: Int,   // 1–5
    val selectedTheme: AppTheme,
    val language: String,             // "ru", "en"
    val dailyGenerationCount: Int,    // счётчик генераций сегодня
    val lastGenerationDate: LocalDate?
)
```

---

## 7. Логика генерации предсказаний

### 7.1 Детерминированная дневная генерация

- **Seed = hash(date.toString() + zodiacSign.id)**
- `Random(seed)` гарантирует одно и то же предсказание для конкретного знака на конкретную дату
- Предсказание не меняется в течение дня при перезапуске приложения
- Кешируется в БД после первой генерации

```kotlin
fun generateDailyPrediction(date: LocalDate, sign: ZodiacSign): Prediction {
    val seed = (date.toString() + sign.id).hashCode().toLong()
    val rng = Random(seed)
    val pool = localPredictions.filter {
        it.zodiacSign == null || it.zodiacSign.id == sign.id
    }
    return pool[rng.nextInt(pool.size)]
}
```

### 7.2 Банк предсказаний

- **Минимум 200 предсказаний** в локальном JSON-файле в `shared/resources`
- Структура: массив объектов `{id, text, category, absurdityLevel, tags, zodiacSign?}`
- Около 40 предсказаний на категорию
- Часть предсказаний — знако-специфичные (пример: "Овны сегодня не должны открывать холодильник больше двух раз")
- Уровни абсурдности 1–5:
  - 1: "Сегодня лучше не торопиться"
  - 3: "Нельзя солёное до заката"
  - 5: "Если встретишь кошку — она что-то знает"

### 7.3 Категории и распределение
- LOVE — 20%
- MONEY — 20%
- CAREER — 20%
- HEALTH — 20%
- CHAOS — 20% (самые абсурдные)

### 7.4 Фильтрация по уровню абсурдности
- Пользователь выбирает диапазон (настройки / генератор)
- При генерации — фильтрация пула по `absurdityLevel`

### 7.5 Лимит ручной перегенерации
- **3 раза в день** для free-пользователей
- Счётчик сбрасывается в полночь
- Хранится в `AppSettings.dailyGenerationCount` + `lastGenerationDate`
- После лимита — показывается мягкий paywall "Разблокировать ∞ генераций"

### 7.6 Будущая поддержка Remote Config
- Интерфейс `PredictionRemoteSource` с методом `fetchPredictions()`
- В MVP — `LocalPredictionSource` (заглушка)
- Позже — Firebase Remote Config или собственный API
- Инвалидация кеша по TTL (1 день)

---

## 8. Локальное хранилище

### 8.1 SQLDelight — структура таблиц

```sql
-- Профиль
CREATE TABLE user_profile (
    zodiac_sign_id TEXT NOT NULL,
    nickname TEXT,
    joined_date TEXT NOT NULL,
    is_premium INTEGER NOT NULL DEFAULT 0
);

-- История предсказаний
CREATE TABLE prediction_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    prediction_id TEXT NOT NULL,
    zodiac_sign_id TEXT NOT NULL,
    date TEXT NOT NULL,
    category TEXT NOT NULL,
    prediction_text TEXT NOT NULL,
    is_favorite INTEGER NOT NULL DEFAULT 0,
    shared_count INTEGER NOT NULL DEFAULT 0
);

-- Дневное кешированное предсказание
CREATE TABLE daily_prediction_cache (
    date TEXT NOT NULL,
    zodiac_sign_id TEXT NOT NULL,
    prediction_id TEXT NOT NULL,
    prediction_text TEXT NOT NULL,
    category TEXT NOT NULL,
    PRIMARY KEY (date, zodiac_sign_id)
);

-- Счётчики генерации
CREATE TABLE generation_limits (
    date TEXT PRIMARY KEY,
    count INTEGER NOT NULL DEFAULT 0
);
```

### 8.2 Multiplatform Settings (Настройки KV)

Используем `com.russhwolf:multiplatform-settings`:
- `zodiac_sign_id` — выбранный знак
- `notifications_enabled` — bool
- `notification_time` — "HH:MM" строка
- `default_absurdity_level` — int 1–5
- `selected_theme` — string
- `onboarding_completed` — bool

### 8.3 Политика хранения истории
- Хранить последние **365 записей**
- Автоочистка — удалять записи старше года при старте
- Избранное — без ограничений (пока ≤100 записей)

---

## 9. Уведомления

### 9.1 Примеры текстов уведомлений

- "🔮 Звёзды готовы. Твоё предсказание на сегодня ждёт тебя."
- "🌙 Не узнаешь — не предупредим. Загляни в антигороскоп."
- "⚠️ Срочное космическое сообщение. Серьёзно."
- "🪐 Меркурий снова в ретрограде. Или нет. Проверь."
- "💀 Сегодня нельзя игнорировать предсказание. Это важно."

### 9.2 Функциональность
- Пользователь выбирает время (дефолт: 09:00)
- Одно уведомление в день
- Тап по уведомлению → открытие HomeScreen с автоматическим показом предсказания
- Deep link: `antihoroscope://daily`

### 9.3 Android
- `WorkManager` с `PeriodicWorkRequest` (24h interval)
- Exact alarm для Android 12+ (требует `SCHEDULE_EXACT_ALARM`)
- Notification channel: "Космические предсказания"

### 9.4 iOS
- `UNUserNotificationCenter` через `expect/actual`
- `UNCalendarNotificationTrigger` с компонентами времени
- Перепланирование при изменении времени в настройках
- `expect class NotificationScheduler` в shared, `actual` в каждой платформе

---

## 10. Шаринг

### 10.1 Текстовый шаринг
- Шаблон: `"[Знак зодиака], [дата]\n\n[Текст предсказания]\n\n📲 Антигороскоп — скачай и узнай своё не-судьбу"`
- `expect fun shareText(text: String)`

### 10.2 Карточка-изображение
- Рендеринг `ShareCard` Composable в `Bitmap` (через Canvas)
- На Android: `View.drawToBitmap()` или offscreen Canvas
- На iOS: `UIGraphicsImageRenderer`
- Размер: 1080×1080px (квадрат для Instagram/Telegram)
- Дизайн: космический фон + знак + предсказание + логотип

### 10.3 Platform Share Sheet
- `expect fun shareImage(bitmap: ImageBitmap, text: String)`
- Android: `Intent.ACTION_SEND` с `FileProvider` (сохраняем во временный файл)
- iOS: `UIActivityViewController`

---

## 11. Монетизация

### 11.1 Модель — Freemium без агрессии

**Free:**
- Дневное предсказание — всегда бесплатно
- 3 ручные генерации в день
- 1 тема оформления
- Реклама (баннер внизу экрана — только на HomeScreen, не на карточке)

**Premium (разовая покупка или подписка $1.99/мес):**
- Безлимитные генерации
- Все темы оформления (3 дополнительных)
- Убрать рекламу
- Эксклюзивные пакеты предсказаний (100+ "особо точных")
- Ранний доступ к новым категориям

### 11.2 Реклама
- **Android**: Google AdMob (баннер 320×50 внизу HomeScreen)
- **iOS**: AdMob или Apple Ads
- Показывается только через 3 дня после установки (grace period)
- Premium убирает рекламу мгновенно

### 11.3 Платёжная система
- Android: Google Play Billing (через `billing-ktx`)
- iOS: StoreKit 2
- Абстракция: `PurchaseManager` с `expect/actual`

---

## 12. Аналитика

### 12.1 Инструмент
- **Firebase Analytics** (бесплатно, хорошая KMP поддержка через wrapper)
- Абстракция `AnalyticsTracker` interface в domain, платформенная реализация

### 12.2 События

| Событие | Параметры |
|---|---|
| `onboarding_started` | — |
| `onboarding_completed` | `steps_completed: Int` |
| `onboarding_skipped` | `at_step: Int` |
| `zodiac_selected` | `zodiac_sign: String` |
| `zodiac_changed` | `from: String, to: String` |
| `prediction_viewed` | `category: String, zodiac: String, absurdity: Int` |
| `prediction_favorited` | `category: String` |
| `prediction_refreshed` | `generation_count: Int` |
| `prediction_shared` | `method: "text"/"image", category: String` |
| `category_selected` | `category: String` |
| `notification_opened` | `time_since_scheduled: Long` |
| `notification_permission_granted` | — |
| `notification_permission_denied` | — |
| `premium_clicked` | `trigger_point: String` |
| `premium_purchased` | `product_id: String` |
| `settings_changed` | `setting: String, value: String` |
| `app_opened` | `source: "direct"/"notification"/"share"` |

### 12.3 User Properties
- `zodiac_sign`
- `is_premium`
- `streak_days`
- `days_since_install`

---

## 13. План тестирования

### 13.1 Unit тесты (shared модуль)

- `PredictionGeneratorTest` — детерминированность (один seed = одно предсказание)
- `DailyPredictionSeedTest` — разные даты дают разные предсказания
- `GenerationLimitTest` — счётчик сбрасывается корректно
- `ZodiacSignMapperTest` — DTO ↔ Entity mapping
- `AppSettingsTest` — сериализация/десериализация настроек
- `PredictionRepositoryTest` — моки data sources, проверка логики

### 13.2 Snapshot / UI тесты (Compose)

- `GlowingPredictionCardTest` — скриншот при разных состояниях
- `ZodiacSelectorTest` — выбор знака, состояние выделения
- `HomeScreenTest` — загрузка, отображение предсказания, кнопки
- `OnboardingFlowTest` — переход по слайдам
- Инструмент: **Paparazzi** (Android), **swift-snapshot-testing** (iOS)

### 13.3 Тесты генератора предсказаний

- Стабильность: 1000 запусков одного seed → всегда один результат
- Равномерность: статистический тест распределения по категориям
- Граничные случаи: пустой пул, пул с одним элементом
- Фильтрация по absurdityLevel работает корректно

### 13.4 Тесты локального хранилища

- SQLDelight: CRUD операции для каждой таблицы
- История: лимит 365 записей, автоочистка
- Миграции БД (при обновлении схемы)
- Multiplatform Settings: сохранение и чтение всех настроек

### 13.5 Интеграционные тесты (платформенные)

- **Android**: Espresso или Compose UI Test
  - Полный онбординг flow
  - Шаринг (Intent проверка)
  - Уведомления (WorkManager test)
- **iOS**: XCTest + XCUITest
  - Базовый UI flow
  - Уведомления (UNUserNotificationCenter mock)

---

## 14. Дорожная карта разработки

### Phase 1 — Foundation (2 недели)
- Настройка KMP проекта (Gradle, targets, CI)
- Базовая архитектура: shared модуль, DI (Koin)
- SQLDelight схема + migrations
- Multiplatform Settings
- Локальный банк предсказаний (JSON → 200+ записей)
- Базовая навигация (Decompose)
- Дизайн-система: цвета, типографика, иконки

### Phase 2 — MVP Core (3 недели)
- Экран онбординга
- Экран выбора знака зодиака
- Главный экран с дневным предсказанием
- Детальный экран предсказания
- Use Cases: GetDailyPrediction, GeneratePrediction
- Кеширование предсказаний в SQLDelight
- Профиль (базовый)
- Настройки (знак, уведомления)

### Phase 3 — Polish (2 недели)
- Анимации (появление карточек, cosmic background, частицы)
- Уведомления (Android WorkManager + iOS UNUserNotificationCenter)
- Шаринг (текст + изображение-карточка)
- Базовая аналитика (Firebase)
- История предсказаний
- Избранное
- Лимит генераций (3/день)

### Phase 4 — Release Prep (1 неделя)
- QA: тестирование на реальных устройствах
- App Store / Google Play материалы (скриншоты, описание)
- Privacy Policy / Terms
- Краш-репортинг (Firebase Crashlytics)
- Финальные правки по UX
- Soft launch (TestFlight + Internal Testing)

### Phase 5 — Post-Release (ongoing)
- Монетизация (AdMob + in-app purchases)
- Генератор предсказаний (экран)
- Дополнительные темы оформления
- Лента сообщества (read-only)
- Remote Config предсказаний
- Виджет для домашнего экрана
- Расширение банка предсказаний

---

## 15. Бэклог

### Epic 1: Онбординг
- **US-001**: Как новый пользователь, хочу пройти онбординг и выбрать знак зодиака
  - Задача: Создать OnboardingScreen с 3 слайдами
  - Задача: Создать ZodiacSelectionScreen
  - Задача: Сохранить знак в настройки
  - Задача: Запросить разрешение на уведомления

### Epic 2: Дневное предсказание
- **US-002**: Как пользователь, хочу каждый день видеть новое абсурдное предсказание
  - Задача: Реализовать GeneratePredictionUseCase с seed
  - Задача: Создать локальный JSON-банк предсказаний
  - Задача: Создать GlowingPredictionCard компонент
  - Задача: Реализовать кеширование в SQLDelight
- **US-003**: Хочу перегенерировать предсказание (с лимитом)
  - Задача: Счётчик генераций + сброс в полночь
  - Задача: UI-блокировка после 3 раз

### Epic 3: Детали предсказания
- **US-004**: Хочу видеть полное предсказание с деталями
  - Задача: PredictionDetailScreen
  - Задача: Анимированный фон на детальном экране
  - Задача: "Научное обоснование" — рандомная псевдонаучная фраза

### Epic 4: Шаринг
- **US-005**: Хочу поделиться предсказанием с друзьями
  - Задача: expect/actual shareText
  - Задача: Рендеринг ShareCard в Bitmap
  - Задача: expect/actual shareImage

### Epic 5: Уведомления
- **US-006**: Хочу получать ежедневное уведомление с предсказанием
  - Задача: NotificationScheduler (Android WorkManager)
  - Задача: NotificationScheduler (iOS UNUserNotificationCenter)
  - Задача: Выбор времени уведомления в настройках
  - Задача: Deep link из уведомления

### Epic 6: Профиль и история
- **US-007**: Хочу видеть историю своих предсказаний
  - Задача: Запись в prediction_history при просмотре
  - Задача: ProfileScreen с историей
  - Задача: Избранное (toggle + список)

### Epic 7: Настройки
- **US-008**: Хочу настроить приложение под себя
  - Задача: SettingsScreen
  - Задача: Смена знака зодиака
  - Задача: Настройка уведомлений

### Epic 8: Аналитика и краши
- **US-009**: (Технический) Нужно знать как пользователи используют приложение
  - Задача: Firebase Analytics интеграция
  - Задача: Логирование всех ключевых событий
  - Задача: Firebase Crashlytics

---

## 16. Риски

### Продуктовые риски

| Риск | Вероятность | Влияние | Митигация |
|---|---|---|---|
| Предсказания становятся скучными при повторном использовании | Высокая | Высокое | Банк ≥200 записей, сезонные обновления, remote config |
| Шутки обидны для части аудитории | Средняя | Среднее | Модерация при написании, избегать личных/политических тем |
| Низкий retention без реального контента | Средняя | Высокое | Стрик механика, уведомления, шаринг для виральности |
| Конкуренция с реальными гороскоп-приложениями | Низкая | Низкое | Чёткое позиционирование как пародия |

### Технические риски

| Риск | Вероятность | Влияние | Митигация |
|---|---|---|---|
| Compose Multiplatform нестабилен на iOS | Средняя | Высокое | Тестировать на реальных устройствах с Phase 1; иметь план fallback на SwiftUI для критичных экранов |
| Производительность particle system / анимаций на старых устройствах | Средняя | Среднее | Configureable quality: отключать частицы при низком FPS |
| iOS: строгие правила App Store по контенту | Средняя | Высокое | Ознакомиться с гайдлайнами заранее; избегать сексуального/алкогольного контента в тексте |
| Разрешения на уведомления отклоняются | Высокая | Среднее | Объяснить ценность на онбординге; работать без уведомлений корректно |
| SQLDelight миграции при обновлении схемы | Низкая | Высокое | Версионировать схему с первого дня, тестировать миграции |
| Рендеринг ShareCard в Bitmap — сложность на iOS | Средняя | Среднее | Прототип в Phase 1, не в Phase 3 |

---

## 17. Deliverables

### 17.1 Структура проекта

```
antihoroscope/
├── androidApp/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── kotlin/
│   │   │   └── com/antihoroscope/android/
│   │   │       ├── MainActivity.kt
│   │   │       ├── AntiHoroscopeApp.kt
│   │   │       └── notifications/
│   │   │           └── NotificationWorker.kt
│   │   └── res/
│   └── build.gradle.kts
│
├── iosApp/
│   ├── iosApp/
│   │   ├── ContentView.swift
│   │   ├── iOSApp.swift
│   │   └── notifications/
│   │       └── NotificationDelegate.swift
│   └── iosApp.xcodeproj/
│
├── shared/
│   ├── src/
│   │   ├── commonMain/kotlin/com/antihoroscope/shared/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── DatabaseDriverFactory.kt  (expect)
│   │   │   │   │   ├── PredictionLocalSource.kt
│   │   │   │   │   └── SettingsStorage.kt
│   │   │   │   ├── remote/
│   │   │   │   │   └── PredictionRemoteSource.kt
│   │   │   │   ├── repository/
│   │   │   │   │   ├── PredictionRepositoryImpl.kt
│   │   │   │   │   └── UserRepositoryImpl.kt
│   │   │   │   └── model/
│   │   │   │       └── PredictionDto.kt
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Prediction.kt
│   │   │   │   │   ├── ZodiacSign.kt
│   │   │   │   │   ├── DailyPrediction.kt
│   │   │   │   │   ├── UserProfile.kt
│   │   │   │   │   └── AppSettings.kt
│   │   │   │   ├── repository/
│   │   │   │   │   ├── PredictionRepository.kt
│   │   │   │   │   └── UserRepository.kt
│   │   │   │   └── usecase/
│   │   │   │       ├── GetDailyPredictionUseCase.kt
│   │   │   │       ├── GeneratePredictionUseCase.kt
│   │   │   │       ├── GetPredictionHistoryUseCase.kt
│   │   │   │       ├── SaveFavoriteUseCase.kt
│   │   │   │       ├── UpdateZodiacSignUseCase.kt
│   │   │   │       └── ScheduleNotificationUseCase.kt
│   │   │   ├── presentation/
│   │   │   │   ├── home/
│   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   ├── HomeState.kt
│   │   │   │   │   └── HomeIntent.kt
│   │   │   │   ├── onboarding/
│   │   │   │   ├── prediction/
│   │   │   │   ├── profile/
│   │   │   │   └── settings/
│   │   │   ├── platform/
│   │   │   │   ├── NotificationScheduler.kt  (expect)
│   │   │   │   ├── ShareManager.kt           (expect)
│   │   │   │   └── AnalyticsTracker.kt       (expect)
│   │   │   └── di/
│   │   │       ├── DataModule.kt
│   │   │       ├── DomainModule.kt
│   │   │       └── PresentationModule.kt
│   │   ├── androidMain/kotlin/com/antihoroscope/shared/
│   │   │   ├── data/local/DatabaseDriverFactory.kt  (actual)
│   │   │   ├── platform/NotificationScheduler.kt    (actual)
│   │   │   └── platform/ShareManager.kt             (actual)
│   │   └── iosMain/kotlin/com/antihoroscope/shared/
│   │       ├── data/local/DatabaseDriverFactory.kt  (actual)
│   │       ├── platform/NotificationScheduler.kt    (actual)
│   │       └── platform/ShareManager.kt             (actual)
│   ├── sqldelight/
│   │   └── com/antihoroscope/shared/
│   │       ├── UserProfile.sq
│   │       ├── PredictionHistory.sq
│   │       ├── DailyPredictionCache.sq
│   │       └── GenerationLimits.sq
│   └── resources/
│       └── predictions.json
│
├── composeApp/
│   └── src/commonMain/kotlin/com/antihoroscope/ui/
│       ├── components/
│       │   ├── CosmicBackground.kt
│       │   ├── GlowingPredictionCard.kt
│       │   ├── ZodiacSelector.kt
│       │   ├── BottomNavigation.kt
│       │   ├── AbsurditySlider.kt
│       │   ├── CosmicButton.kt
│       │   ├── ShareCard.kt
│       │   └── PredictionCategoryCard.kt
│       ├── screens/
│       │   ├── OnboardingScreen.kt
│       │   ├── ZodiacSelectionScreen.kt
│       │   ├── HomeScreen.kt
│       │   ├── PredictionDetailScreen.kt
│       │   ├── ProfileScreen.kt
│       │   └── SettingsScreen.kt
│       ├── theme/
│       │   ├── Color.kt
│       │   ├── Typography.kt
│       │   ├── Theme.kt
│       │   └── Shapes.kt
│       └── navigation/
│           └── AppNavigation.kt
│
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

### 17.2 Рекомендуемые библиотеки

| Назначение | Библиотека | Версия |
|---|---|---|
| UI Framework | Compose Multiplatform | 1.7.x |
| Navigation | Decompose | 3.x |
| DI | Koin | 4.x |
| База данных | SQLDelight | 2.x |
| KV хранилище | multiplatform-settings | 1.x |
| HTTP клиент | Ktor | 3.x |
| Сериализация | kotlinx.serialization | 1.7.x |
| Coroutines | kotlinx.coroutines | 1.9.x |
| Дата/время | kotlinx-datetime | 0.6.x |
| Изображения | Coil (Compose) | 3.x |
| Аналитика | Firebase (KMM wrapper) | — |
| Краш-репорты | Firebase Crashlytics | — |
| Реклама | Google AdMob | — |
| Покупки Android | billing-ktx | 7.x |
| Тесты UI | Paparazzi (Android) | — |
| Логирование | Napier (KMP logger) | 2.x |

### 17.3 Первые 20 задач в порядке приоритета

| # | Задача | Оценка |
|---|---|---|
| 1 | Создать KMP проект, настроить Gradle (android + ios targets) | 1д |
| 2 | Добавить Koin DI, базовые модули | 0.5д |
| 3 | Настроить SQLDelight + схема (4 таблицы) | 1д |
| 4 | Настроить multiplatform-settings | 0.5д |
| 5 | Написать 200+ предсказаний в JSON (predictions.json) | 2д |
| 6 | Создать domain entities (ZodiacSign, Prediction, DailyPrediction, UserProfile, AppSettings) | 0.5д |
| 7 | Реализовать GeneratePredictionUseCase (seed-логика) + тесты | 1д |
| 8 | Реализовать PredictionLocalSource (JSON reader + SQLDelight CRUD) | 1д |
| 9 | Реализовать PredictionRepository + UserRepository | 1д |
| 10 | Настроить Compose Multiplatform + Theme (Color, Typography, Shapes) | 1д |
| 11 | Компонент CosmicBackground (градиент + частицы на Canvas) | 1.5д |
| 12 | Компонент GlowingPredictionCard | 1.5д |
| 13 | Компонент ZodiacSelector (LazyVerticalGrid + анимация) | 1д |
| 14 | Настроить Decompose навигацию (граф: Onboarding → Home → Detail → Profile → Settings) | 1д |
| 15 | Экран OnboardingScreen (3 слайда) | 1д |
| 16 | Экран ZodiacSelectionScreen + подключение к UpdateZodiacSignUseCase | 1д |
| 17 | Экран HomeScreen (хедер + карточка + категории + кнопка refresh) | 2д |
| 18 | ViewModel для HomeScreen + GetDailyPredictionUseCase | 1д |
| 19 | Экран PredictionDetailScreen + анимации | 1.5д |
| 20 | Настроить CI (GitHub Actions: build Android APK + iOS framework) | 1д |

**Итого первых 20 задач: ~20 рабочих дней (≈4 недели)**

### 17.4 Definition of Done для MVP

**Функциональные критерии:**
- [ ] Пользователь проходит онбординг и выбирает знак зодиака
- [ ] Главный экран показывает дневное предсказание (стабильное в течение дня)
- [ ] Предсказание меняется каждый день (новый seed)
- [ ] Работает кнопка перегенерации (лимит 3 раза в день)
- [ ] Детальный экран открывается по тапу на карточку
- [ ] Шаринг работает (текст + карточка) на Android и iOS
- [ ] Уведомления отправляются в выбранное пользователем время
- [ ] История предсказаний сохраняется и отображается
- [ ] Смена знака зодиака работает через настройки
- [ ] Приложение работает офлайн (все предсказания локальные)

**Технические критерии:**
- [ ] Нет крешей при стандартных сценариях использования (0 в Crashlytics за 3 дня тестирования)
- [ ] Время запуска < 2 сек на mid-range устройствах
- [ ] Приложение работает на Android API 26+ и iOS 16+
- [ ] Unit тесты покрывают GeneratePredictionUseCase и репозитории
- [ ] Аналитика: все 12 ключевых событий логируются корректно
- [ ] Приложение прошло ревью App Store Connect и Google Play Console
- [ ] Privacy Policy опубликована и указана в сторах
- [ ] Нет утечек памяти (проверено Profiler)

**Продуктовые критерии:**
- [ ] Банк предсказаний: минимум 200 уникальных записей
- [ ] Дизайн соответствует dark-cosmic концепции
- [ ] Все UI-анимации плавные (60fps)
- [ ] Скриншоты для стора подготовлены (5 шт. Android + 5 iOS)
- [ ] ASO: название, описание, ключевые слова заполнены
