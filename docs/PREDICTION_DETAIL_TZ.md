# ТЗ: Epic 3 — Prediction Detail MVP

Документ описывает следующий продуктовый блок после Home: детальный экран предсказания, который открывается по тапу на карточку текущего предсказания на Home.

Связанный task breakdown: `docs/PREDICTION_DETAIL_TASKS.md`.

## 1. Цель блока

Дать пользователю второй полноценный экран после Home, где предсказание можно прочитать целиком, увидеть его метаданные и выполнить базовые действия.

Prediction Detail MVP должен ощущаться как естественное продолжение Home:

- Home показывает компактную карточку дня.
- Detail раскрывает тот же прогноз в более спокойном и читаемом формате.
- Пользователь может вернуться назад без потери состояния Home.
- Действия `Поделиться` и `В избранное` дают понятный MVP feedback, даже если полноценная реализация отложена.

## 2. Scope MVP

### 2.1 Входит

- `PredictionDetailScreen` как отдельный экран в текущем `composeApp`.
- Переход из Home по тапу на карточку предсказания.
- Полный текст выбранного предсказания.
- Отображение категории предсказания.
- Отображение знака зодиака.
- Отображение даты.
- Отображение `absurdity level`.
- Псевдонаучная подпись или объяснение в стиле продукта.
- MVP feedback для `Поделиться`.
- MVP feedback для `В избранное`.
- Analytics events для просмотра, действий и возврата назад.

### 2.2 Не входит

- SQLDelight history.
- Persisted favorites.
- Реальный системный share sheet.
- Image share card.
- Bottom navigation.
- Remote/backend.
- Новая persistence-модель для detail.
- Большая переработка навигации, Decompose или Koin.

## 3. Пользовательские сценарии

### 3.1 Открыть detail по тапу на Home-карточку

Пользователь находится на Home и видит карточку текущего предсказания. По тапу на карточку приложение открывает `PredictionDetailScreen` с тем же предсказанием.

Ожидаемое поведение:

- открывается detail-экран;
- текст, категория, знак, дата и absurdity level соответствуют выбранной Home-карточке;
- отправляется analytics event `prediction_detail_viewed`.

### 3.2 Вернуться назад

Пользователь нажимает back action в top bar или системный back.

Ожидаемое поведение:

- приложение возвращается на Home;
- состояние Home не пересоздаётся без необходимости;
- текущая карточка и выбранные Home-параметры остаются на месте;
- отправляется analytics event `prediction_detail_back_clicked`.

### 3.3 Нажать `Поделиться`

Пользователь нажимает кнопку `Поделиться`.

Ожидаемое поведение MVP:

- реальный share sheet не открывается;
- пользователь видит короткий feedback, например snackbar/message;
- отправляется analytics event `prediction_detail_share_clicked`.

### 3.4 Нажать `В избранное`

Пользователь нажимает кнопку `В избранное`.

Ожидаемое поведение MVP:

- persisted favorites не используются;
- допустим локальный selected state в рамках текущего detail-экрана;
- пользователь видит короткий feedback, например snackbar/message;
- отправляется analytics event `prediction_favorite_clicked`.

### 3.5 Нажать `Следующее`

Кнопка `Следующее` в рамках MVP является заглушкой или явно отложенным действием.

Ожидаемое поведение MVP:

- действие не должно ломать текущий экран;
- пользователь получает понятный feedback, что функция появится позже;
- генерация следующего прогноза, история и persistence не реализуются в этом эпике.

## 4. UI Composition

### 4.1 Общая композиция

Экран должен переиспользовать визуальный язык Home и Onboarding:

- `CosmicBackground`;
- верхняя зона с back action;
- большая detail-card или unframed content;
- metadata row;
- action buttons.

Главная задача UI — дать длинному русскому тексту достаточно воздуха. Detail не должен выглядеть как маркетинговый hero или набор декоративных карточек.

### 4.2 Background

Используется `CosmicBackground` из существующей дизайн-системы приложения.

Требования:

- фон не должен ухудшать читаемость текста;
- основной контент должен иметь достаточный contrast;
- при длинном тексте экран должен корректно скроллиться.

### 4.3 Top bar / Back action

В верхней части экрана нужен back action.

Требования:

- back action визуально считывается как навигационное действие;
- действие доступно на compact и expanded размерах;
- нажатие возвращает на Home;
- analytics для back отправляется один раз на пользовательское действие.

### 4.4 Detail content

Основной блок detail может быть реализован как большая detail-card или как unframed content с выделенными секциями.

Минимальный состав:

- полный текст предсказания;
- категория;
- знак зодиака;
- дата;
- absurdity level;
- псевдонаучная подпись.

Detail-card должна стилистически наследовать `GlowingPredictionCard`, но не обязана копировать её layout. На detail-экране приоритетом является чтение длинного текста.

### 4.5 Metadata row

Metadata row показывает короткие атрибуты предсказания.

Минимальный набор:

- знак зодиака;
- категория;
- дата;
- absurdity level.

Требования:

- metadata не должна конкурировать с основным текстом;
- на узких экранах элементы должны переноситься без overlap;
- длинные русские значения не должны вылезать за контейнер.

### 4.6 Action buttons

Минимальный набор actions:

- `Поделиться`;
- `В избранное`;
- `Следующее`.

Требования:

- кнопки доступны после чтения основного контента;
- на compact ширине допускается вертикальная раскладка;
- feedback для MVP-действий должен быть коротким и понятным;
- real share sheet и persisted favorite не реализуются.

## 5. Архитектура

### 5.1 Feature package

Новый блок размещается в feature-пакете:

`feature/prediction/detail`

Рекомендуемые сущности:

- `PredictionDetailState`;
- `PredictionDetailIntent`;
- `PredictionDetailEvent`;
- `PredictionDetailViewModel` или lightweight state holder;
- `PredictionDetailScreen`.

### 5.2 State / Intent / Event

Detail должен следовать текущему подходу feature state.

Минимальный state:

- выбранное `DailyPrediction` или `Prediction`;
- текущий favorite selected state для MVP;
- transient feedback message;
- флаг processing не обязателен, но допустим для единообразия.

Минимальные intents:

- `BackClicked`;
- `ShareClicked`;
- `FavoriteClicked`;
- `NextClicked`;
- `FeedbackShown` или аналогичный intent для очистки transient message.

Минимальные events:

- `NavigateBack`;
- `ShowShareFeedback`;
- `ShowFavoriteFeedback`;
- `ShowNextFeedback`.

Конкретные имена можно адаптировать под существующие conventions проекта, но контракт должен оставаться явным и тестируемым.

### 5.3 Navigation в `App.kt`

В рамках MVP используется минимальная навигация в текущем `App.kt`, без внедрения Decompose/Koin.

Рекомендуемая модель:

- Home остаётся стартовым экраном после onboarding flow.
- Home продолжает отдавать `HomeEvent.PredictionSelected`.
- `App.kt` при получении `HomeEvent.PredictionSelected(predictionId)` берёт текущее предсказание из текущего `HomeState.Content`.
- Если `predictionId` соответствует текущему `dailyPrediction.prediction.id`, `App.kt` открывает detail и передаёт текущий `DailyPrediction`.
- Если данные недоступны, переход не выполняется или показывается безопасный fallback.

Такой подход сохраняет MVP компактным и не требует новой repository lookup/persistence-модели.

### 5.4 Reuse существующих моделей

Нужно переиспользовать существующие доменные модели:

- `DailyPrediction`;
- `Prediction`;
- связанные enum/value-модели категории, знака, даты и absurdity level.

Новые persistence-модели в этом эпике не добавляются.

## 6. Analytics

Detail MVP должен отправлять события через существующий `AnalyticsTracker`.

Обязательные события:

- `prediction_detail_viewed`;
- `prediction_detail_share_clicked`;
- `prediction_favorite_clicked`;
- `prediction_detail_back_clicked`.

Рекомендуемые параметры:

- `prediction_id`;
- `zodiac_sign`;
- `category`;
- `absurdity_level`;
- `date`;
- `source = home`.

Точные имена параметров можно адаптировать под существующий analytics style проекта.

## 7. Test Plan

Обязательные проверки:

- Home card tap открывает detail с тем же prediction.
- Back возвращает на Home без потери Home state.
- Detail показывает длинный русский текст без overlap.
- Share click показывает MVP message.
- Favorite click показывает MVP message или локальный selected state.
- Analytics events отправляются через `AnalyticsTracker`.

Обязательные команды verification:

```bash
./gradlew :composeApp:allTests
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

## 8. Acceptance Criteria

Prediction Detail MVP считается готовым, если:

- по тапу на Home-карточку открывается detail-экран;
- detail отображает то же предсказание, что было выбрано на Home;
- длинный русский текст читается без overlap на мобильной ширине;
- back action возвращает на Home и не сбрасывает Home state;
- `Поделиться` показывает MVP feedback;
- `В избранное` показывает MVP feedback или локальный selected state;
- `Следующее` безопасно обработано как MVP-заглушка или явно отложенное действие;
- обязательные analytics events отправляются;
- unit-тесты state/viewmodel покрывают основные intents;
- visual QA зафиксирован в документации;
- verification-команды проходят.

## 9. Что явно отложено

Следующие блоки не должны попадать в реализацию Prediction Detail MVP:

- SQLDelight history;
- persisted favorites;
- real share sheet;
- image share card;
- remote/backend;
- bottom navigation;
- полноценная навигационная архитектура;
- генерация следующего прогноза на detail-экране.
