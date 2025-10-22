package com.jeollantino.gweather.data.security

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "api_key_prefs"
        private const val API_KEY_NAME = "api_key"
    }

    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setApiKey(apiKey: String) {
        storeApiKey(apiKey)
    }

    fun storeApiKey(apiKey: String) {
        sharedPreferences.edit()
            .putString(API_KEY_NAME, apiKey)
            .apply()
    }

    fun getApiKey(): String? {
        return sharedPreferences.getString(API_KEY_NAME, null)
    }

    fun hasApiKey(): Boolean {
        return getApiKey() != null
    }

    fun clearApiKey() {
        sharedPreferences.edit()
            .remove(API_KEY_NAME)
            .apply()
    }
}
