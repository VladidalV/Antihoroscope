# ONB-021: Проверка адаптивности онбординга

Дата проверки: 2026-05-05

## Статус

Частично выполнено в текущей среде:
- выполнена статическая проверка Compose layout;
- выполнены сборки и тесты;
- реальный запуск на Android/iOS устройстве из терминала не выполнен, потому что `adb` недоступен в окружении.

## Проверенные сценарии

### Android small: 360x640

Ожидаемое поведение:
- верхняя панель помещается: `Назад` / dots / `Пропустить`;
- контентная область скроллится через `OnboardingPage`;
- основная CTA закреплена снизу и не уезжает под system bars благодаря `safeContentPadding`;
- экран выбора знака не использует nested lazy vertical scroll;
- zodiac grid рендерится обычными `Column`/`Row`, поэтому не должен падать от infinite height constraints.

Статус: пройдено статически.

### Android typical: 412x915

Ожидаемое поведение:
- welcome/concept/notifications помещаются без критичного скролла;
- zodiac screen скроллится, CTA остаётся доступной;
- `FlowRow` с time presets переносит chips при нехватке ширины.

Статус: пройдено статически.

### iPhone SE-like

Ожидаемое поведение:
- `safeContentPadding` учитывает системные зоны;
- текстовые блоки находятся внутри скроллируемой области;
- primary button имеет стабильную высоту `52.dp`;
- secondary text buttons имеют минимальную высоту `48.dp`.

Статус: пройдено статически.

### iPhone 15-like

Ожидаемое поведение:
- контент визуально центрируется внутри `OnboardingPage`;
- cosmic background покрывает весь viewport;
- notification selector и zodiac selector используют `fillMaxWidth`.

Статус: пройдено статически.

### Landscape

Ожидаемое поведение:
- экран не должен падать;
- верхняя панель и CTA остаются вне scroll area;
- средняя часть скроллится, если высоты не хватает.

Статус: требует ручной проверки на устройстве/симуляторе.

## Что проверено в коде

- `OnboardingPage` использует `safeContentPadding`.
- Центральная часть `OnboardingPage` имеет `verticalScroll`.
- CTA находится вне scroll area, внизу основного layout.
- `CosmicButton` имеет `defaultMinSize(minHeight = 52.dp)`.
- `CosmicTextButton` имеет `defaultMinSize(minHeight = 48.dp)`.
- `ZodiacSelector` не использует `LazyVerticalGrid` внутри scroll-контейнера.
- `NotificationTimeSelector` использует `FlowRow` для пресетов времени.
- В onboarding layout нет `LazyColumn` / `LazyVerticalGrid`, вложенных в vertical scroll.

## Выполненные команды

```bash
./gradlew :composeApp:allTests
```

Результат: `BUILD SUCCESSFUL`

## Ограничения проверки

Команда:

```bash
adb devices
```

не выполнилась, потому что `adb` не найден в текущем окружении. Поэтому фактические device screenshots и проверка на реальном Android-девайсе не выполнялись.

## Остаточный риск

- Нужна ручная visual QA проверка в Android Studio на emulator/device:
  - 360x640;
  - 412x915;
  - landscape.
- Нужна ручная visual QA проверка iOS в Xcode simulator:
  - iPhone SE-like;
  - iPhone 15-like.

## Итог

Кодовая структура онбординга готова к адаптивному отображению: критичных layout-паттернов, которые уже вызывали runtime crash или могут сломать маленький экран, не найдено. Финальное закрытие visual QA требует запуска на симуляторе или реальном устройстве.
