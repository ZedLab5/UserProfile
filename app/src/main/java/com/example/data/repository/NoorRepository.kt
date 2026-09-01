package com.example.data.repository

import com.example.data.local.DailyHabitEntity
import com.example.data.local.FastLogEntity
import com.example.data.local.FavoriteItemEntity
import com.example.data.local.KhatmaHistoryEntity
import com.example.data.local.KhatmaPlanEntity
import com.example.data.local.NoorDao
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.QadaRecordEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.StreakDailyLogEntity
import com.example.data.local.StreakSummaryEntity
import com.example.data.local.SurahEntity
import com.example.data.local.TasbihRecordEntity
import com.example.data.local.VerseEntity
import com.example.data.model.CalculationAuthority
import com.example.data.model.DuaItem
import com.example.data.model.PrayerTime
import com.example.data.model.PrayerZone
import com.example.data.model.Reciter
import com.example.data.model.StreakActivityType
import com.example.data.model.Surah
import com.example.data.model.SurahMeta
import com.example.data.model.UnifiedStreakData
import com.example.data.model.Verse
import com.example.data.quran.DuaData
import com.example.data.quran.QuranData
import com.example.data.quran.QuranDataLoader
import com.example.data.quran.StreakEngine
import com.example.data.prayer.PrayerCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NoorRepository(
    private val dao: NoorDao
) {
    val favorites: Flow<List<FavoriteItemEntity>> = dao.getAllFavorites()
    val habits: Flow<List<DailyHabitEntity>> = dao.getAllHabits()
    val readingProgress: Flow<ReadingProgressEntity?> = dao.getReadingProgress()
    val tasbihRecords: Flow<List<TasbihRecordEntity>> = dao.getAllTasbihRecords()
    val allPrayerRecords: Flow<List<PrayerRecordEntity>> = dao.getAllPrayerRecords()
    val activeKhatmaPlan: Flow<KhatmaPlanEntity?> = dao.getActiveKhatmaPlan()
    val khatmaHistory: Flow<List<KhatmaHistoryEntity>> = dao.getAllKhatmaHistory()
    val qadaRecords: Flow<List<QadaRecordEntity>> = dao.getAllQadaRecords()
    val fastLogs: Flow<List<FastLogEntity>> = dao.getAllFastLogs()
    val streakDailyLogs: Flow<List<StreakDailyLogEntity>> = dao.getAllStreakDailyLogs()
    val streakSummary: Flow<StreakSummaryEntity?> = dao.getStreakSummary()

    // Quran Data Access
    val allSurahsFlow: Flow<List<SurahMeta>> = dao.getAllSurahsFlow().map { entities ->
        if (entities.isNotEmpty()) {
            entities.map {
                SurahMeta(
                    number = it.number,
                    nameArabic = it.nameArabic,
                    nameEnglish = it.nameEnglish,
                    englishMeaning = it.englishMeaning,
                    totalVerses = it.totalVerses,
                    revelationType = it.revelationType
                )
            }
        } else {
            QuranData.canonicalSurahs
        }
    }

    suspend fun preloadQuranIfNeeded(context: android.content.Context) {
        QuranDataLoader.preloadQuranIfNeeded(context, dao)
    }

    suspend fun getSurahs(): List<SurahMeta> {
        val inDb = dao.getAllSurahs()
        return if (inDb.isNotEmpty()) {
            inDb.map {
                SurahMeta(
                    number = it.number,
                    nameArabic = it.nameArabic,
                    nameEnglish = it.nameEnglish,
                    englishMeaning = it.englishMeaning,
                    totalVerses = it.totalVerses,
                    revelationType = it.revelationType
                )
            }
        } else {
            QuranData.canonicalSurahs
        }
    }

    suspend fun getSurahMeta(number: Int): SurahMeta? {
        val entity = dao.getSurahByNumber(number)
        return if (entity != null) {
            SurahMeta(
                number = entity.number,
                nameArabic = entity.nameArabic,
                nameEnglish = entity.nameEnglish,
                englishMeaning = entity.englishMeaning,
                totalVerses = entity.totalVerses,
                revelationType = entity.revelationType
            )
        } else {
            QuranData.getSurahMeta(number)
        }
    }

    companion object {
        private val bismillahPrefixRegex = Regex("^[بِّسْمِ|بِسْمِ].*?ٱلرَّحِيمِ\\s*")

        fun sanitizeArabicVerseText(surahNumber: Int, verseNumber: Int, rawArabic: String): String {
            if (surahNumber != 1 && verseNumber == 1) {
                val cleaned = rawArabic.replace(bismillahPrefixRegex, "").trim()
                return if (cleaned.isNotEmpty()) cleaned else rawArabic
            }
            return rawArabic
        }
    }

    fun getVersesForSurahFlow(surahNumber: Int): Flow<List<Verse>> {
        return dao.getVersesForSurahFlow(surahNumber).map { list ->
            list.map {
                Verse(
                    surahNumber = it.surahNumber,
                    verseNumber = it.verseNumber,
                    arabicText = sanitizeArabicVerseText(it.surahNumber, it.verseNumber, it.arabicText),
                    transliteration = it.transliteration,
                    translation = it.translation,
                    tafsirShort = it.tafsirShort,
                    juz = it.juz,
                    page = it.page,
                    absoluteAyahIndex = it.absoluteNumber
                )
            }
        }
    }

    suspend fun getVersesForSurah(surahNumber: Int): List<Verse> {
        return dao.getVersesForSurah(surahNumber).map {
            Verse(
                surahNumber = it.surahNumber,
                verseNumber = it.verseNumber,
                arabicText = sanitizeArabicVerseText(it.surahNumber, it.verseNumber, it.arabicText),
                transliteration = it.transliteration,
                translation = it.translation,
                tafsirShort = it.tafsirShort,
                juz = it.juz,
                page = it.page,
                absoluteAyahIndex = it.absoluteNumber
            )
        }
    }

    suspend fun getSurahWithVerses(surahNumber: Int): Surah {
        val meta = getSurahMeta(surahNumber) ?: QuranData.getSurahMeta(surahNumber) ?: SurahMeta(
            number = surahNumber,
            nameArabic = "",
            nameEnglish = "Surah $surahNumber",
            englishMeaning = "",
            totalVerses = 0,
            revelationType = "Meccan"
        )
        val verses = getVersesForSurah(surahNumber)
        return Surah(
            number = meta.number,
            nameArabic = meta.nameArabic,
            nameEnglish = meta.nameEnglish,
            englishMeaning = meta.englishMeaning,
            totalVerses = if (verses.isNotEmpty()) verses.size else meta.totalVerses,
            revelationType = meta.revelationType,
            verses = verses
        )
    }

    suspend fun getActiveKhatmaPlanOnce(): KhatmaPlanEntity? = dao.getActiveKhatmaPlanOnce()

    suspend fun saveKhatmaPlan(plan: KhatmaPlanEntity) {
        dao.saveKhatmaPlan(plan)
    }

    suspend fun deleteActiveKhatmaPlan() {
        dao.deleteActiveKhatmaPlan()
    }

    suspend fun recordKhatmaHistory(history: KhatmaHistoryEntity) {
        dao.insertKhatmaHistory(history)
    }

    fun isFavorite(title: String, arabicText: String): Flow<Boolean> = dao.isFavorite(title, arabicText)

    suspend fun toggleFavorite(
        type: String,
        title: String,
        arabicText: String,
        translation: String,
        source: String
    ) {
        val isFav = dao.isFavorite(title, arabicText).firstOrNull() ?: false
        if (isFav) {
            dao.deleteFavoriteByContent(title, arabicText)
        } else {
            dao.insertFavorite(
                FavoriteItemEntity(
                    type = type,
                    title = title,
                    arabicText = arabicText,
                    translation = translation,
                    source = source
                )
            )
        }
    }

    suspend fun removeFavorite(item: FavoriteItemEntity) {
        dao.deleteFavorite(item)
    }

    suspend fun initDefaultHabitsIfEmpty() {
        val existing = dao.getAllHabits().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaults = listOf(
                DailyHabitEntity(
                    title = "Read 10 Verses of Quran",
                    targetCount = 10,
                    currentCount = 4,
                    isCompleted = false,
                    category = "Quran",
                    iconType = "book"
                ),
                DailyHabitEntity(
                    title = "100 Morning & Evening Tasbih",
                    targetCount = 100,
                    currentCount = 73,
                    isCompleted = false,
                    category = "Dhikr",
                    iconType = "tasbih"
                ),
                DailyHabitEntity(
                    title = "Morning & Evening Azkar",
                    targetCount = 2,
                    currentCount = 1,
                    isCompleted = false,
                    category = "Dhikr",
                    iconType = "shield"
                ),
                DailyHabitEntity(
                    title = "Pray 5 Daily Salah in Khushu",
                    targetCount = 5,
                    currentCount = 3,
                    isCompleted = false,
                    category = "Salah",
                    iconType = "mosque"
                ),
                DailyHabitEntity(
                    title = "Daily Sadaqah / Act of Kindness",
                    targetCount = 1,
                    currentCount = 1,
                    isCompleted = true,
                    category = "Charity",
                    iconType = "heart"
                )
            )
            dao.insertHabits(defaults)
        }

        val existingQada = dao.getAllQadaRecords().firstOrNull()
        if (existingQada.isNullOrEmpty()) {
            val defaultQada = listOf(
                QadaRecordEntity(prayerType = "Fajr", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Dhuhr", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Asr", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Maghrib", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Isha", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Witr", totalMissed = 0, completedMadeUp = 0)
            )
            dao.saveQadaRecords(defaultQada)
        }

        val existingFasts = dao.getAllFastLogs().firstOrNull()
        if (existingFasts.isNullOrEmpty()) {
            val defaultFasts = listOf(
                FastLogEntity(
                    type = "RAMADAN_MAKEUP",
                    title = "Ramadan Make-Up Fast #1",
                    subtitle = "Obligatory Qada for missed Ramadan day",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "MONDAY_THURSDAY",
                    title = "Monday Sunnah Fast",
                    subtitle = "Deeds are presented on Mondays (Sunnah)",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "MONDAY_THURSDAY",
                    title = "Thursday Sunnah Fast",
                    subtitle = "Deeds are presented on Thursdays (Sunnah)",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "WHITE_DAYS",
                    title = "Ayyam al-Beed (13th, 14th, 15th)",
                    subtitle = "Fasting three days of every lunar month",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "SHAWWAL",
                    title = "6 Days of Shawwal Fast #1",
                    subtitle = "Reward of fasting the entire year",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "ARAFAH",
                    title = "Day of Arafah (9th Dhul-Hijjah)",
                    subtitle = "Expiates sins of previous and coming year",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "ASHURA",
                    title = "Day of Ashura (10th Muharram)",
                    subtitle = "Expiates sins of the previous year",
                    isCompleted = false
                )
            )
            dao.insertFastLogs(defaultFasts)
        }
    }

    suspend fun updateQadaMissedCount(prayerType: String, delta: Int) {
        val existing = dao.getAllQadaRecords().firstOrNull()?.find { it.prayerType == prayerType }
        val currentMissed = existing?.totalMissed ?: 0
        val currentMadeUp = existing?.completedMadeUp ?: 0
        val newMissed = (currentMissed + delta).coerceAtLeast(0)
        dao.saveQadaRecord(
            QadaRecordEntity(
                prayerType = prayerType,
                totalMissed = newMissed,
                completedMadeUp = currentMadeUp,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun completeQadaMadeUp(prayerType: String) {
        val existing = dao.getAllQadaRecords().firstOrNull()?.find { it.prayerType == prayerType }
        val currentMissed = existing?.totalMissed ?: 0
        val currentMadeUp = existing?.completedMadeUp ?: 0
        val newMissed = (currentMissed - 1).coerceAtLeast(0)
        val newMadeUp = currentMadeUp + 1
        dao.saveQadaRecord(
            QadaRecordEntity(
                prayerType = prayerType,
                totalMissed = newMissed,
                completedMadeUp = newMadeUp,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun decrementQadaMadeUp(prayerType: String) {
        val existing = dao.getAllQadaRecords().firstOrNull()?.find { it.prayerType == prayerType }
        val currentMissed = existing?.totalMissed ?: 0
        val currentMadeUp = existing?.completedMadeUp ?: 0
        val newMadeUp = (currentMadeUp - 1).coerceAtLeast(0)
        dao.saveQadaRecord(
            QadaRecordEntity(
                prayerType = prayerType,
                totalMissed = currentMissed,
                completedMadeUp = newMadeUp,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleFastLogCompleted(fastLog: FastLogEntity) {
        val todayStr = getTodayDateString()
        val newCompleted = !fastLog.isCompleted
        val updated = fastLog.copy(
            isCompleted = newCompleted,
            completedDateIso = if (newCompleted) todayStr else ""
        )
        dao.updateFastLog(updated)
    }

    suspend fun addFastLog(title: String, type: String, subtitle: String = "", note: String = "") {
        dao.insertFastLog(
            FastLogEntity(
                type = type,
                title = title,
                subtitle = subtitle,
                isCompleted = false,
                note = note
            )
        )
    }

    suspend fun deleteFastLog(fastLog: FastLogEntity) {
        dao.deleteFastLog(fastLog)
    }

    suspend fun updateHabitProgress(habit: DailyHabitEntity, newCount: Int) {
        val count = newCount.coerceIn(0, habit.targetCount)
        val updated = habit.copy(
            currentCount = count,
            isCompleted = count >= habit.targetCount
        )
        dao.updateHabit(updated)
    }

    suspend fun addCustomHabit(title: String, targetCount: Int, category: String) {
        dao.insertHabit(
            DailyHabitEntity(
                title = title,
                targetCount = targetCount,
                currentCount = 0,
                isCompleted = false,
                category = category,
                iconType = "star"
            )
        )
    }

    suspend fun deleteHabit(habit: DailyHabitEntity) {
        dao.deleteHabit(habit)
    }

    suspend fun saveReadingProgress(surahNumber: Int, surahName: String, ayahNumber: Int, totalAyahs: Int) {
        dao.saveReadingProgress(
            ReadingProgressEntity(
                id = 1,
                surahNumber = surahNumber,
                surahName = surahName,
                ayahNumber = ayahNumber,
                totalAyahs = totalAyahs,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateTasbih(dhikrName: String, count: Int, target: Int) {
        val existing = dao.getTasbihRecord(dhikrName)
        val total = (existing?.totalAllTime ?: 0) + 1
        dao.saveTasbihRecord(
            TasbihRecordEntity(
                dhikrName = dhikrName,
                currentCount = count,
                targetCount = target,
                totalAllTime = total
            )
        )
    }

    suspend fun resetTasbih(dhikrName: String, target: Int) {
        val existing = dao.getTasbihRecord(dhikrName)
        dao.saveTasbihRecord(
            TasbihRecordEntity(
                dhikrName = dhikrName,
                currentCount = 0,
                targetCount = target,
                totalAllTime = existing?.totalAllTime ?: 0
            )
        )
    }

    fun getFajrDayLocalDate(
        zone: PrayerZone = prayerZones.first(),
        authority: CalculationAuthority = calculationAuthorities.first(),
        isHanafiAsr: Boolean = false,
        minuteOffsets: Map<String, Int> = emptyMap()
    ): LocalDate {
        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val today = LocalDate.now()
        val times = PrayerCalculator.calculatePrayerTimesList(
            zone = zone,
            authority = authority,
            isHanafiAsr = isHanafiAsr,
            date = today,
            minuteOffsets = minuteOffsets
        )
        val fajrTime = times.find { it.name == "Fajr" }
        val fajrMinutes = if (fajrTime != null) fajrTime.hour * 60 + fajrTime.minute else (5 * 60 + 15)
        return if (currentMinutes < fajrMinutes) {
            today.minusDays(1)
        } else {
            today
        }
    }

    fun getFajrDayDateString(zone: PrayerZone = prayerZones.first()): String {
        return getFajrDayLocalDate(zone).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun getTodayDateString(): String {
        return getFajrDayDateString()
    }

    fun getPrayerRecordsForToday(zone: PrayerZone = prayerZones.first()): Flow<List<PrayerRecordEntity>> {
        return dao.getPrayerRecordsForDate(getFajrDayDateString(zone))
    }

    suspend fun getCompletedPrayersForFajrDay(zone: PrayerZone = prayerZones.first()): Set<String> {
        val date = getFajrDayDateString(zone)
        val records = dao.getPrayerRecordsForDateOnce(date)
        return records.filter { it.isCompleted && !it.prayerName.equals("Sunrise", ignoreCase = true) }.map { it.prayerName }.toSet()
    }

    suspend fun setPrayerCompleted(prayerName: String, isCompleted: Boolean, zone: PrayerZone = prayerZones.first()) {
        if (prayerName.equals("Sunrise", ignoreCase = true) || prayerName.equals("الشروق", ignoreCase = true)) {
            return
        }
        val date = getFajrDayDateString(zone)
        dao.insertOrUpdatePrayer(
            PrayerRecordEntity(
                date = date,
                prayerName = prayerName,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) System.currentTimeMillis() else 0L
            )
        )
        // Synchronize with unified streak system for the same Fajr-to-Fajr day
        val todayRecords = dao.getPrayerRecordsForDateOnce(date)
        val hasAnyCompleted = todayRecords.filter { !it.prayerName.equals("Sunrise", ignoreCase = true) }.any { it.isCompleted }
        recordSalatActivity(hasAnyCompleted, date)
    }

    val prayerZones: List<PrayerZone> = listOf(
        PrayerZone("ma_tangier", "Tangier & Tetouan", "طنجة وتطوان", "Morocco", "Zone 1 (North)", 35.7595, -5.8340, "Africa/Casablanca"),
        PrayerZone("ma_casablanca", "Casablanca & Rabat", "الدار البيضاء والرباط", "Morocco", "Zone 2 (Central Coast)", 33.5731, -7.5898, "Africa/Casablanca"),
        PrayerZone("ma_fes", "Fes & Meknes", "فاس ومكناس", "Morocco", "Zone 3 (Saïss / Central)", 34.0181, -5.0078, "Africa/Casablanca"),
        PrayerZone("ma_marrakech", "Marrakech & Safi", "مراكش وآسفي", "Morocco", "Zone 4 (Haouz / South)", 31.6295, -7.9811, "Africa/Casablanca"),
        PrayerZone("ma_oujda", "Oujda & Nador", "وجدة والناظور", "Morocco", "Zone 5 (Oriental / East)", 34.6814, -1.9086, "Africa/Casablanca"),
        PrayerZone("ma_agadir", "Agadir & Souss", "أكادير وسوس", "Morocco", "Zone 6 (Souss-Massa)", 30.4278, -9.5981, "Africa/Casablanca"),
        PrayerZone("ma_laayoune", "Laayoune & Dakhla", "العيون والداخلة", "Morocco", "Zone 7 (Sahara)", 27.1536, -13.2033, "Africa/Casablanca"),
        PrayerZone("sa_makkah", "Makkah Al-Mukarramah", "مكة المكرمة", "Saudi Arabia", "Hijaz Sanctuary Zone", 21.3891, 39.8579, "Asia/Riyadh"),
        PrayerZone("sa_madinah", "Madinah Al-Munawwarah", "المدينة المنورة", "Saudi Arabia", "Prophetic City Zone", 24.5247, 39.5692, "Asia/Riyadh"),
        PrayerZone("eg_cairo", "Cairo Al-Qahira", "القاهرة", "Egypt", "Nile Valley Zone", 30.0444, 31.2357, "Africa/Cairo"),
        PrayerZone("tr_istanbul", "Istanbul", "إسطنبول", "Turkey", "Bosphorus / Marmara Zone", 41.0082, 28.9784, "Europe/Istanbul"),
        PrayerZone("ae_dubai", "Dubai & Abu Dhabi", "دبي وأبوظبي", "United Arab Emirates", "Gulf Arabian Zone", 25.2048, 55.2708, "Asia/Dubai"),
        PrayerZone("gb_london", "London", "لندن", "United Kingdom", "Western Europe / UK Zone", 51.5074, -0.1278, "Europe/London"),
        PrayerZone("fr_paris", "Paris", "باريس", "France", "Central Europe Zone", 48.8566, 2.3522, "Europe/Paris"),
        PrayerZone("us_newyork", "New York", "نيويورك", "United States", "North America Eastern", 40.7128, -74.0060, "America/New_York")
    )

    val calculationAuthorities: List<CalculationAuthority> = listOf(
        CalculationAuthority("habous", "Moroccan Ministry of Habous & Islamic Affairs", "Official Kingdom of Morocco timing standard", 19.0, 17.0),
        CalculationAuthority("mwl", "Muslim World League (MWL)", "Standard international calculation method", 18.0, 17.0),
        CalculationAuthority("umm_al_qura", "Umm Al-Qura University, Makkah", "Official Saudi Arabia timing standard", 18.5, 0.0, ishaIntervalMinutes = 90),
        CalculationAuthority("egypt", "Egyptian General Authority of Survey", "Egypt, Africa and parts of Arab world", 19.5, 17.5),
        CalculationAuthority("isna", "Islamic Society of North America (ISNA)", "United States & Canada standard", 15.0, 15.0),
        CalculationAuthority("diyanet", "Diyanet İşleri Başkanlığı (Turkey)", "Republic of Turkey standard", 18.0, 17.0),
        CalculationAuthority("karachi", "University of Islamic Sciences, Karachi", "Pakistan, Bangladesh, India & Afghanistan", 18.0, 18.0)
    )

    fun calculatePrayerTimes(
        zone: PrayerZone = prayerZones.first(),
        authority: CalculationAuthority = calculationAuthorities.first(),
        isHanafiAsr: Boolean = false,
        date: LocalDate = LocalDate.now(),
        minuteOffsets: Map<String, Int> = emptyMap()
    ): List<PrayerTime> {
        return PrayerCalculator.calculatePrayerTimesList(
            zone = zone,
            authority = authority,
            isHanafiAsr = isHanafiAsr,
            date = date,
            minuteOffsets = minuteOffsets
        )
    }

    // Static data helpers
    fun getReciters(): List<Reciter> = QuranData.reciters
    fun getDailyAyah(): DuaItem = DuaData.dailyAyah
    fun getDailyDua(): DuaItem = DuaData.dailyDua
    fun getCategorizedDuas(): List<DuaItem> = DuaData.categorizedDuas

    // ============================================================
    // UNIFIED STREAK SYSTEM METHODS
    // ============================================================

    suspend fun initDefaultStreaksIfEmpty() {
        val existingLogs = dao.getAllStreakDailyLogs().firstOrNull()
        if (existingLogs.isNullOrEmpty()) {
            val initialLogs = StreakEngine.generateInitialSeedLogs()
            dao.saveStreakDailyLogs(initialLogs)
            val initialSummary = StreakSummaryEntity(
                id = 1,
                freezesRemaining = 2,
                lastMonthReset = StreakEngine.getCurrentMonthString(),
                longestStreakEver = 14
            )
            dao.saveStreakSummary(initialSummary)
        } else {
            // Check if month changed to reset freezes
            val currentMonth = StreakEngine.getCurrentMonthString()
            val summary = dao.getStreakSummaryOnce()
            if (summary == null) {
                dao.saveStreakSummary(
                    StreakSummaryEntity(
                        id = 1,
                        freezesRemaining = 2,
                        lastMonthReset = currentMonth,
                        longestStreakEver = 0
                    )
                )
            } else if (summary.lastMonthReset != currentMonth) {
                dao.saveStreakSummary(
                    summary.copy(
                        freezesRemaining = 2,
                        lastMonthReset = currentMonth
                    )
                )
            }
        }
    }

    suspend fun recordActivity(activityType: StreakActivityType, dateStr: String = getFajrDayDateString()) {
        val existingLog = dao.getStreakDailyLog(dateStr)
        val updatedLog = if (existingLog != null) {
            when (activityType) {
                StreakActivityType.SALAT -> existingLog.copy(salatCompleted = true, updatedAt = System.currentTimeMillis())
                StreakActivityType.QURAN -> existingLog.copy(quranCompleted = true, updatedAt = System.currentTimeMillis())
                StreakActivityType.AZKAR -> existingLog.copy(azkarCompleted = true, updatedAt = System.currentTimeMillis())
                StreakActivityType.DUA -> existingLog.copy(duaCompleted = true, updatedAt = System.currentTimeMillis())
                StreakActivityType.TASBIH -> existingLog.copy(tasbihCompleted = true, updatedAt = System.currentTimeMillis())
            }
        } else {
            StreakDailyLogEntity(
                date = dateStr,
                salatCompleted = activityType == StreakActivityType.SALAT,
                quranCompleted = activityType == StreakActivityType.QURAN,
                azkarCompleted = activityType == StreakActivityType.AZKAR,
                duaCompleted = activityType == StreakActivityType.DUA,
                tasbihCompleted = activityType == StreakActivityType.TASBIH,
                isFreezeUsed = false,
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.saveStreakDailyLog(updatedLog)
    }

    suspend fun recordSalatActivity(isCompleted: Boolean, dateStr: String = getFajrDayDateString()) {
        val existingLog = dao.getStreakDailyLog(dateStr)
        val updatedLog = if (existingLog != null) {
            existingLog.copy(salatCompleted = isCompleted, updatedAt = System.currentTimeMillis())
        } else {
            StreakDailyLogEntity(
                date = dateStr,
                salatCompleted = isCompleted,
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.saveStreakDailyLog(updatedLog)
    }

    suspend fun recordQuranActivity() = recordActivity(StreakActivityType.QURAN)
    suspend fun recordAzkarActivity() = recordActivity(StreakActivityType.AZKAR)
    suspend fun recordDuaActivity() = recordActivity(StreakActivityType.DUA)
    suspend fun recordTasbihActivity() = recordActivity(StreakActivityType.TASBIH)

    suspend fun useStreakFreeze(): Boolean {
        val todayStr = getFajrDayDateString()
        val yesterdayStr = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val summary = dao.getStreakSummaryOnce() ?: StreakSummaryEntity()
        val currentMonth = StreakEngine.getCurrentMonthString()
        val freezesLeft = if (summary.lastMonthReset != currentMonth) 2 else summary.freezesRemaining

        if (freezesLeft <= 0) return false

        // Determine if yesterday is the day to freeze or today
        val yesterdayLog = dao.getStreakDailyLog(yesterdayStr)
        val todayLog = dao.getStreakDailyLog(todayStr)

        val targetDateStr = if (yesterdayLog == null || (!yesterdayLog.salatCompleted && !yesterdayLog.quranCompleted && !yesterdayLog.azkarCompleted && !yesterdayLog.duaCompleted && !yesterdayLog.tasbihCompleted && !yesterdayLog.isFreezeUsed)) {
            yesterdayStr
        } else {
            todayStr
        }

        val targetLog = dao.getStreakDailyLog(targetDateStr)
        val newTargetLog = targetLog?.copy(isFreezeUsed = true, updatedAt = System.currentTimeMillis())
            ?: StreakDailyLogEntity(date = targetDateStr, isFreezeUsed = true, updatedAt = System.currentTimeMillis())

        dao.saveStreakDailyLog(newTargetLog)
        dao.saveStreakSummary(
            summary.copy(
                freezesRemaining = (freezesLeft - 1).coerceAtLeast(0),
                lastMonthReset = currentMonth,
                updatedAt = System.currentTimeMillis()
            )
        )
        return true
    }
}
