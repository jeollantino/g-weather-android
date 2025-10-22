package com.jeollantino.gweather.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import android.content.Context
import com.jeollantino.gweather.data.local.dao.UserDao
import com.jeollantino.gweather.data.local.dao.WeatherDao
import com.jeollantino.gweather.data.local.dao.WeatherHistoryDao
import com.jeollantino.gweather.data.local.entity.User
import com.jeollantino.gweather.data.local.entity.WeatherCacheEntity
import com.jeollantino.gweather.data.local.entity.WeatherHistory

@Database(
    entities = [
        WeatherCacheEntity::class,
        User::class,
        WeatherHistory::class
    ],
    version = 5,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun userDao(): UserDao
    abstract fun weatherHistoryDao(): WeatherHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        /**
         * Get database instance without encryption (for backward compatibility)
         */
        fun getDatabase(context: Context): WeatherDatabase {
            return getDatabase(context, null)
        }

        /**
         * Get database instance with optional encryption
         * @param context Application context
         * @param supportFactory Optional SupportFactory for SQLCipher encryption
         */
        fun getDatabase(
            context: Context,
            supportFactory: SupportSQLiteOpenHelper.Factory?
        ): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()

                // Apply encryption if SupportFactory is provided
                supportFactory?.let {
                    builder.openHelperFactory(it)
                }

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Clear the database instance (useful for testing or re-initialization)
         */
        fun clearInstance() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}
