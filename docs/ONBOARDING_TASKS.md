# Онбординг: декомпозиция задач для реализации

Документ разбивает `docs/ONBOARDING_TZ.md` на конкретные инженерные задачи. Порядок подобран так, чтобы как можно раньше получить рабочий end-to-end flow: первый запуск -> онбординг -> выбор знака -> завершение -> главный экран.

## 0. Текущее состояние проекта

Проект сейчас находится в состоянии стандартного Compose Multiplatform шаблона:
- основной UI расположен в `composeApp/src/commonMain/kotlin/com/example/antihoroscope/App.kt`;
- отдельной feature-структуры пока нет;
- DI, navigation, settings storage и domain/data слои ещё не заведены;
- для первого этапа онбординг можно реализовать внутри `composeApp`, без преждевременного разнесения по модулям.

## 1. Целевой результат первой реализации

После выполнения задач первой итерации должно работать:
- при запуске показывается `OnboardingScreen`;
- есть 4 шага: приветствие, концепция, выбор знака, уведомления;
- кнопки `Далее`, `Назад`, `Пропустить` работают по правилам из ТЗ;
- без выбора знака нельзя завершить онбординг;
- выбранный знак сохраняется в памяти приложения на время сессии;
- после завершения показывается временный `HomePlaceholderScreen`;
- визуал уже похож на dark cosmic UI, пусть без финальной полировки.

Локальное постоянное хранение, реальные permissions уведомлений и полноценная навигационная библиотека выносятся во вторую итерацию, чтобы не утонуть в инфраструктуре до появления живого экрана.

## 2. Итерации

### Итерация 1: Рабочий UI-flow без постоянного хранения

Цель: собрать онбординг в Compose и подключить его в `App.kt`.

Оценка: 1.5-2 рабочих дня.

### Итерация 2: Состояние, хранение и стартовая логика

Цель: сохранять `onboarding_completed`, `zodiac_sign_id`, `notification_time` и при повторном запуске открывать Home.

Оценка: 1-1.5 рабочих дня.

### Итерация 3: Платформенные уведомления

Цель: добавить expect/actual API для запроса разрешений и планирования ежедневного уведомления.

Оценка: 1.5-2 рабочих дня.

### Итерация 4: Полировка, тесты, аналитика

Цель: довести экран до production-ready состояния: анимации, accessibility, тесты, analytics events.

Оценка: 2-3 рабочих дня.

## 3. Подзадачи

### ONB-001: Создать структуру feature onboarding

**Тип:** tech  
**Приоритет:** P0  
**Зависимости:** нет  
**Файлы:**
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/onboarding/`
- `composeApp/src/commonMain/kotlin/com/example/antihoroscope/feature/onboarding/components/`

**Что сделать:**
- создать папки feature;
- завести пустые файлы под экран, state, intents и компоненты;
- не подключать DI на этом шаге.

**Definition of Done:**
- структура файлов создана;
- проект компилируется.

---

### ONB-002: Описать модели онбординга

**Тип:** domain/presentation  
**Приоритет:** P0  
**Зависимости:** ONB-001  
**Файлы:**
- `OnboardingStep.kt`
- `OnboardingState.kt`
- `OnboardingIntent.kt`
- `ZodiacSignUiModel.kt`

**Что сделать:**
- создать `enum class OnboardingStep`;
- создать `data class OnboardingState`;
- создать `sealed interface OnboardingIntent`;
- создать UI-модель знака зодиака;
- добавить список 12 знаков зодиака как временный in-memory catalog.

**Минимальные поля `OnboardingState`:**
- `currentStep`;
- `selectedZodiacSignId`;
- `notificationsEnabled`;
- `notificationTime`;
- `isCompleting`;
- `errorMessage`.

**Definition of Done:**
- состояние полностью описывает UI;
- нет Android/iOS зависимостей в commonMain;
- список знаков доступен из UI.

---

### ONB-003: Реализовать reducer/state holder для онбординга

**Тип:** logic  
**Приоритет:** P0  
**Зависимости:** ONB-002  
**Файлы:**
- `OnboardingViewModel.kt` или временно `OnboardingStateHolder.kt`

**Что сделать:**
- обработать `NextClicked`;
- обработать `BackClicked`;
- обработать `SkipClicked`;
- обработать `ZodiacSelected`;
- обработать `NotificationToggleChanged`;
- обработать `NotificationTimeChanged`;
- обработать `CompleteClicked`;
- добавить callback `onCompleted`.

**Правила:**
- `SkipClicked` на слайдах Welcome/Concept ведёт сразу к Zodiac;
- `SkipClicked` на Zodiac игнорируется;
- `NextClicked` на Zodiac не работает без выбранного знака;
- `CompleteClicked` завершает flow только если есть знак.

**Рекомендация:**
- на первой итерации можно использовать `rememberSaveable`/`remember` state holder без lifecycle ViewModel;
- ViewModel подключить позже, когда появятся storage/use cases.

**Definition of Done:**
- логика шагов работает без UI;
- нет дублирования правил в Composable;
- все переходы соответствуют ТЗ.

---

### ONB-004: Создать базовую тему Антигороскопа

**Тип:** UI foundation  
**Приоритет:** P0  
**Зависимости:** нет  
**Файлы:**
- `ui/theme/AntiHoroscopeTheme.kt`
- `ui/theme/Colors.kt`
- `ui/theme/Typography.kt`

**Что сделать:**
- заменить шаблонный `MaterialTheme` на кастомную dark theme;
- определить цвета:
  - background black/purple;
  - primary neon purple;
  - secondary magenta/cyan;
  - card glass dark;
  - text primary/secondary.
- задать базовую типографику.

**Definition of Done:**
- `App()` обёрнут в `AntiHoroscopeTheme`;
- фон и текст выглядят как тёмный cosmic UI;
- цвета не размазаны хардкодом по всем компонентам.

---

### ONB-005: Создать `CosmicBackground`

**Тип:** UI component  
**Приоритет:** P0  
**Зависимости:** ONB-004  
**Файлы:**
- `ui/components/CosmicBackground.kt`

**Что сделать:**
- реализовать full-screen background;
- использовать тёмный vertical/radial gradient;
- добавить лёгкие статичные звёзды через `Canvas`;
- анимацию частиц оставить опциональной на полировку.

**Definition of Done:**
- фон занимает весь экран;
- учитываются safe insets на уровне контента;
- нет тяжёлой анимации, которая может просаживать FPS.

---

### ONB-006: Создать `OnboardingProgressDots`

**Тип:** UI component  
**Приоритет:** P0  
**Зависимости:** ONB-004  
**Файлы:**
- `feature/onboarding/components/OnboardingProgressDots.kt`

**Что сделать:**
- 4 точки прогресса;
- активная точка шире или ярче;
- добавить простую анимацию смены активной точки.

**Definition of Done:**
- прогресс соответствует текущему шагу;
- точки не кликабельны;
- компонент переиспользуемый и не знает про тексты шагов.

---

### ONB-007: Создать базовые кнопки онбординга

**Тип:** UI component  
**Приоритет:** P0  
**Зависимости:** ONB-004  
**Файлы:**
- `ui/components/CosmicButton.kt`
- `ui/components/CosmicTextButton.kt`

**Что сделать:**
- primary button с neon gradient/glow;
- secondary text button;
- disabled состояние;
- loading состояние можно добавить позже.

**Definition of Done:**
- кнопки имеют минимальную высоту 48dp;
- текст не обрезается;
- disabled состояние визуально понятно.

---

### ONB-008: Создать `OnboardingPage`

**Тип:** UI component  
**Приоритет:** P0  
**Зависимости:** ONB-005, ONB-006, ONB-007  
**Файлы:**
- `feature/onboarding/components/OnboardingPage.kt`

**Что сделать:**
- общий layout для шагов;
- slots:
  - `visual`;
  - `title`;
  - `description`;
  - `content`;
  - `primaryButton`;
  - `secondaryButton`;
- поддержать compact экран через `Column` + vertical scroll.

**Definition of Done:**
- Welcome, Concept и Notifications можно собрать через этот layout;
- CTA не перекрываются системной навигацией;
- экран нормально выглядит на 360x640 dp.

---

### ONB-009: Создать `ZodiacSelector`

**Тип:** UI component  
**Приоритет:** P0  
**Зависимости:** ONB-002, ONB-004  
**Файлы:**
- `feature/onboarding/components/ZodiacSelector.kt`
- `feature/onboarding/components/ZodiacCard.kt`

**Что сделать:**
- сетка из 12 знаков;
- карточка: символ, название, даты;
- selected state;
- callback `onZodiacSelected(id)`;
- адаптация для маленьких экранов через scroll.

**Definition of Done:**
- можно выбрать знак;
- выбранный знак очевидно подсвечен;
- `Продолжить` активируется после выбора;
- есть `contentDescription` для accessibility.

---

### ONB-010: Создать `NotificationTimeSelector`

**Тип:** UI component  
**Приоритет:** P1  
**Зависимости:** ONB-004  
**Файлы:**
- `feature/onboarding/components/NotificationTimeSelector.kt`

**Что сделать:**
- toggle уведомлений;
- отображение выбранного времени;
- быстрые пресеты `09:00`, `13:00`, `20:00`;
- на MVP без platform time picker, только пресеты.

**Definition of Done:**
- пользователь может включить/выключить уведомления;
- пользователь может выбрать один из трёх пресетов;
- состояние отдаётся наружу через callbacks.

---

### ONB-011: Собрать `OnboardingScreen`

**Тип:** screen  
**Приоритет:** P0  
**Зависимости:** ONB-003, ONB-008, ONB-009, ONB-010  
**Файлы:**
- `feature/onboarding/OnboardingScreen.kt`

**Что сделать:**
- собрать 4 шага;
- подключить progress dots;
- подключить `ZodiacSelector`;
- подключить `NotificationTimeSelector`;
- добавить тексты из ТЗ;
- обработать `onCompleted`.

**Definition of Done:**
- весь flow проходится вручную;
- `Пропустить` работает по правилам;
- без знака нельзя завершить;
- после последнего шага вызывается `onCompleted`.

---

### ONB-012: Создать временный `HomePlaceholderScreen`

**Тип:** screen  
**Приоритет:** P0  
**Зависимости:** ONB-004  
**Файлы:**
- `feature/home/HomePlaceholderScreen.kt`

**Что сделать:**
- создать простой экран-заглушку после онбординга;
- показать выбранный знак;
- добавить текст: `Антигороскоп почти готов. Звёзды уже нервничают.`

**Definition of Done:**
- после онбординга пользователь видит не шаблон Compose, а экран приложения;
- экран принимает `selectedZodiacSignId` или UI-модель.

---

### ONB-013: Подключить flow в `App.kt`

**Тип:** integration  
**Приоритет:** P0  
**Зависимости:** ONB-011, ONB-012  
**Файлы:**
- `App.kt`

**Что сделать:**
- удалить шаблонный `Click me`;
- добавить простую app state:
  - `showOnboarding`;
  - `selectedZodiacSignId`;
- если onboarding не завершён, показывать `OnboardingScreen`;
- после завершения показывать `HomePlaceholderScreen`.

**Definition of Done:**
- приложение запускается сразу в онбординг;
- после прохождения открывается Home placeholder;
- проект компилируется на commonMain.

---

### ONB-014: Добавить локальное постоянное хранение настроек

**Тип:** data  
**Приоритет:** P1  
**Зависимости:** ONB-013  
**Файлы:**
- `data/settings/SettingsStorage.kt`
- `data/settings/InMemorySettingsStorage.kt`
- позже: `MultiplatformSettingsStorage.kt`

**Что сделать:**
- определить interface для settings;
- сначала реализовать in-memory или простую заглушку;
- затем подключить `multiplatform-settings`;
- сохранить:
  - `onboarding_completed`;
  - `zodiac_sign_id`;
  - `notifications_enabled`;
  - `notification_time`.

**Definition of Done:**
- после перезапуска приложения онбординг не показывается;
- выбранный знак сохраняется;
- storage не завязан напрямую на UI.

---

### ONB-015: Добавить `CompleteOnboardingUseCase`

**Тип:** domain  
**Приоритет:** P1  
**Зависимости:** ONB-014  
**Файлы:**
- `domain/onboarding/CompleteOnboardingUseCase.kt`
- `domain/onboarding/ObserveOnboardingStatusUseCase.kt`

**Что сделать:**
- вынести сохранение результата из UI/state holder;
- валидировать, что знак выбран;
- возвращать success/error result.

**Definition of Done:**
- UI не пишет напрямую в storage;
- completion можно тестировать unit-тестом;
- ошибка сохранения отображается на экране.

---

### ONB-016: Перевести state holder в ViewModel

**Тип:** presentation  
**Приоритет:** P1  
**Зависимости:** ONB-015  
**Файлы:**
- `feature/onboarding/OnboardingViewModel.kt`

**Что сделать:**
- использовать `StateFlow<OnboardingState>`;
- принимать intents;
- вызывать use cases;
- эмитить one-shot event `OnboardingCompleted`.

**Definition of Done:**
- состояние переживает recomposition;
- UI подписан на state;
- бизнес-логика не находится в Composable.

---

### ONB-017: Добавить platform notification permission API

**Тип:** platform  
**Приоритет:** P2  
**Зависимости:** ONB-016  
**Файлы:**
- `platform/notifications/NotificationPermissionManager.kt`
- `androidMain/.../NotificationPermissionManager.android.kt`
- `iosMain/.../NotificationPermissionManager.ios.kt`

**Что сделать:**
- создать `expect` API;
- Android: подготовить запрос `POST_NOTIFICATIONS` для Android 13+;
- iOS: подготовить запрос через `UNUserNotificationCenter`;
- на первой версии можно вернуть mock status до полной платформенной интеграции.

**Definition of Done:**
- common code не зависит от Android/iOS SDK;
- permission status отображается в UI;
- отказ не блокирует завершение онбординга.

---

### ONB-018: Добавить daily notification scheduler

**Тип:** platform  
**Приоритет:** P2  
**Зависимости:** ONB-017  
**Файлы:**
- `platform/notifications/NotificationScheduler.kt`
- `androidMain/.../NotificationScheduler.android.kt`
- `iosMain/.../NotificationScheduler.ios.kt`

**Что сделать:**
- создать `expect` API для планирования уведомления;
- Android: подготовить WorkManager/AlarmManager решение;
- iOS: подготовить `UNCalendarNotificationTrigger`;
- сохранить выбранное время в settings.

**Definition of Done:**
- включение уведомлений сохраняется;
- scheduler вызывается после granted permission;
- ошибка scheduler не ломает onboarding completion.

---

### ONB-019: Добавить аналитику онбординга

**Тип:** analytics  
**Приоритет:** P2  
**Зависимости:** ONB-016  
**Файлы:**
- `analytics/AnalyticsTracker.kt`
- `feature/onboarding/OnboardingAnalytics.kt`

**Что сделать:**
- создать interface `AnalyticsTracker`;
- добавить no-op реализацию для разработки;
- логировать события:
  - `onboarding_started`;
  - `onboarding_step_viewed`;
  - `onboarding_skipped`;
  - `zodiac_selected`;
  - `notification_permission_requested`;
  - `onboarding_completed`.

**Definition of Done:**
- события вызываются из ViewModel/use case уровня;
- ошибки аналитики не влияют на UI;
- параметры событий соответствуют ТЗ.

---

### ONB-020: Написать unit-тесты state machine

**Тип:** tests  
**Приоритет:** P1  
**Зависимости:** ONB-003 или ONB-016  
**Файлы:**
- `composeApp/src/commonTest/kotlin/com/example/antihoroscope/feature/onboarding/OnboardingStateHolderTest.kt`
- позже: `OnboardingViewModelTest.kt`

**Что проверить:**
- стартовый шаг `Welcome`;
- `NextClicked` переводит Welcome -> Concept -> Zodiac -> Notifications;
- `SkipClicked` с Welcome/Concept ведёт на Zodiac;
- `SkipClicked` на Zodiac не пропускает выбор;
- `NextClicked` на Zodiac без знака не работает;
- выбор знака сохраняется в state;
- completion невозможен без знака.

**Definition of Done:**
- тесты запускаются через Gradle;
- ключевые бизнес-правила покрыты.

---

### ONB-021: Проверить адаптивность вручную

**Тип:** QA  
**Приоритет:** P1  
**Зависимости:** ONB-013  
**Что проверить:**
- Android small: 360x640;
- Android typical: 412x915;
- iPhone SE-like;
- iPhone 15-like;
- landscape хотя бы без критического краша.

**Definition of Done:**
- тексты не обрезаются;
- кнопки доступны;
- Zodiac grid скроллится;
- CTA не уезжает под system bars.

---

### ONB-022: Визуальная полировка

**Тип:** UI polish  
**Приоритет:** P2  
**Зависимости:** ONB-013  
**Что сделать:**
- добавить плавную смену слайдов;
- добавить glow у карточек;
- добавить лёгкие частицы в `CosmicBackground`;
- улучшить zodiac icons;
- добавить микрокопирайтинг для выбранного знака.

**Definition of Done:**
- UI соответствует концепции Google Stitch: dark cosmic, neon, mystical, ironic;
- анимации не мешают чтению;
- экран не выглядит как стандартный Material шаблон.

## 4. Рекомендуемый порядок выполнения

Для старта разработки брать задачи так:

~1. `ONB-001` - структура feature.
2. `ONB-002` - модели и список знаков.
3. `ONB-003` - state machine.
4. `ONB-004` - базовая тема.
5. `ONB-005` - cosmic background.
6. `ONB-006` - progress dots.
7. `ONB-007` - кнопки.
8. `ONB-008` - общий layout страницы.
9. `ONB-009` - zodiac selector.
10. `ONB-010` - notification time selector.
11. `ONB-011` - сборка OnboardingScreen.
12. `ONB-012` - Home placeholder.
13. `ONB-013` - подключение в App.kt.~

После `ONB-013` уже можно запускать приложение и руками проходить онбординг.

Далее:

~14. `ONB-020` - тесты state machine.~

~15. `ONB-014` - постоянное хранение.~
~16. `ONB-015` - use cases.~
~17. `ONB-016` - ViewModel.~
~18. `ONB-021` - ручная адаптивная проверка.~

~19. `ONB-017` - permission API.~
~20. `ONB-018` - scheduler.~
~21. `ONB-019` - аналитика.~
~22. `ONB-022` - визуальная полировка.~

## 5. Минимальный набор файлов для первой итерации

```text
composeApp/src/commonMain/kotlin/com/example/antihoroscope/
├── App.kt
├── feature/
│   ├── home/
│   │   └── HomePlaceholderScreen.kt
│   └── onboarding/
│       ├── OnboardingIntent.kt
│       ├── OnboardingScreen.kt
│       ├── OnboardingState.kt
│       ├── OnboardingStateHolder.kt
│       ├── OnboardingStep.kt
│       ├── ZodiacSignUiModel.kt
│       └── components/
│           ├── NotificationTimeSelector.kt
│           ├── OnboardingPage.kt
│           ├── OnboardingProgressDots.kt
│           ├── ZodiacCard.kt
│           └── ZodiacSelector.kt
└── ui/
    ├── components/
    │   ├── CosmicBackground.kt
    │   ├── CosmicButton.kt
    │   └── CosmicTextButton.kt
    └── theme/
        ├── AntiHoroscopeTheme.kt
        ├── Colors.kt
        └── Typography.kt
```

## 6. Что можно начать делать прямо сейчас

Первая практическая задача:

**Взять `ONB-001` - `ONB-003`:**
- создать feature-папки;
- добавить модели `OnboardingStep`, `OnboardingState`, `OnboardingIntent`;
- добавить `ZodiacSignUiModel` и список знаков;
- реализовать чистую state machine без UI.

Это правильный первый шаг, потому что после него UI будет просто отображением уже понятной логики, а не местом, где незаметно расползутся правила онбординга.

## 7. Definition of Done для всего onboarding MVP

- Первый запуск открывает онбординг.
- Онбординг имеет 4 шага.
- Слайды 1, 2 и 4 можно пропустить.
- Слайд выбора знака нельзя пропустить.
- Без выбранного знака нельзя завершить flow.
- Выбранный знак сохраняется.
- После завершения открывается Home.
- При повторном запуске онбординг не показывается.
- Уведомления можно включить или пропустить.
- Отказ от уведомлений не блокирует завершение.
- UI не ломается на маленьком экране.
- Бизнес-правила covered unit-тестами.
- Код не содержит Android/iOS зависимостей в common UI и state logic.
