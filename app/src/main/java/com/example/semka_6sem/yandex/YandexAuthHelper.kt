package com.example.semka_6sem.yandex

import androidx.activity.ComponentActivity
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk

// запускает вход через яндекс id
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
