# Home Visual QA

Date: 2026-05-06

Scope: HOME-022 visual QA for `HomeScreen` and supporting Home UI components.

## Method

- Static Compose layout review for small Android and iPhone SE-like widths.
- State review for `Content`, `Loading`, `MissingZodiac`, `EmptyCatalog`, `Error`, exhausted limit, selected category, and transient share/limit messages.
- Compilation verification through Kotlin metadata after layout adjustment.

No emulator or screenshot harness is configured in this KMP project, so this pass records code-level visual QA and layout risk checks.

## Checklist

| Scenario | Result | Notes |
| --- | --- | --- |
| Small Android screen | Pass | Home content uses `verticalScroll`, safe content padding, and no fixed screen-height assumptions. |
| iPhone SE-like screen | Pass | Main content can scroll; category cards use horizontal scroll instead of shrinking. |
| Long Russian prediction text | Pass | `GlowingPredictionCard` has no fixed max height; prediction text wraps and the card grows. |
| Exhausted state | Pass | Refresh button disables through `state.canRefresh`; `GenerationLimitIndicator` shows `Космос выдохся` with soft rose accent. |
| Selected category | Pass | Selected card has stronger border, glow, scale, and semantics selected state. |
| Loading state | Pass | Centered loader and short text with safe padding. |
| Missing zodiac / empty / error states | Pass | Centered title and message, no action overlap. |
| Dark theme contrast | Pass | Primary text uses `starWhite`; secondary text uses `moonMuted`; active accents use cyan/purple/rose. |
| Button text clipping | Pass | `Другое предсказание`, `Космос думает...`, and `Поделиться` fit within current button styles. |
| Transient message overlap | Fixed | Added bottom spacer after share action so feedback message has breathing room on small screens. |

## Follow-up

For a later polish pass, add screenshot-based verification once an Android emulator, iOS simulator, or Compose screenshot test harness is available.
