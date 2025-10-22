# GWeather Security Implementation

This document describes the security features implemented in the GWeather application to protect sensitive user data.

## Overview

GWeather implements multi-layered security using Android's best practices for data protection:

1. **Encrypted SharedPreferences** - Sensitive preferences stored with encryption
2. **Database Encryption** - Room database encrypted using SQLCipher
3. **Secure Session Management** - User sessions stored securely

## Components

### 1. SecurePreferencesManager

**Location**: `app/src/main/java/com/jeollantino/gweather/security/SecurePreferencesManager.kt`

Manages encrypted SharedPreferences using AndroidX Security Crypto library with `EncryptedSharedPreferences`.

**Encryption Scheme**:
- Master Key: AES256_GCM (Android Keystore)
- Key Encryption: AES256_SIV
- Value Encryption: AES256_GCM

**Usage Example**:
```kotlin
@Inject
lateinit var securePreferences: SecurePreferencesManager

// Store sensitive data
securePreferences.putString(SecurePreferenceKeys.AUTH_TOKEN, "token_value")
securePreferences.putLong(SecurePreferenceKeys.USER_ID, userId)

// Retrieve data
val token = securePreferences.getString(SecurePreferenceKeys.AUTH_TOKEN)
val userId = securePreferences.getLong(SecurePreferenceKeys.USER_ID, -1L)

// Clear data
securePreferences.remove(SecurePreferenceKeys.AUTH_TOKEN)
securePreferences.clear() // Clear all data
```

**Predefined Keys** (in `SecurePreferenceKeys` object):
- `AUTH_TOKEN` - Authentication token
- `USER_ID` - Current user ID
- `REFRESH_TOKEN` - Token refresh value
- `LAST_LOGIN_TIME` - Timestamp of last login
- `IS_BIOMETRIC_ENABLED` - Biometric authentication flag
- `API_KEY` - API key storage

### 2. DatabaseEncryptionManager

**Location**: `app/src/main/java/com/jeollantino/gweather/security/DatabaseEncryptionManager.kt`

Manages SQLCipher encryption for the Room database, providing transparent encryption at rest.

**Features**:
- Automatic passphrase generation using `SecureRandom` (256-bit key)
- Passphrase stored securely using `EncryptedSharedPreferences`
- SQLCipher library initialization
- Support for key rotation

**How it works**:
1. On first launch, generates a secure random passphrase
2. Stores passphrase in encrypted preferences
3. Uses passphrase to create SQLCipher `SupportFactory`
4. Room uses the factory to open encrypted database

**Usage**:
```kotlin
@Inject
lateinit var encryptionManager: DatabaseEncryptionManager

// Initialize SQLCipher (done automatically in Hilt module)
encryptionManager.initializeSQLCipher()

// Get encryption factory for Room
val factory = encryptionManager.createSupportFactory()
val database = Room.databaseBuilder(context, WeatherDatabase::class.java, "weather_database")
    .openHelperFactory(factory)
    .build()
```

### 3. Encrypted Database

**Location**: `app/src/main/java/com/jeollantino/gweather/data/local/database/WeatherDatabase.kt`

The Room database is encrypted using SQLCipher, protecting:
- User credentials (username, email, password hashes)
- Weather cache data
- Weather history records

**Configuration**:
```kotlin
// Database is automatically configured with encryption in AppModule
@Provides
@Singleton
fun provideWeatherDatabase(
    @ApplicationContext context: Context,
    encryptionManager: DatabaseEncryptionManager
): WeatherDatabase {
    return WeatherDatabase.getDatabase(context, encryptionManager.createSupportFactory())
}
```

### 4. Secure Session Management

**Location**: `app/src/main/java/com/jeollantino/gweather/data/repository/AuthRepository.kt`

User sessions are managed securely with encrypted storage:

**Features**:
- Session data stored in `EncryptedSharedPreferences`
- User ID and last login timestamp tracked
- Automatic session restoration on app restart
- Secure session clearing on logout

**Implementation**:
```kotlin
// On successful login
private fun saveSession(user: User) {
    securePreferences.putLong(SecurePreferenceKeys.USER_ID, user.id)
    securePreferences.putLong(SecurePreferenceKeys.LAST_LOGIN_TIME, System.currentTimeMillis())
}

// On app initialization
private fun restoreSession() {
    val userId = securePreferences.getLong(SecurePreferenceKeys.USER_ID, -1L)
    if (userId != -1L) {
        // Session exists - restore user state if needed
    }
}

// On logout
private fun clearSession() {
    securePreferences.remove(SecurePreferenceKeys.USER_ID)
    securePreferences.remove(SecurePreferenceKeys.LAST_LOGIN_TIME)
}
```

## Dependencies

```kotlin
// AndroidX Security Crypto
implementation("androidx.security:security-crypto:1.1.0-alpha06")

// SQLCipher for database encryption
implementation("net.zetetic:android-database-sqlcipher:4.5.4")
```

## Security Best Practices

### Password Hashing
Passwords are hashed using SHA-256 before storage:
```kotlin
private fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.toByteArray())
    return hash.joinToString("") { "%02x".format(it) }
}
```

### Data Protection Levels

| Data Type | Storage | Encryption | Protection Level |
|-----------|---------|------------|------------------|
| User credentials | SQLCipher DB | AES-256 | High |
| Session tokens | EncryptedSharedPreferences | AES-256-GCM | High |
| Weather cache | SQLCipher DB | AES-256 | Medium |
| API keys | EncryptedSharedPreferences | AES-256-GCM | High |

## Testing

When writing tests that involve encrypted storage:

```kotlin
// Mock SecurePreferencesManager
val mockSecurePrefs = mockk<SecurePreferencesManager>()
coEvery { mockSecurePrefs.putLong(any(), any()) } just Runs
coEvery { mockSecurePrefs.getLong(any(), any()) } returns -1L

// For encrypted database in tests, use in-memory database
val testDatabase = Room.inMemoryDatabaseBuilder(
    context,
    WeatherDatabase::class.java
).build()
```

## Migration Notes

### Migrating from Unencrypted Database

If upgrading an existing app with unencrypted database:

1. The app uses `.fallbackToDestructiveMigration()` which will recreate the database with encryption
2. User data will be lost during migration
3. Users will need to re-authenticate

**Future Enhancement**: Implement proper migration:
```kotlin
// Example migration strategy (not implemented)
fun migrateToEncryptedDatabase(context: Context) {
    // 1. Open old unencrypted database
    // 2. Export data
    // 3. Clear old database
    // 4. Create encrypted database with data
}
```

## Troubleshooting

### Common Issues

**Issue**: "Database is encrypted or is not a database"
- **Cause**: Passphrase mismatch or corrupted encryption key
- **Solution**: Clear app data or call `DatabaseEncryptionManager.clearEncryptionKey()` (WARNING: This will make existing database unreadable)

**Issue**: EncryptedSharedPreferences crashes on older devices
- **Cause**: AndroidX Security requires API 23+
- **Solution**: App minimum SDK is 24, so this should not occur

**Issue**: SQLCipher initialization failure
- **Cause**: Native library loading issues
- **Solution**: Ensure `initializeSQLCipher()` is called before database access (handled automatically in DI)

## Security Considerations

1. **Master Key Storage**: Master key is stored in Android Keystore, which is hardware-backed on modern devices
2. **Root Detection**: Consider adding root detection for production apps
3. **Certificate Pinning**: Consider implementing certificate pinning for API calls
4. **Biometric Authentication**: Keys are prepared for biometric auth integration
5. **ProGuard**: Ensure encryption classes are properly obfuscated in release builds

## Future Enhancements

- [ ] Biometric authentication support
- [ ] Certificate pinning for API calls
- [ ] Secure key backup/recovery mechanism
- [ ] Database encryption key rotation
- [ ] Root/tamper detection
- [ ] Secure data wipe on multiple failed auth attempts

## References

- [AndroidX Security Crypto](https://developer.android.com/reference/androidx/security/crypto/package-summary)
- [SQLCipher for Android](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/)
- [Android Keystore System](https://developer.android.com/training/articles/keystore)
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
