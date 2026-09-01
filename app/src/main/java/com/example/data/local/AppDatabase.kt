package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteItemEntity::class,
        PrayerRecordEntity::class,
        DailyHabitEntity::class,
        ReadingProgressEntity::class,
        TasbihRecordEntity::class,
        KhatmaPlanEntity::class,
        KhatmaHistoryEntity::class,
        QadaRecordEntity::class,
        FastLogEntity::class,
        StreakDailyLogEntity::class,
        StreakSummaryEntity::class,
        SurahEntity::class,
        VerseEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noorDao(): NoorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noor_database.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
