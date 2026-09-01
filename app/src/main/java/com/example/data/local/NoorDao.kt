package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface NoorDao {
    // Favorites
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(item: FavoriteItemEntity): Long

    @Delete
    suspend fun deleteFavorite(item: FavoriteItemEntity)

    @Query("DELETE FROM favorites WHERE title = :title AND arabicText = :arabicText")
    suspend fun deleteFavoriteByContent(title: String, arabicText: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE title = :title OR arabicText = :arabicText)")
    fun isFavorite(title: String, arabicText: String): Flow<Boolean>

    // Prayer Records
    @Query("SELECT * FROM prayer_records WHERE date = :date")
    fun getPrayerRecordsForDate(date: String): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records WHERE date = :date")
    suspend fun getPrayerRecordsForDateOnce(date: String): List<PrayerRecordEntity>

    @Query("SELECT * FROM prayer_records")
    fun getAllPrayerRecords(): Flow<List<PrayerRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayer(record: PrayerRecordEntity)

    // Daily Habits
    @Query("SELECT * FROM daily_habits")
    fun getAllHabits(): Flow<List<DailyHabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: DailyHabitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<DailyHabitEntity>)

    @Update
    suspend fun updateHabit(habit: DailyHabitEntity)

    @Delete
    suspend fun deleteHabit(habit: DailyHabitEntity)

    // Reading Progress
    @Query("SELECT * FROM reading_progress WHERE id = 1 LIMIT 1")
    fun getReadingProgress(): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingProgress(progress: ReadingProgressEntity)

    // Tasbih Records
    @Query("SELECT * FROM tasbih_records")
    fun getAllTasbihRecords(): Flow<List<TasbihRecordEntity>>

    @Query("SELECT * FROM tasbih_records WHERE dhikrName = :name LIMIT 1")
    suspend fun getTasbihRecord(name: String): TasbihRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasbihRecord(record: TasbihRecordEntity)

    // Khatma Active Plan
    @Query("SELECT * FROM khatma_plans WHERE id = 1 LIMIT 1")
    fun getActiveKhatmaPlan(): Flow<KhatmaPlanEntity?>

    @Query("SELECT * FROM khatma_plans WHERE id = 1 LIMIT 1")
    suspend fun getActiveKhatmaPlanOnce(): KhatmaPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveKhatmaPlan(plan: KhatmaPlanEntity)

    @Query("DELETE FROM khatma_plans WHERE id = 1")
    suspend fun deleteActiveKhatmaPlan()

    // Khatma History
    @Query("SELECT * FROM khatma_history ORDER BY completedAtTimestamp DESC")
    fun getAllKhatmaHistory(): Flow<List<KhatmaHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKhatmaHistory(history: KhatmaHistoryEntity): Long

    // Qada Records
    @Query("SELECT * FROM qada_records")
    fun getAllQadaRecords(): Flow<List<QadaRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQadaRecord(record: QadaRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQadaRecords(records: List<QadaRecordEntity>)

    // Fast Logs
    @Query("SELECT * FROM fast_logs ORDER BY createdAt ASC")
    fun getAllFastLogs(): Flow<List<FastLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFastLog(fastLog: FastLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFastLogs(fastLogs: List<FastLogEntity>)

    @Update
    suspend fun updateFastLog(fastLog: FastLogEntity)

    @Delete
    suspend fun deleteFastLog(fastLog: FastLogEntity)

    // Streak Daily Logs
    @Query("SELECT * FROM streak_daily_logs ORDER BY date DESC")
    fun getAllStreakDailyLogs(): Flow<List<StreakDailyLogEntity>>

    @Query("SELECT * FROM streak_daily_logs WHERE date = :date LIMIT 1")
    suspend fun getStreakDailyLog(date: String): StreakDailyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStreakDailyLog(log: StreakDailyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStreakDailyLogs(logs: List<StreakDailyLogEntity>)

    // Streak Summary
    @Query("SELECT * FROM streak_summary WHERE id = 1 LIMIT 1")
    fun getStreakSummary(): Flow<StreakSummaryEntity?>

    @Query("SELECT * FROM streak_summary WHERE id = 1 LIMIT 1")
    suspend fun getStreakSummaryOnce(): StreakSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStreakSummary(summary: StreakSummaryEntity)

    // Surahs (Metadata)
    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun getAllSurahsFlow(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs ORDER BY number ASC")
    suspend fun getAllSurahs(): List<SurahEntity>

    @Query("SELECT * FROM surahs WHERE number = :number LIMIT 1")
    suspend fun getSurahByNumber(number: Int): SurahEntity?

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Query("DELETE FROM surahs")
    suspend fun deleteAllSurahs()

    // Verses
    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
    fun getVersesForSurahFlow(surahNumber: Int): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
    suspend fun getVersesForSurah(surahNumber: Int): List<VerseEntity>

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getVerse(surahNumber: Int, verseNumber: Int): VerseEntity?

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getTotalVerseCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE surahNumber = :surahNumber")
    suspend fun getVerseCountForSurah(surahNumber: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    @Query("DELETE FROM verses")
    suspend fun deleteAllVerses()
}
