package com.example.data.backup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.local.AppDatabase
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
import com.example.data.local.TasbihRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupMetadata(
    val exportDate: String,
    val timestamp: Long,
    val khatmaCount: Int,
    val tasbihCount: Int,
    val favoritesCount: Int,
    val fastLogsCount: Int,
    val prayersCount: Int,
    val streakLogsCount: Int
)

data class ImportResult(
    val success: Boolean,
    val message: String,
    val itemsRestoredCount: Int = 0
)

object BackupManager {

    suspend fun generateBackupJson(
        dao: NoorDao,
        userName: String,
        userEmail: String,
        userBio: String,
        appLanguage: String
    ): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "Al-Noor")
        root.put("version", 2)
        root.put("exportTimestamp", System.currentTimeMillis())
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))

        // User Profile
        val userObj = JSONObject().apply {
            put("name", userName)
            put("email", userEmail)
            put("bio", userBio)
            put("language", appLanguage)
        }
        root.put("user_profile", userObj)

        // Reading Progress
        val readingProg = dao.getReadingProgress().firstOrNull()
        if (readingProg != null) {
            val rpObj = JSONObject().apply {
                put("surahNumber", readingProg.surahNumber)
                put("surahName", readingProg.surahName)
                put("ayahNumber", readingProg.ayahNumber)
                put("totalAyahs", readingProg.totalAyahs)
                put("updatedAt", readingProg.updatedAt)
            }
            root.put("reading_progress", rpObj)
        }

        // Active Khatma Plan
        val activeKhatma = dao.getActiveKhatmaPlanOnce()
        if (activeKhatma != null) {
            val kpObj = JSONObject().apply {
                put("title", activeKhatma.title)
                put("totalDays", activeKhatma.totalDays)
                put("startEpochDay", activeKhatma.startEpochDay)
                put("targetEndEpochDay", activeKhatma.targetEndEpochDay)
                put("dailySessionsCount", activeKhatma.dailySessionsCount)
                put("reminderEnabled", activeKhatma.reminderEnabled)
                put("reminderTime", activeKhatma.reminderTime)
                put("totalAyahs", activeKhatma.totalAyahs)
                put("readAyahsCount", activeKhatma.readAyahsCount)
                put("lastReadSurah", activeKhatma.lastReadSurah)
                put("lastReadAyah", activeKhatma.lastReadAyah)
                put("isCompleted", activeKhatma.isCompleted)
                put("completedAtEpochDay", activeKhatma.completedAtEpochDay ?: -1L)
                put("daysTaken", activeKhatma.daysTaken ?: -1)
                put("paceAdjustmentType", activeKhatma.paceAdjustmentType)
                put("completedSessionsTodayBitmask", activeKhatma.completedSessionsTodayBitmask)
                put("lastSessionDateDay", activeKhatma.lastSessionDateDay)
                put("createdAt", activeKhatma.createdAt)
                put("updatedAt", activeKhatma.updatedAt)
            }
            root.put("khatma_plan", kpObj)
        }

        // Khatma History
        val khatmaHistory = dao.getAllKhatmaHistory().firstOrNull() ?: emptyList()
        val khArray = JSONArray()
        for (kh in khatmaHistory) {
            val khObj = JSONObject().apply {
                put("title", kh.title)
                put("totalDays", kh.totalDays)
                put("daysTaken", kh.daysTaken)
                put("totalAyahsRead", kh.totalAyahsRead)
                put("startDateFormatted", kh.startDateFormatted)
                put("completionDateFormatted", kh.completionDateFormatted)
                put("completedAtTimestamp", kh.completedAtTimestamp)
            }
            khArray.put(khObj)
        }
        root.put("khatma_history", khArray)

        // Tasbih Records
        val tasbihList = dao.getAllTasbihRecords().firstOrNull() ?: emptyList()
        val tasbihArray = JSONArray()
        for (t in tasbihList) {
            val tObj = JSONObject().apply {
                put("dhikrName", t.dhikrName)
                put("currentCount", t.currentCount)
                put("targetCount", t.targetCount)
                put("totalAllTime", t.totalAllTime)
            }
            tasbihArray.put(tObj)
        }
        root.put("tasbih_records", tasbihArray)

        // Favorites
        val favList = dao.getAllFavorites().firstOrNull() ?: emptyList()
        val favArray = JSONArray()
        for (f in favList) {
            val fObj = JSONObject().apply {
                put("type", f.type)
                put("title", f.title)
                put("arabicText", f.arabicText)
                put("translation", f.translation)
                put("source", f.source)
                put("createdAt", f.createdAt)
            }
            favArray.put(fObj)
        }
        root.put("favorites", favArray)

        // Fasting Logs
        val fastList = dao.getAllFastLogs().firstOrNull() ?: emptyList()
        val fastArray = JSONArray()
        for (fast in fastList) {
            val fastObj = JSONObject().apply {
                put("type", fast.type)
                put("title", fast.title)
                put("subtitle", fast.subtitle)
                put("isCompleted", fast.isCompleted)
                put("completedDateIso", fast.completedDateIso)
                put("note", fast.note)
                put("createdAt", fast.createdAt)
            }
            fastArray.put(fastObj)
        }
        root.put("fast_logs", fastArray)

        // Qada Records
        val qadaList = dao.getAllQadaRecords().firstOrNull() ?: emptyList()
        val qadaArray = JSONArray()
        for (q in qadaList) {
            val qObj = JSONObject().apply {
                put("prayerType", q.prayerType)
                put("totalMissed", q.totalMissed)
                put("completedMadeUp", q.completedMadeUp)
                put("updatedAt", q.updatedAt)
            }
            qadaArray.put(qObj)
        }
        root.put("qada_records", qadaArray)

        // Streak Daily Logs
        val streakLogs = dao.getAllStreakDailyLogs().firstOrNull() ?: emptyList()
        val streakArray = JSONArray()
        for (s in streakLogs) {
            val sObj = JSONObject().apply {
                put("date", s.date)
                put("salatCompleted", s.salatCompleted)
                put("quranCompleted", s.quranCompleted)
                put("azkarCompleted", s.azkarCompleted)
                put("duaCompleted", s.duaCompleted)
                put("tasbihCompleted", s.tasbihCompleted)
                put("isFreezeUsed", s.isFreezeUsed)
                put("updatedAt", s.updatedAt)
            }
            streakArray.put(sObj)
        }
        root.put("streak_daily_logs", streakArray)

        // Streak Summary
        val streakSummary = dao.getStreakSummaryOnce()
        if (streakSummary != null) {
            val ssObj = JSONObject().apply {
                put("freezesRemaining", streakSummary.freezesRemaining)
                put("lastMonthReset", streakSummary.lastMonthReset)
                put("longestStreakEver", streakSummary.longestStreakEver)
                put("updatedAt", streakSummary.updatedAt)
            }
            root.put("streak_summary", ssObj)
        }

        root.toString(2)
    }

    suspend fun restoreFromJson(
        jsonString: String,
        dao: NoorDao,
        onProfileRestored: (name: String, email: String, bio: String, language: String) -> Unit
    ): ImportResult = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString.trim())
            var count = 0

            // Restore User Profile
            if (root.has("user_profile")) {
                val u = root.getJSONObject("user_profile")
                val name = u.optString("name", "")
                val email = u.optString("email", "")
                val bio = u.optString("bio", "")
                val lang = u.optString("language", "")
                if (name.isNotBlank() || email.isNotBlank()) {
                    onProfileRestored(name, email, bio, lang)
                    count++
                }
            }

            // Restore Reading Progress
            if (root.has("reading_progress")) {
                val rp = root.getJSONObject("reading_progress")
                dao.saveReadingProgress(
                    ReadingProgressEntity(
                        id = 1,
                        surahNumber = rp.getInt("surahNumber"),
                        surahName = rp.getString("surahName"),
                        ayahNumber = rp.getInt("ayahNumber"),
                        totalAyahs = rp.getInt("totalAyahs"),
                        updatedAt = rp.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
                count++
            }

            // Restore Active Khatma Plan
            if (root.has("khatma_plan")) {
                val kp = root.getJSONObject("khatma_plan")
                val completedEpochDay = kp.optLong("completedAtEpochDay", -1L)
                val daysTaken = kp.optInt("daysTaken", -1)
                dao.saveKhatmaPlan(
                    KhatmaPlanEntity(
                        id = 1,
                        title = kp.optString("title", "Personal Khatma"),
                        totalDays = kp.optInt("totalDays", 30),
                        startEpochDay = kp.optLong("startEpochDay", 0L),
                        targetEndEpochDay = kp.optLong("targetEndEpochDay", 30L),
                        dailySessionsCount = kp.optInt("dailySessionsCount", 3),
                        reminderEnabled = kp.optBoolean("reminderEnabled", true),
                        reminderTime = kp.optString("reminderTime", "07:00 AM"),
                        totalAyahs = kp.optInt("totalAyahs", 6236),
                        readAyahsCount = kp.optInt("readAyahsCount", 0),
                        lastReadSurah = kp.optInt("lastReadSurah", 1),
                        lastReadAyah = kp.optInt("lastReadAyah", 1),
                        isCompleted = kp.optBoolean("isCompleted", false),
                        completedAtEpochDay = if (completedEpochDay > 0) completedEpochDay else null,
                        daysTaken = if (daysTaken > 0) daysTaken else null,
                        paceAdjustmentType = kp.optString("paceAdjustmentType", "SPREAD"),
                        completedSessionsTodayBitmask = kp.optInt("completedSessionsTodayBitmask", 0),
                        lastSessionDateDay = kp.optLong("lastSessionDateDay", 0L),
                        createdAt = kp.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = kp.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
                count++
            }

            // Restore Khatma History
            if (root.has("khatma_history")) {
                val khArr = root.getJSONArray("khatma_history")
                for (i in 0 until khArr.length()) {
                    val kh = khArr.getJSONObject(i)
                    dao.insertKhatmaHistory(
                        KhatmaHistoryEntity(
                            id = 0,
                            title = kh.optString("title", "Khatma"),
                            totalDays = kh.optInt("totalDays", 30),
                            daysTaken = kh.optInt("daysTaken", 30),
                            totalAyahsRead = kh.optInt("totalAyahsRead", 6236),
                            startDateFormatted = kh.optString("startDateFormatted", ""),
                            completionDateFormatted = kh.optString("completionDateFormatted", ""),
                            completedAtTimestamp = kh.optLong("completedAtTimestamp", System.currentTimeMillis())
                        )
                    )
                    count++
                }
            }

            // Restore Tasbih Records
            if (root.has("tasbih_records")) {
                val tArr = root.getJSONArray("tasbih_records")
                for (i in 0 until tArr.length()) {
                    val t = tArr.getJSONObject(i)
                    dao.saveTasbihRecord(
                        TasbihRecordEntity(
                            dhikrName = t.getString("dhikrName"),
                            currentCount = t.optInt("currentCount", 0),
                            targetCount = t.optInt("targetCount", 33),
                            totalAllTime = t.optInt("totalAllTime", 0)
                        )
                    )
                    count++
                }
            }

            // Restore Favorites
            if (root.has("favorites")) {
                val fArr = root.getJSONArray("favorites")
                for (i in 0 until fArr.length()) {
                    val f = fArr.getJSONObject(i)
                    dao.insertFavorite(
                        FavoriteItemEntity(
                            id = 0,
                            type = f.optString("type", "AYAH"),
                            title = f.optString("title", ""),
                            arabicText = f.optString("arabicText", ""),
                            translation = f.optString("translation", ""),
                            source = f.optString("source", ""),
                            createdAt = f.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                    count++
                }
            }

            // Restore Fasting Logs
            if (root.has("fast_logs")) {
                val fastArr = root.getJSONArray("fast_logs")
                for (i in 0 until fastArr.length()) {
                    val fast = fastArr.getJSONObject(i)
                    dao.insertFastLog(
                        FastLogEntity(
                            id = 0,
                            type = fast.optString("type", "VOLUNTARY"),
                            title = fast.optString("title", "Fast"),
                            subtitle = fast.optString("subtitle", ""),
                            isCompleted = fast.optBoolean("isCompleted", false),
                            completedDateIso = fast.optString("completedDateIso", ""),
                            note = fast.optString("note", ""),
                            createdAt = fast.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                    count++
                }
            }

            // Restore Qada Records
            if (root.has("qada_records")) {
                val qArr = root.getJSONArray("qada_records")
                for (i in 0 until qArr.length()) {
                    val q = qArr.getJSONObject(i)
                    dao.saveQadaRecord(
                        QadaRecordEntity(
                            prayerType = q.getString("prayerType"),
                            totalMissed = q.optInt("totalMissed", 0),
                            completedMadeUp = q.optInt("completedMadeUp", 0),
                            updatedAt = q.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                    count++
                }
            }

            // Restore Streak Logs
            if (root.has("streak_daily_logs")) {
                val sArr = root.getJSONArray("streak_daily_logs")
                for (i in 0 until sArr.length()) {
                    val s = sArr.getJSONObject(i)
                    dao.saveStreakDailyLog(
                        StreakDailyLogEntity(
                            date = s.getString("date"),
                            salatCompleted = s.optBoolean("salatCompleted", false),
                            quranCompleted = s.optBoolean("quranCompleted", false),
                            azkarCompleted = s.optBoolean("azkarCompleted", false),
                            duaCompleted = s.optBoolean("duaCompleted", false),
                            tasbihCompleted = s.optBoolean("tasbihCompleted", false),
                            isFreezeUsed = s.optBoolean("isFreezeUsed", false),
                            updatedAt = s.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                    count++
                }
            }

            // Restore Streak Summary
            if (root.has("streak_summary")) {
                val ss = root.getJSONObject("streak_summary")
                dao.saveStreakSummary(
                    StreakSummaryEntity(
                        id = 1,
                        freezesRemaining = ss.optInt("freezesRemaining", 2),
                        lastMonthReset = ss.optString("lastMonthReset", "2026-08"),
                        longestStreakEver = ss.optInt("longestStreakEver", 0),
                        updatedAt = ss.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
                count++
            }

            ImportResult(
                success = true,
                message = "Successfully restored $count spiritual data records.",
                itemsRestoredCount = count
            )
        } catch (e: Exception) {
            ImportResult(
                success = false,
                message = "Invalid backup format: ${e.localizedMessage ?: "Unknown error"}",
                itemsRestoredCount = 0
            )
        }
    }

    fun shareBackup(context: Context, backupJson: String) {
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val fileName = "alnoor_backup_$dateStr.json"
        
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Al-Noor Spiritual Backup - $dateStr")
            putExtra(Intent.EXTRA_TEXT, backupJson)
        }
        val chooser = Intent.createChooser(sendIntent, "Export & Backup Al-Noor Data via (Drive, Email, Files)")
        context.startActivity(chooser)
    }

    fun copyToClipboard(context: Context, backupJson: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Al-Noor Spiritual Backup", backupJson))
    }
}
