package com.example.sem6lab6.firebase

import android.util.Log
import com.example.sem6lab6.R
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RemoteConfigState(
    val welcomeBanner: String = "Добро пожаловать в Filmi from Nikitka",
    val experimentalProfileEnabled: Boolean = true
)

interface RemoteConfigService {
    val state: StateFlow<RemoteConfigState>

    fun start()
}

class FirebaseRemoteConfigService(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
) : RemoteConfigService {
    private val _state = MutableStateFlow(RemoteConfigState())
    override val state: StateFlow<RemoteConfigState> = _state.asStateFlow()

    override fun start() {
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Remote Config fetch failed", task.exception)
            }
            applyCurrentValues()
        }
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    applyCurrentValues()
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Remote Config realtime update failed", error)
            }
        })
    }

    private fun applyCurrentValues() {
        _state.value = RemoteConfigState(
            welcomeBanner = remoteConfig.getString(KEY_WELCOME_BANNER),
            experimentalProfileEnabled = remoteConfig.getBoolean(KEY_EXPERIMENTAL_PROFILE_ENABLED)
        )
    }

    private companion object {
        const val TAG = "RemoteConfigService"
        const val KEY_WELCOME_BANNER = "welcome_banner"
        const val KEY_EXPERIMENTAL_PROFILE_ENABLED = "experimental_profile_enabled"
    }
}
