# Трекер привычек

Мобильное приложение на **Jetpack Compose** для курса мобильной разработки.  
Отмечайте привычки за день, смотрите серию (streak), получайте напоминание в 20:00, синхронизируйте данные через **Firestore** после входа **Яндекс ID**.

## Скриншоты

Добавьте PNG в `docs/screenshots/` (см. `docs/screenshots/README.md`):

| Файл | Экран |
|------|--------|
| `01_list.png` | Список привычек |
| `02_add.png` | Добавление + ML Kit |
| `03_settings.png` | Настройки |
| `04_notification.png` | Уведомление |

## Сборка и запуск

1. Android Studio, JDK 17+, Android SDK 36.
2. Скопируйте `local.properties.example` → `local.properties`, укажите `sdk.dir` и `yandex.client.id`.
3. Firebase: скачайте `google-services.json` в `app/` (шаблон — `app/google-services.json.example`).  
   **Обязательно два Android-приложения** в одном Firebase-проекте:
   - `com.example.semka_6sem` (full)
   - `com.example.semka_6sem.demo` (demo)  
   Иначе при запуске **demoDebug** ошибка: *No matching client found for package name …demo*.  
   В консоли: Project settings → Your apps → **Add app** → Android → package `com.example.semka_6sem.demo` → скачать **новый** `google-services.json` (в файле два блока `client`).
4. Синхронизируйте Gradle, запустите конфигурацию **demoDebug** или **fullDebug** на эмуляторе API 24+.

```bash
.\gradlew.bat assembleDemoDebug      # тестовые привычки при первом запуске
.\gradlew.bat assembleFullRelease    # release APK для сдачи
```

APK для Releases: см. папку `releases/`.

## Flavors

| Flavor | applicationId | Отличие |
|--------|---------------|---------|
| **demo** | `com.example.semka_6sem.demo` | При первом запуске 3 тестовые привычки |
| **full** | `com.example.semka_6sem` | Пустой список |

В настройках отображаются `FLAVOR_LABEL` и `BACKEND_URL` из `BuildConfig` (разные URL по flavor).

## Функции

- Список привычек, отметка «сегодня», счётчик streak.
- Добавление вручную или **ML Kit** (текст с фото).
- Настройки: уведомления, Яндекс, удаление аккаунта (очистка Room + Firestore + выход).
- Поделиться списком (через `ContentProvider`).
- Фон: **WorkManager** (20:00), **BootReceiver**, **FCM**.

---

## Критерии семестровой — где в коде

### Обязательные (15 баллов)

| Критерий | Баллы | Где в проекте |
|----------|-------|----------------|
| **Чистая архитектура** | 5 | Модули `:domain`, `:data`, `:app`. Use case: `domain/.../usecase/`. Репозитории: `domain/.../repository/`, реализация `data/.../repository/HabitRepositoryImpl.kt`. Маппинг: `data/.../mapper/HabitMapper.kt` (Entity/DTO → Domain). **Hilt**: `SemkaApplication`, `data/.../di/`. |
| **WorkManager + Receiver/Service** | 3 | `worker/HabitReminderWorker.kt`, `util/ReminderScheduler.kt`, `receiver/BootReceiver.kt`, `fcm/HabitsFirebaseMessagingService.kt`, `provider/HabitContentProvider.kt` |
| **Анимации Compose** | 2 | `presentation/list/HabitListScreen.kt` — `Crossfade`, `AnimatedVisibility`. `HabitCard.kt` — `animateFloatAsState`, `animateColorAsState`, `animateContentSize`. |
| **XML + Compose** | 2 | `res/layout/activity_settings.xml` + `ComposeView`, `settings/SettingsActivity.kt` |
| **Gradle** | 2 | `app/build.gradle.kts` — `buildTypes` debug/release, R8 в release; `productFlavors` demo/full, `applicationIdSuffix`, `BACKEND_URL` |
| **UX** | 1 | Состояния Loading/Error/Empty, Snackbar, валидация на экране добавления |

### Бонусные (до +5)

| Критерий | Баллы | Где в проекте |
|----------|-------|----------------|
| **Firebase FCM + Firestore** | +2 | `fcm/HabitsFirebaseMessagingService.kt`, `data/remote/FirestoreHabitDataSource.kt`, `util/FirebaseInitializer.kt` |
| **ML Kit** | +2 | `util/TextRecognitionHelper.kt`, кнопка на `presentation/add/AddHabitScreen.kt` |
| **Яндекс ID** | +1 | `yandex/YandexAuthHelper.kt`, настройки, `AuthRepository` + синхронизация `syncFromCloud()` |

**Итого:** 15 обязательных + до 5 бонусных (макс. 20).

---

## Структура модулей

```
:domain   — модели, интерфейсы репозиториев, use case (без Android UI)
:data     — Room, Firestore, DataStore, Hilt-модули
:app      — Compose UI, WorkManager, FCM, Яндекс SDK, ContentProvider
```

Зависимости направлены внутрь: `app` → `data` → `domain`.

## Автор

Слесарев Никита ФИТ-231
