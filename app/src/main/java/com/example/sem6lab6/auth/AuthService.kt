package com.example.sem6lab6.auth

import android.content.Intent
import androidx.activity.ComponentActivity
import com.vk.api.sdk.auth.VKAuthenticationResult

interface AuthService {
    fun createYandexLoginIntent(activity: ComponentActivity): Intent

    fun handleYandexLoginResult(
        activity: ComponentActivity,
        resultCode: Int,
        data: Intent?
    ): Result<AuthSession>

    fun handleVkLoginResult(result: VKAuthenticationResult): Result<AuthSession>
}
