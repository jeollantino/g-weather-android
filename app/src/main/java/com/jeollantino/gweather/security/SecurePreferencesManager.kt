package com.jeollantino.gweather.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Store a string value securely
     */
    fun putString(key: String, value: String?) {
        encryptedPreferences.edit().putString(key, value).apply()
    }

    /**
     * Retrieve a string value securely
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return encryptedPreferences.getString(key, defaultValue)
    }

    /**
     * Store a boolean value securely
     */
    fun putBoolean(key: String, value: Boolean) {
        encryptedPreferences.edit().putBoolean(key, value).apply()
    }

    /**
     * Retrieve a boolean value securely
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return encryptedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * Store a long value securely
     */
    fun putLong(key: String, value: Long) {
        encryptedPreferences.edit().putLong(key, value).apply()
    }

    /**
     * Retrieve a long value securely
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return encryptedPreferences.getLong(key, defaultValue)
    }

    /**
     * Store an integer value securely
     */
    fun putInt(key: String, value: Int) {
        encryptedPreferences.edit().putInt(key, value).apply()
    }

    /**
     * Retrieve an integer value securely
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return encryptedPreferences.getInt(key, defaultValue)
    }

    /**
     * Remove a specific key
     */
    fun remove(key: String) {
        encryptedPreferences.edit().remove(key).apply()
    }

    /**
     * Clear all encrypted preferences
     */
    fun clear() {
        encryptedPreferences.edit().clear().apply()
    }

    /**
     * Check if a key exists
     */
    fun contains(key: String): Boolean {
        return encryptedPreferences.contains(key)
    }

    companion object {
        private const val PREFS_NAME = "gweather_secure_prefs"
    }
}

object SecurePreferenceKeys {
    const val AUTH_TOKEN = "auth_token"
    const val USER_ID = "user_id"
    const val REFRESH_TOKEN = "refresh_token"
    const val API_KEY = "api_key"
    const val LAST_LOGIN_TIME = "last_login_time"
    const val IS_BIOMETRIC_ENABLED = "is_biometric_enabled"
}
