# Home MVP: task breakdown

Документ разбивает `Epic 2: Дневное предсказание / Home MVP` на подзадачи, которые можно брать в разработку последовательно. Базовое ТЗ: `docs/HOME_TZ.md`.

## 1. Целевой результат

После выполнения блока:

- `HomePlaceholderScreen` заменён на настоящий `HomeScreen`;
- пользователь после онбординга видит дневное антипредсказание;
- предсказание стабильно в течение дня;
- пользователь может до 3 раз вручную запросить другое предсказание;
- выбранная категория влияет на ручную генерацию;
- лимит сохраняется между перезапусками;
- генератор и лимит покрыты unit-тестами.

## 2. Итерации

### Итерация 1: Domain и генератор

Цель: получить чистую генерацию без UI.

Задачи:

- `HOME-001`;
- `HOME-002`;
- `HOME-003`;
- `HOME-004`;
- `HOME-005`;
- `HOME-006`;
- `HOME-007`.

### Итерация 2: Лимиты и storage

Цель: добавить дневный лимит ручной генерации.

Задачи:

- `HOME-008`;
- `HOME-009`;
- `HOME-010`.

### Итерация 3: Presentation

Цель: собрать `HomeViewModel` и state machine.

Задачи:

- `HOME-011`;
- `HOME-012`;
- `HOME-013`.

### Итерация 4: UI

Цель: заменить placeholder на готовый Home.

Задачи:

- `HOME-014`;
- `HOME-015`;
- `HOME-016`;
- `HOME-017`;
- `HOME-018`;
- `HOME-019`.

### Итерация 5: Проверка и полировка

Цель: довести Home MVP до стабильного состояния.

Задачи:

- `HOME-020`;
- `HOME-021`;
- `HOME-022`;
- `HOME-023`;
- `HOME-024`.

## 3. Задачи

### HOME-001: Создать domain-модели предсказаний

**Тип:** domain  
**Приоритет:** P0  
**Зависимости:** нет  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/Prediction.kt`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/PredictionCategory.kt`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/DailyPrediction.kt`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/GenerationLimit.kt`

**Что сделать:**

- описать `Prediction`;
- описать `PredictionCategory`;
- описать `DailyPrediction`;
- описать `GenerationLimit`;
- добавить extension-поля для категории:
  - `titleRu`;
  - `analyticsName`;
  - `accentColorHex`;
  - `shortLabel`.

**Definition of Done:**

- модели лежат в `commonMain`;
- модели не зависят от Compose UI;
- `absurdityLevel` документирован как диапазон `1..5`;
- проект компилируется.

---

### HOME-002: Создать локальный каталог предсказаний

**Тип:** data/content  
**Приоритет:** P0  
**Зависимости:** `HOME-001`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/data/prediction/LocalPredictionCatalog.kt`

**Что сделать:**

- добавить минимум 50 предсказаний;
- покрыть 5 категорий минимум по 10 текстов;
- добавить общие предсказания с `zodiacSignId = null`;
- добавить несколько знако-специфичных предсказаний;
- проверить уникальность `id`.

**Definition of Done:**

- каталог возвращает `List<Prediction>`;
- нет пустых `id`;
- нет пустых `text`;
- все `absurdityLevel` в диапазоне `1..5`;
- тексты соответствуют юмористическому стилю приложения.

---

### HOME-003: Создать `PredictionRepository`

**Тип:** domain/data  
**Приоритет:** P0  
**Зависимости:** `HOME-002`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/PredictionRepository.kt`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/data/prediction/InMemoryPredictionRepository.kt`

**Что сделать:**

- описать repository interface;
- реализовать local/in-memory repository поверх `LocalPredictionCatalog`;
- добавить методы:
  - `getAllPredictions()`;
  - `getPredictionsForZodiac(zodiacSignId: String)`;
  - `getPredictionsByCategory(category: PredictionCategory)`.

**Definition of Done:**

- use cases не зависят от concrete catalog object;
- repository возвращает immutable collections;
- пустой каталог не приводит к crash.

---

### HOME-004: Реализовать `DateProvider`

**Тип:** core utility  
**Приоритет:** P0  
**Зависимости:** нет  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/core/time/DateProvider.kt`
- `composeApp/src/androidMain/kotlin/com/example/antihoroscope/core/time/SystemDateProvider.android.kt`
- `composeApp/src/iosMain/kotlin/com/example/antihoroscope/core/time/SystemDateProvider.ios.kt`

**Что сделать:**

- вернуть `dateKey` в формате `YYYY-MM-DD`;
- вернуть display label для UI;
- сделать provider injectable;
- добавить fake implementation для тестов.

**Definition of Done:**

- генератор не обращается к системной дате напрямую;
- тесты могут задавать дату явно;
- Android/iOS compile не ломается.

---

### HOME-005: Реализовать `GenerateDailyPredictionUseCase`

**Тип:** domain  
**Приоритет:** P0  
**Зависимости:** `HOME-001`, `HOME-003`, `HOME-004`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/GenerateDailyPredictionUseCase.kt`

**Что сделать:**

- собрать pool по знаку;
- использовать seed `dateKey + zodiacSignId + daily`;
- стабильно выбирать prediction;
- возвращать controlled result/error при пустом pool.

**Definition of Done:**

- одинаковые input дают одинаковый result;
- use case не зависит от UI;
- empty pool не падает exception в UI.

---

### HOME-006: Реализовать `GenerateManualPredictionUseCase`

**Тип:** domain  
**Приоритет:** P0  
**Зависимости:** `HOME-005`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/GenerateManualPredictionUseCase.kt`

**Что сделать:**

- использовать seed `dateKey + zodiacSignId + manual + generationIndex + category`;
- поддержать optional category;
- исключать текущее prediction, если после исключения pool не пустой;
- вернуть controlled result/error при пустом pool.

**Definition of Done:**

- ручная генерация работает без UI;
- category filter влияет на pool;
- текущий prediction не повторяется, если есть альтернатива.

---

### HOME-007: Добавить unit-тесты генератора

**Тип:** tests  
**Приоритет:** P0  
**Зависимости:** `HOME-005`, `HOME-006`  
**Файлы:**

- `composeApp/src/commonTest/kotlin/com/example/antihoroscope/domain/prediction/GenerateDailyPredictionUseCaseTest.kt`
- `composeApp/src/commonTest/kotlin/com/example/antihoroscope/domain/prediction/GenerateManualPredictionUseCaseTest.kt`

**Что проверить:**

- same date + same sign = same prediction;
- category filter работает;
- zodiac-specific prediction попадает в pool;
- empty pool возвращает controlled error;
- manual generation исключает текущий prediction при наличии альтернативы.

**Definition of Done:**

- тесты проходят через Gradle test task;
- generator edge cases зафиксированы до UI.

---

### HOME-008: Создать storage для лимита генерации

**Тип:** data/storage  
**Приоритет:** P0  
**Зависимости:** `HOME-004`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/data/settings/HomeSettingsStorage.kt`
- `composeApp/src/androidMain/kotlin/com/example/antihoroscope/data/settings/HomeSettingsStorage.android.kt`
- `composeApp/src/iosMain/kotlin/com/example/antihoroscope/data/settings/HomeSettingsStorage.ios.kt`

**Что сделать:**

- хранить `home_generation_limit_date`;
- хранить `home_generation_limit_used_count`;
- добавить методы read/save/reset;
- использовать existing storage style из онбординга.

**Definition of Done:**

- счётчик сохраняется между перезапусками;
- новая дата может быть обработана use case;
- storage не содержит бизнес-логики лимита.

---

### HOME-009: Реализовать use cases лимита

**Тип:** domain  
**Приоритет:** P0  
**Зависимости:** `HOME-008`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/GetGenerationLimitUseCase.kt`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/domain/prediction/ConsumeGenerationLimitUseCase.kt`

**Что сделать:**

- max count = `3`;
- если дата изменилась, вернуть свежий лимит;
- consume делать только при успешной manual generation;
- запретить consume сверх лимита.

**Definition of Done:**

- 3 refresh доступны;
- 4-й refresh blocked;
- новая дата сбрасывает лимит.

---

### HOME-010: Добавить unit-тесты лимита

**Тип:** tests  
**Приоритет:** P0  
**Зависимости:** `HOME-009`  
**Файлы:**

- `composeApp/src/commonTest/kotlin/com/example/antihoroscope/domain/prediction/GenerationLimitUseCaseTest.kt`

**Что проверить:**

- initial remaining count = 3;
- после consume remaining уменьшается;
- после 3 consume лимит exhausted;
- 4-й consume не проходит;
- новая дата сбрасывает счётчик.

**Definition of Done:**

- логика лимита защищена тестами;
- тесты не зависят от реальной даты.

---

### HOME-011: Создать Home state contract

**Тип:** presentation  
**Приоритет:** P0  
**Зависимости:** `HOME-001`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/HomeState.kt`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/HomeIntent.kt`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/HomeEvent.kt`

**Что сделать:**

- описать `Loading`;
- описать `Content`;
- описать `MissingZodiac`;
- описать `EmptyCatalog`;
- описать `Error`;
- добавить intents:
  - `ScreenShown`;
  - `CategorySelected`;
  - `RefreshClicked`;
  - `ShareClicked`;
  - `PredictionClicked`.

**Definition of Done:**

- `HomeScreen` можно собрать только по state;
- бизнес-логика не протекает в UI.

---

### HOME-012: Реализовать `HomeViewModel`

**Тип:** presentation  
**Приоритет:** P0  
**Зависимости:** `HOME-005`, `HOME-006`, `HOME-009`, `HOME-011`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/HomeViewModel.kt`

**Что сделать:**

- загрузить выбранный знак из existing onboarding/settings storage;
- загрузить daily prediction;
- загрузить generation limit;
- обработать выбор категории;
- обработать refresh;
- обработать share click через event/message.

**Definition of Done:**

- ViewModel отдаёт `StateFlow<HomeState>`;
- ViewModel не зависит от Compose UI;
- refresh не обходит лимит.

---

### HOME-013: Добавить tests для `HomeViewModel`

**Тип:** tests  
**Приоритет:** P1  
**Зависимости:** `HOME-012`  
**Файлы:**

- `composeApp/src/commonTest/kotlin/com/example/antihoroscope/feature/home/HomeViewModelTest.kt`

**Что проверить:**

- initial loading -> content;
- missing zodiac -> `MissingZodiac`;
- category selected обновляет state;
- refresh success обновляет prediction;
- exhausted refresh показывает message/error state.

**Definition of Done:**

- основные пользовательские действия покрыты без UI-тестов.

---

### HOME-014: Создать `GlowingPredictionCard`

**Тип:** UI component  
**Приоритет:** P0  
**Зависимости:** `HOME-001`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/ui/components/GlowingPredictionCard.kt`

**Что сделать:**

- card background в стиле glass/cosmic;
- neon border;
- category accent;
- prediction text;
- absurdity level;
- date/zodiac metadata.

**Definition of Done:**

- компонент переиспользуем;
- длинный текст не выходит за границы;
- компонент не содержит бизнес-логики.

---

### HOME-015: Создать category row

**Тип:** UI component  
**Приоритет:** P0  
**Зависимости:** `HOME-001`, `HOME-011`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/components/PredictionCategoryRow.kt`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/components/PredictionCategoryCard.kt`

**Что сделать:**

- horizontal scroll;
- 5 категорий;
- selected state;
- tap callback;
- аккуратная neon-подсветка выбранной категории.

**Definition of Done:**

- selected category визуально понятна;
- row не ломает маленькие экраны.

---

### HOME-016: Создать `GenerationLimitIndicator`

**Тип:** UI component  
**Приоритет:** P1  
**Зависимости:** `HOME-001`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/components/GenerationLimitIndicator.kt`

**Что сделать:**

- показать remaining count;
- показать exhausted state;
- не занимать много места;
- использовать ироничный copy.

**Definition of Done:**

- пользователь понимает, сколько refresh осталось;
- exhausted state заметен, но не агрессивен.

---

### HOME-017: Реализовать `HomeScreen`

**Тип:** screen  
**Приоритет:** P0  
**Зависимости:** `HOME-011`, `HOME-014`, `HOME-015`, `HOME-016`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/HomeScreen.kt`

**Что сделать:**

- использовать `CosmicBackground`;
- собрать header;
- добавить prediction card;
- добавить category row;
- добавить refresh button;
- добавить share button;
- отрисовать loading/error/missing zodiac states.

**Definition of Done:**

- экран полностью отображает `HomeState.Content`;
- экран не содержит генерации/лимитов;
- UI адаптируется под маленькие экраны.

---

### HOME-018: Подключить `HomeScreen` вместо placeholder

**Тип:** integration  
**Приоритет:** P0  
**Зависимости:** `HOME-012`, `HOME-017`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/App.kt`
- возможно `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/home/HomePlaceholderScreen.kt`

**Что сделать:**

- собрать dependencies вручную в текущем стиле проекта;
- заменить `HomePlaceholderScreen`;
- сохранить fallback для missing zodiac;
- не ломать onboarding flow.

**Definition of Done:**

- после завершённого онбординга открывается настоящий Home;
- placeholder больше не используется в основном flow.

---

### HOME-019: Добавить MVP share feedback

**Тип:** UX  
**Приоритет:** P1  
**Зависимости:** `HOME-012`, `HOME-017`  
**Файлы:**

- `feature/home/HomeViewModel.kt`
- `feature/home/HomeScreen.kt`

**Что сделать:**

- обработать `ShareClicked`;
- показать message/event;
- добавить analytics event `prediction_share_clicked`.

**Definition of Done:**

- кнопка `Поделиться` не является мёртвой;
- реальный share sheet явно отложен.

---

### HOME-020: Подключить analytics events

**Тип:** analytics  
**Приоритет:** P1  
**Зависимости:** `HOME-012`  
**Файлы:**

- `feature/home/HomeViewModel.kt`
- возможно `core/analytics/AnalyticsEvent.kt`, если есть централизованный список событий.

**Что сделать:**

- `home_viewed`;
- `prediction_viewed`;
- `prediction_refreshed`;
- `prediction_refresh_limit_reached`;
- `prediction_category_selected`;
- `prediction_share_clicked`.

**Definition of Done:**

- события идут через существующий `AnalyticsTracker`;
- no-op tracker не ломает тесты.

---

### HOME-021: Расширить каталог до 100+ предсказаний

**Тип:** content  
**Приоритет:** P2  
**Зависимости:** `HOME-002`  
**Файлы:**

- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/data/prediction/LocalPredictionCatalog.kt`

**Что сделать:**

- увеличить каталог до 100+;
- проверить повторы;
- проверить баланс категорий;
- убрать токсичные или слишком персональные формулировки.

**Definition of Done:**

- минимум 100 предсказаний;
- каждая категория имеет минимум 20 текстов.

---

### HOME-022: Visual QA Home

**Тип:** QA  
**Приоритет:** P1  
**Зависимости:** `HOME-017`, `HOME-018`  
**Файлы:**

- `docs/HOME_VISUAL_QA.md` при необходимости

**Что проверить:**

- маленький Android экран;
- iPhone SE-like;
- длинные русские тексты;
- exhausted state;
- selected category;
- loading/error states;
- тёмная тема и контраст.

**Definition of Done:**

- нет overlap;
- нет обрезанного текста в кнопках;
- card readable на маленьких экранах.

---

### HOME-023: Запустить проверки сборки и тестов

**Тип:** verification  
**Приоритет:** P0  
**Зависимости:** все P0/P1 задачи  
**Команды:**

```bash
./gradlew allTests
./gradlew assembleDebug
```

**Definition of Done:**

- tests проходят;
- Android debug build проходит;
- если iOS compile target доступен локально, проверить common/iOS compilation.

---

### HOME-024: Обновить документацию статуса

**Тип:** docs  
**Приоритет:** P1  
**Зависимости:** завершение реализации  
**Файлы:**

- `docs/HOME_TASKS.md`
- `PLAN.md` при необходимости

**Что сделать:**

- отметить выполненные HOME-задачи;
- добавить известные ограничения;
- зафиксировать, что отложено на следующие эпики.

**Definition of Done:**

- по документации понятно, что реализовано;
- следующий блок можно планировать без повторного анализа.

## 4. Рекомендуемый порядок реализации

1. `HOME-001` — domain-модели.
2. `HOME-002` — локальный каталог на 50 предсказаний.
3. `HOME-003` — repository.
4. `HOME-004` — date provider.
5. `HOME-005` — daily generator.
6. `HOME-006` — manual generator.
7. `HOME-007` — tests генератора.
8. `HOME-008` — storage лимита.
9. `HOME-009` — use cases лимита.
10. `HOME-010` — tests лимита.
11. `HOME-011` — state contract.
12. `HOME-012` — HomeViewModel.
13. `HOME-014` — GlowingPredictionCard.
14. `HOME-015` — category row.
15. `HOME-016` — limit indicator.
16. `HOME-017` — HomeScreen.
17. `HOME-018` — подключение в App.
18. `HOME-019` — share feedback.
19. `HOME-020` — analytics.
20. `HOME-022` — visual QA.
21. `HOME-023` — финальные проверки.
22. `HOME-024` — обновить статус.

`HOME-021` можно делать параллельно после появления базового каталога, потому что это content-задача.

## 5. Definition of Done для Home MVP

- Пользователь после онбординга попадает на настоящий Home.
- Home показывает выбранный знак.
- Home показывает дату.
- Home показывает дневное предсказание.
- Daily prediction стабилен в рамках одной даты.
- Manual refresh работает.
- Manual refresh ограничен 3 попытками в день.
- Лимит сохраняется между перезапусками.
- Лимит сбрасывается на новую дату.
- Category row отображается и влияет на manual generation.
- Share button даёт MVP feedback.
- Empty/error states не приводят к crash.
- Generator покрыт unit-тестами.
- Limit покрыт unit-тестами.
- `./gradlew allTests` проходит.
- `./gradlew assembleDebug` проходит.

## 6. Что брать первым

Первая реализационная задача:

```text
HOME-001: Создать domain-модели предсказаний
```

После неё логично сразу делать `HOME-002`, потому что без каталога нельзя проверить generator end-to-end.
