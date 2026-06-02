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
## Скриншоты проекта
<img width="150" height="318" alt="image" src="https://github.com/user-attachments/assets/94cde3b5-3cbb-46e8-ab66-c2bc0f25249c" />

<img width="152" height="317" alt="image" src="https://github.com/user-attachments/assets/56f66ea8-3515-4ecf-a701-004a9c6a1263" />

<img width="148" height="313" alt="image" src="https://github.com/user-attachments/assets/e5fa3ed2-3aa0-478e-ba34-aa35a379b44a" />

<img width="156" height="313" alt="image" src="https://github.com/user-attachments/assets/29d8e216-87b7-4563-ad4e-d7194af76546" />

<img width="156" height="318" alt="image" src="https://github.com/user-attachments/assets/66b03453-094f-4d14-beb4-5fd027c235d1" />

---

## Код, реализующий задания
# Обязательные критерии (15 баллов)

### 1. Чистая архитектура — модули Gradle (data / domain / presentation)

**Задание:** минимум 2 модуля, разделение слоёв.

**Путь:** `settings.gradle.kts`

**Код:**
```kotlin
rootProject.name = "semka_6sem"
include(":app")
include(":domain")
include(":data")
```

---

### 2. Чистая архитектура — Use Case (изолированная бизнес-операция)

**Задание:** отдельные Use Case без Android-зависимостей.

**Путь:** `domain/src/main/kotlin/com/example/semka_6sem/domain/usecase/ToggleTodayUseCase.kt`

**Код:**
```kotlin
// переключает отметку за сегодня
class ToggleTodayUseCase(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(id: String) {
        repository.toggleToday(id)
    }
}
```

---

### 3. Чистая архитектура — репозиторий (контракт в domain, реализация в data)

**Задание:** интерфейс репозитория в domain, impl в data.

**Путь:** `domain/src/main/kotlin/com/example/semka_6sem/domain/repository/HabitRepository.kt`

**Код:**
```kotlin
// контракт хранения привычек
interface HabitRepository {
    fun observeHabits(): Flow<List<HabitWithStatus>>
    suspend fun addHabit(title: String)
    suspend fun deleteHabit(id: String)
    suspend fun toggleToday(id: String)
    suspend fun clearAll()
    suspend fun seedDemoHabitsIfEmpty()
    suspend fun syncFromCloud()
}
```

---

### 4. Маппинг Entity / DTO → Domain

**Задание:** преобразование данных слоя data в доменные модели.

**Путь:** `data/src/main/kotlin/com/example/semka_6sem/data/mapper/HabitMapper.kt`

**Код:**
```kotlin
object HabitMapper {

    fun fromEntity(entity: HabitEntity): Habit = Habit(
        id = entity.id,
        title = entity.title,
    )

    fun fromDto(dto: HabitDto): Habit = Habit(
        id = dto.id,
        title = dto.title,
    )

    fun toDto(habit: Habit): HabitDto = HabitDto(
        id = habit.id,
        title = habit.title,
    )
}
```

---

### 5. DI — Hilt (модули и точка входа)

**Задание:** внедрение зависимостей через Hilt.

**Путь:** `data/src/main/kotlin/com/example/semka_6sem/data/di/RepositoryModule.kt`

**Код:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
```

**Путь:** `app/src/main/java/com/example/semka_6sem/SemkaApplication.kt`

**Код:**
```kotlin
@HiltAndroidApp
class SemkaApplication : Application() {

    @Inject
    lateinit var habitRepository: HabitRepository
    // ...
}
```

**Путь:** `app/src/main/java/com/example/semka_6sem/presentation/list/HabitListViewModel.kt`

**Код:**
```kotlin
@HiltViewModel
class HabitListViewModel @Inject constructor(
    getHabitsUseCase: GetHabitsUseCase,
    private val toggleTodayUseCase: ToggleTodayUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
) : ViewModel() {
```

---

### 6. WorkManager — периодическая задача с Constraints

**Задание:** фоновое напоминание, ограничения на выполнение.

**Путь:** `app/src/main/java/com/example/semka_6sem/util/ReminderScheduler.kt`

**Код:**
```kotlin
fun schedule(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()
    val request = PeriodicWorkRequestBuilder<HabitReminderWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
        .setConstraints(constraints)
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        WORK_NAME,
        ExistingPeriodicWorkPolicy.UPDATE,
        request,
    )
}
```

**Путь:** `app/src/main/java/com/example/semka_6sem/worker/HabitReminderWorker.kt`

**Код:**
```kotlin
class HabitReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val enabled = entryPoint.settingsRepository().notificationsEnabled.first()
        if (enabled) {
            NotificationHelper.showReminder(applicationContext)
        }
        return Result.success()
    }
}
```

---

### 7. BroadcastReceiver — жизненный цикл после перезагрузки

**Задание:** перепланирование WorkManager после BOOT_COMPLETED.

**Путь:** `app/src/main/java/com/example/semka_6sem/receiver/BootReceiver.kt`

**Код:**
```kotlin
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderScheduler.schedule(context)
        }
    }
}
```

**Путь:** `app/src/main/AndroidManifest.xml`

**Код:**
```xml
<receiver
    android:name=".receiver.BootReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

---

### 8. Service — FCM (дополнительно к WorkManager)

**Задание:** сервис для фоновых событий (push).

**Путь:** `app/src/main/java/com/example/semka_6sem/fcm/HabitsFirebaseMessagingService.kt`

**Код:**
```kotlin
class HabitsFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        NotificationHelper.showReminder(this)
    }
}
```

**Путь:** `app/src/main/AndroidManifest.xml`

**Код:**
```xml
<service
    android:name=".fcm.HabitsFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

---

### 9. ContentProvider

**Задание:** провайдер данных для экспорта списка привычек.

**Путь:** `app/src/main/java/com/example/semka_6sem/provider/HabitContentProvider.kt`

**Код:**
```kotlin
override fun query(...): Cursor? {
    if (matcher.match(uri) != HABITS) return null
    val dao = entryPoint().habitDao()
    val habits = runBlocking { dao.observeHabits().first() }
    val cursor = MatrixCursor(arrayOf("_id", "title"))
    habits.forEach { cursor.addRow(arrayOf(it.id, it.title)) }
    return cursor
}
```

---

### 10. Анимации Compose — Crossfade и AnimatedVisibility

**Задание:** минимум 2 типа анимаций в пользовательском сценарии.

**Путь:** `app/src/main/java/com/example/semka_6sem/presentation/list/HabitListScreen.kt`

**Код:**
```kotlin
Crossfade(targetState = uiState, label = "listState") { state ->
  when (state) {
    HabitListUiState.Loading -> CircularProgressIndicator(...)
    is HabitListUiState.Success -> {
      items(state.habits, key = { it.habit.id }) { item ->
        AnimatedVisibility(
          visible = true,
          enter = fadeIn() + slideInVertically(),
          exit = fadeOut(),
        ) {
          HabitCard(...)
        }
      }
    }
  }
}
```

---

### 11. Анимации Compose — animate*AsState и animateContentSize

**Путь:** `app/src/main/java/com/example/semka_6sem/presentation/list/HabitCard.kt`

**Код:**
```kotlin
val alpha by animateFloatAsState(
    targetValue = if (item.doneToday) 0.65f else 1f,
    label = "habitAlpha",
)
val titleColor by animateColorAsState(
    targetValue = if (item.doneToday) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    },
    label = "habitColor",
)

Column(modifier = Modifier.animateContentSize()) {
    Text(text = item.habit.title, color = titleColor, ...)
}
```

---

### 12. XML-разметка + ComposeView

**Задание:** экран с XML, внутри встроен Compose.

**Путь:** `app/src/main/res/layout/activity_settings.xml`

**Код:**
```xml
<LinearLayout ...>
    <TextView
        android:text="@string/settings"
        ... />

    <androidx.compose.ui.platform.ComposeView
        android:id="@+id/settings_compose"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />
</LinearLayout>
```

**Путь:** `app/src/main/java/com/example/semka_6sem/settings/SettingsActivity.kt`

**Код:**
```kotlin
setContentView(R.layout.activity_settings)

findViewById<ComposeView>(R.id.settings_compose).setContent {
    Semka_6semTheme {
        SettingsScreen(
            uiState = state,
            onNotificationsChange = viewModel::setNotificationsEnabled,
            onYandexLogin = { yandexAuthHelper.login() },
            ...
        )
    }
}
```

---

### 13. Gradle — productFlavors и buildTypes (R8 в release)

**Задание:** debug/release, 2 flavor с разными applicationId и полями.

**Путь:** `app/build.gradle.kts`

**Код:**
```kotlin
productFlavors {
    create("demo") {
        dimension = "version"
        isDefault = true
        applicationIdSuffix = ".demo"
        buildConfigField("boolean", "IS_DEMO", "true")
        buildConfigField("String", "BACKEND_URL", "\"https://habits-demo.example.com\"")
    }
    create("full") {
        dimension = "version"
        buildConfigField("boolean", "IS_DEMO", "false")
        buildConfigField("String", "BACKEND_URL", "\"https://habits.example.com\"")
    }
}

buildTypes {
    debug { isMinifyEnabled = false }
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

---

### 14. Качество кода и UX — состояния загрузки и ошибок

**Задание:** Loading / Success / Error, Snackbar.

**Путь:** `app/src/main/java/com/example/semka_6sem/presentation/list/HabitListViewModel.kt`

**Код:**
```kotlin
sealed interface HabitListUiState {
    data object Loading : HabitListUiState
    data class Success(val habits: List<HabitWithStatus>) : HabitListUiState
    data class Error(val message: String) : HabitListUiState
}

init {
    viewModelScope.launch {
        getHabitsUseCase()
            .catch { e ->
                _uiState.value = HabitListUiState.Error(e.message ?: "error")
            }
            .collect { habits ->
                _uiState.value = HabitListUiState.Success(habits)
            }
    }
}
```

**Путь:** `app/src/main/java/com/example/semka_6sem/presentation/list/HabitListScreen.kt`

**Код:**
```kotlin
LaunchedEffect(uiState) {
    if (uiState is HabitListUiState.Error) {
        snackbar.showSnackbar(uiState.message)
    }
}
// пустой список:
Text(text = stringResource(R.string.empty_habits), ...)
```

---

## Бонусные критерии (до +5 баллов)

### 15. Firebase Firestore — облачное хранилище

**Задание:** Firestore как хранилище привычек после входа.

**Путь:** `data/src/main/kotlin/com/example/semka_6sem/data/remote/FirestoreHabitDataSource.kt`

**Код:**
```kotlin
suspend fun upsertHabit(userId: String, habit: Habit) {
    runCatching {
        val fs = firestore ?: return
        val dto = HabitMapper.toDto(habit)
        fs.collection("users").document(userId).collection("habits")
            .document(habit.id).set(dto).await()
    }
}
```

---

### 16. Firebase Cloud Messaging (FCM)

**Задание:** push-уведомления.

**Путь:** `app/src/main/java/com/example/semka_6sem/fcm/HabitsFirebaseMessagingService.kt`

**Код:**
```kotlin
override fun onMessageReceived(message: RemoteMessage) {
    NotificationHelper.showReminder(this)
}
```

**Путь:** `app/src/main/java/com/example/semka_6sem/util/FirebaseInitializer.kt`

**Код:**
```kotlin
FirebaseMessaging.getInstance().subscribeToTopic("habits_reminders")
```

---

### 17. ML Kit — распознавание текста с фото (бонус «ИИ»)

**Задание:** on-device ML в сценарии добавления привычки.

**Путь:** `app/src/main/java/com/example/semka_6sem/util/TextRecognitionHelper.kt`

**Код:**
```kotlin
suspend fun recognize(context: Context, uri: Uri): String {
    val image = InputImage.fromFilePath(context, uri)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    return try {
        recognizer.process(image).await().text
    } finally {
        recognizer.close()
    }
}
```

**Путь:** `app/src/main/java/com/example/semka_6sem/presentation/add/AddHabitScreen.kt`

**Код:**
```kotlin
val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    scope.launch {
        val text = TextRecognitionHelper.recognize(context, uri)
        onScannedText(text)
    }
}
Button(onClick = { picker.launch("image/*") }) {
    Text(stringResource(R.string.scan_text))
}
```

---

### 18. Вход через Яндекс ID (бонус «внешний сервис»)

**Задание:** OAuth / вход через Yandex.

**Путь:** `app/src/main/java/com/example/semka_6sem/yandex/YandexAuthHelper.kt`

**Код:**
```kotlin
class YandexAuthHelper(
    activity: ComponentActivity,
    private val sdk: YandexAuthSdk,
    private val onSuccess: (String) -> Unit,
) {
    private val launcher = activity.registerForActivityResult(sdk.contract) { result ->
        if (result is YandexAuthResult.Success) {
            onSuccess(result.token.value)
        }
    }

    fun login() {
        launcher.launch(YandexAuthLoginOptions())
    }
}
```

**Путь:** `app/src/main/java/com/example/semka_6sem/presentation/settings/SettingsViewModel.kt`

**Код:**
```kotlin
fun onYandexLoginSuccess(userId: String) {
    viewModelScope.launch {
        authRepository.setUserId(userId)
        habitRepository.syncFromCloud()
    }
}
```

---

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
