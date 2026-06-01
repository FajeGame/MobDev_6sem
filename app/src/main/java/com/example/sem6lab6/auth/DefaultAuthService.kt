package com.example.sem6lab6.auth

import android.content.Intent
import androidx.activity.ComponentActivity
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk

class DefaultAuthService : AuthService {
    override fun createYandexLoginIntent(activity: ComponentActivity): Intent {
        val sdk = createYandexSdk(activity)
        return sdk.createLoginIntent(YandexAuthLoginOptions.Builder().build())
    }

    override fun handleYandexLoginResult(
        activity: ComponentActivity,
        resultCode: Int,
        data: Intent?
    ): Result<AuthSession> {
        return try {
            val token = createYandexSdk(activity).extractToken(resultCode, data)
                ?: return Result.failure(IllegalStateException("Вход через Яндекс отменен"))

            Result.success(
                AuthSession(
                    token = token.value,
                    userName = "Пользователь Яндекса",
                    provider = AuthProvider.YANDEX,
                    expiresAtMillis = oneDayFromNow()
                )
            )
        } catch (error: YandexAuthException) {
            Result.failure(error)
        }
    }

    override fun handleVkLoginResult(result: VKAuthenticationResult): Result<AuthSession> {
        return when (result) {
            is VKAuthenticationResult.Success -> Result.success(
                AuthSession(
                    token = result.token.accessToken,
                    userName = "Пользователь VK",
                    provider = AuthProvider.VK,
                    expiresAtMillis = oneDayFromNow()
                )
            )
            is VKAuthenticationResult.Failed -> Result.failure(result.exception)
        }
    }

    private fun createYandexSdk(activity: ComponentActivity): YandexAuthSdk {
        return YandexAuthSdk(activity, YandexAuthOptions(activity, true))
    }

    private fun oneDayFromNow(): Long {
        return System.currentTimeMillis() + 24 * 60 * 60 * 1000L
    }
}
