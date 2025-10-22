package com.jeollantino.gweather.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.sqlite.db.SupportSQLiteOpenHelper
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseEncryptionManager @Inject constructor(
    private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            DB_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Get or generate database passphrase
     */
    fun getDatabasePassphrase(): ByteArray {
        var passphrase = encryptedPreferences.getString(KEY_DB_PASSPHRASE, null)

        if (passphrase == null) {
            passphrase = generateSecurePassphrase()
            encryptedPreferences.edit()
                .putString(KEY_DB_PASSPHRASE, passphrase)
                .apply()
        }

        return passphrase.toByteArray(Charsets.UTF_8)
    }

    /**
     * Create SupportFactory for Room with SQLCipher encryption
     */
    fun createSupportFactory(): SupportSQLiteOpenHelper.Factory {
        return SupportFactory(getDatabasePassphrase())
    }

    /**
     * Generate a secure random passphrase for database encryption
     */
    private fun generateSecurePassphrase(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32) // 256-bit key
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Initialize SQLCipher libraries
     */
    fun initializeSQLCipher() {
        SQLiteDatabase.loadLibs(context)
    }

    /**
     * Clear database encryption key (use with caution)
     * This will make the existing encrypted database unreadable
     */
    fun clearEncryptionKey() {
        encryptedPreferences.edit().remove(KEY_DB_PASSPHRASE).apply()
    }

    companion object {
        private const val DB_PREFS_NAME = "gweather_db_encryption"
        private const val KEY_DB_PASSPHRASE = "db_passphrase"
    }
}
