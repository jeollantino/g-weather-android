package com.jeollantino.gweather.di

import android.content.Context
import com.jeollantino.gweather.BuildConfig
import com.jeollantino.gweather.data.local.database.WeatherDatabase
import com.jeollantino.gweather.data.local.dao.UserDao
import com.jeollantino.gweather.data.local.dao.WeatherDao
import com.jeollantino.gweather.data.local.dao.WeatherHistoryDao
import com.jeollantino.gweather.data.api.OpenWeatherApiService
import com.jeollantino.gweather.data.repository.AuthRepository
import com.jeollantino.gweather.data.repository.WeatherHistoryRepository
import com.jeollantino.gweather.data.repository.WeatherRepositoryImpl
import com.jeollantino.gweather.domain.repository.WeatherRepository
import com.jeollantino.gweather.security.DatabaseEncryptionManager
import com.jeollantino.gweather.security.SecurePreferencesManager
import com.jeollantino.gweather.util.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenWeatherApiService(retrofit: Retrofit): OpenWeatherApiService {
        return retrofit.create(OpenWeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiKey(): String {
        return BuildConfig.OPENWEATHER_API_KEY
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseEncryptionManager(@ApplicationContext context: Context): DatabaseEncryptionManager {
        return DatabaseEncryptionManager(context).apply {
            initializeSQLCipher()
        }
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context,
        encryptionManager: DatabaseEncryptionManager
    ): WeatherDatabase {
        // Use encrypted database with SQLCipher
        return WeatherDatabase.getDatabase(context, encryptionManager.createSupportFactory())
    }

    @Provides
    fun provideWeatherDao(database: WeatherDatabase): WeatherDao {
        return database.weatherDao()
    }

    @Provides
    fun provideUserDao(database: WeatherDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideWeatherHistoryDao(database: WeatherDatabase): WeatherHistoryDao {
        return database.weatherHistoryDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideSecurePreferencesManager(@ApplicationContext context: Context): SecurePreferencesManager {
        return SecurePreferencesManager(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: OpenWeatherApiService,
        weatherDao: WeatherDao,
        apiKey: String
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiService, weatherDao, apiKey)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        securePreferences: SecurePreferencesManager
    ): AuthRepository {
        return AuthRepository(userDao, securePreferences)
    }

    @Provides
    @Singleton
    fun provideWeatherHistoryRepository(weatherHistoryDao: WeatherHistoryDao): WeatherHistoryRepository {
        return WeatherHistoryRepository(weatherHistoryDao)
    }

    @Provides
    @Singleton
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager {
        return LocationManager(context)
    }
}
