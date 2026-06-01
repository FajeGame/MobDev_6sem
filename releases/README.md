# APK для сдачи

Соберите release и скопируйте сюда:

```bash
.\gradlew.bat assembleFullRelease assembleDemoRelease
```

Файлы:

- `app-full-release.apk` ← из `app/build/outputs/apk/full/release/`
- `app-demo-release.apk` ← из `app/build/outputs/apk/demo/release/`

Release подписан debug-ключом (для установки на эмулятор без настройки keystore).

Загрузите в GitHub/GitLab → Releases.
