package com.example.data.quran

import com.example.data.local.StreakDailyLogEntity
import com.example.data.local.StreakSummaryEntity
import com.example.data.model.DayStreakStatus
import com.example.data.model.UnifiedStreakData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object StreakEngine {

    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")

    fun getTodayDateString(): String = LocalDate.now().format(DATE_FORMATTER)
    fun getCurrentMonthString(): String = LocalDate.now().format(MONTH_FORMATTER)

    fun calculateStreakData(
        logsList: List<StreakDailyLogEntity>,
        summary: StreakSummaryEntity?,
        todayDate: LocalDate = LocalDate.now()
    ): UnifiedStreakData {
        val todayStr = todayDate.format(DATE_FORMATTER)
        val yesterdayStr = todayDate.minusDays(1).format(DATE_FORMATTER)
        val logsMap = logsList.associateBy { it.date }

        val todayLog = logsMap[todayStr]
        val yesterdayLog = logsMap[yesterdayStr]

        val todaySalat = todayLog?.salatCompleted == true
        val todayQuran = todayLog?.quranCompleted == true
        val todayAzkar = todayLog?.azkarCompleted == true
        val todayDua = todayLog?.duaCompleted == true
        val todayTasbih = todayLog?.tasbihCompleted == true
        val todayFrozen = todayLog?.isFreezeUsed == true
        val todayAnyDone = todaySalat || todayQuran || todayAzkar || todayDua || todayTasbih || todayFrozen

        val yesterdaySalat = yesterdayLog?.salatCompleted == true
        val yesterdayQuran = yesterdayLog?.quranCompleted == true
        val yesterdayAzkar = yesterdayLog?.azkarCompleted == true
        val yesterdayDua = yesterdayLog?.duaCompleted == true
        val yesterdayTasbih = yesterdayLog?.tasbihCompleted == true
        val yesterdayFrozen = yesterdayLog?.isFreezeUsed == true
        val yesterdayAnyDone = yesterdaySalat || yesterdayQuran || yesterdayAzkar || yesterdayDua || yesterdayTasbih || yesterdayFrozen

        // Freezes management
        val currentMonth = todayDate.format(MONTH_FORMATTER)
        val freezesRemaining = if (summary == null || summary.lastMonthReset != currentMonth) {
            2
        } else {
            summary.freezesRemaining.coerceIn(0, 2)
        }

        // Calculate Overall Streak
        val currentStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { log -> log.salatCompleted || log.quranCompleted || log.azkarCompleted || log.duaCompleted || log.tasbihCompleted || log.isFreezeUsed }
        )

        // Calculate Individual Sub-Streaks
        val salatStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.salatCompleted }
        )

        val quranStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.quranCompleted }
        )

        val azkarStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.azkarCompleted }
        )

        val duaStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.duaCompleted }
        )

        val tasbihStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.tasbihCompleted }
        )

        // Calculate Longest Streak in history
        val computedLongest = calculateAllTimeLongestStreak(logsList)
        val recordedLongest = summary?.longestStreakEver ?: 0
        val longestStreak = maxOf(currentStreak, computedLongest, recordedLongest)

        // Recent 60 Days for Calendar / Contribution Heatmap
        val recentDays = mutableListOf<DayStreakStatus>()
        for (i in 59 downTo 0) {
            val date = todayDate.minusDays(i.toLong())
            val dateStr = date.format(DATE_FORMATTER)
            val log = logsMap[dateStr]
            recentDays.add(
                DayStreakStatus(
                    date = dateStr,
                    salatCompleted = log?.salatCompleted == true,
                    quranCompleted = log?.quranCompleted == true,
                    azkarCompleted = log?.azkarCompleted == true,
                    duaCompleted = log?.duaCompleted == true,
                    tasbihCompleted = log?.tasbihCompleted == true,
                    isFreezeUsed = log?.isFreezeUsed == true
                )
            )
        }

        val todayCompletedCount = (if (todaySalat) 1 else 0) +
                (if (todayQuran) 1 else 0) +
                (if (todayAzkar) 1 else 0) +
                (if (todayDua) 1 else 0) +
                (if (todayTasbih) 1 else 0)

        val isYesterdayMissed = !yesterdayAnyDone
        val canUseFreeze = freezesRemaining > 0 && (isYesterdayMissed || !todayAnyDone)

        return UnifiedStreakData(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            salatStreak = salatStreak,
            quranStreak = quranStreak,
            azkarStreak = azkarStreak,
            duaStreak = duaStreak,
            tasbihStreak = tasbihStreak,
            freezesRemaining = freezesRemaining,
            todayCompletedCount = todayCompletedCount,
            isTodayAnyCompleted = todayAnyDone,
            todaySalatDone = todaySalat,
            todayQuranDone = todayQuran,
            todayAzkarDone = todayAzkar,
            todayDuaDone = todayDua,
            todayTasbihDone = todayTasbih,
            isYesterdayMissed = isYesterdayMissed,
            canUseFreeze = canUseFreeze,
            recentDays = recentDays
        )
    }

    private fun calculateConsecutiveStreak(
        todayDate: LocalDate,
        logsMap: Map<String, StreakDailyLogEntity>,
        predicate: (StreakDailyLogEntity) -> Boolean
    ): Int {
        val todayStr = todayDate.format(DATE_FORMATTER)
        val todayLog = logsMap[todayStr]
        val isTodayDone = todayLog != null && predicate(todayLog)

        var streak = 0
        var checkDate = if (isTodayDone) todayDate else todayDate.minusDays(1)

        while (true) {
            val dateStr = checkDate.format(DATE_FORMATTER)
            val log = logsMap[dateStr]
            if (log != null && predicate(log)) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    private fun calculateAllTimeLongestStreak(logsList: List<StreakDailyLogEntity>): Int {
        if (logsList.isEmpty()) return 0
        val sortedLogs = logsList.sortedBy { it.date }
        var maxStreak = 0
        var currentStreak = 0
        var lastDate: LocalDate? = null

        for (log in sortedLogs) {
            val isDone = log.salatCompleted || log.quranCompleted || log.azkarCompleted || log.duaCompleted || log.tasbihCompleted || log.isFreezeUsed
            if (!isDone) {
                currentStreak = 0
                lastDate = null
                continue
            }

            try {
                val date = LocalDate.parse(log.date, DATE_FORMATTER)
                if (lastDate == null || date == lastDate.plusDays(1)) {
                    currentStreak++
                } else if (date == lastDate) {
                    // duplicate date, skip
                } else {
                    currentStreak = 1
                }
                lastDate = date
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak
                }
            } catch (_: Exception) {
                // Ignore parse errors
            }
        }
        return maxStreak
    }

    fun generateInitialSeedLogs(todayDate: LocalDate = LocalDate.now()): List<StreakDailyLogEntity> {
        val list = mutableListOf<StreakDailyLogEntity>()
        val formatter = DATE_FORMATTER

        // Day 0 (Today) - In progress (Salat + Quran + Tasbih completed)
        list.add(
            StreakDailyLogEntity(
                date = todayDate.format(formatter),
                salatCompleted = true,
                quranCompleted = true,
                azkarCompleted = false,
                duaCompleted = false,
                tasbihCompleted = true,
                isFreezeUsed = false
            )
        )

        // Day -1 (Yesterday) - 4 completed
        list.add(
            StreakDailyLogEntity(
                date = todayDate.minusDays(1).format(formatter),
                salatCompleted = true,
                quranCompleted = true,
                azkarCompleted = true,
                duaCompleted = false,
                tasbihCompleted = true,
                isFreezeUsed = false
            )
        )

        // Day -2 - All 5 completed
        list.add(
            StreakDailyLogEntity(
                date = todayDate.minusDays(2).format(formatter),
                salatCompleted = true,
                quranCompleted = true,
                azkarCompleted = true,
                duaCompleted = true,
                tasbihCompleted = true,
                isFreezeUsed = false
            )
        )

        // Day -3 - 3 completed
        list.add(
            StreakDailyLogEntity(
                date = todayDate.minusDays(3).format(formatter),
                salatCompleted = true,
                quranCompleted = true,
                azkarCompleted = false,
                duaCompleted = true,
                tasbihCompleted = false,
                isFreezeUsed = false
            )
        )

        // Day -4 - 4 completed
        list.add(
            StreakDailyLogEntity(
                date = todayDate.minusDays(4).format(formatter),
                salatCompleted = true,
                quranCompleted = true,
                azkarCompleted = true,
                duaCompleted = false,
                tasbihCompleted = true,
                isFreezeUsed = false
            )
        )

        // Day -5 - 2 completed (Salat + Quran)
        list.add(
            StreakDailyLogEntity(
                date = todayDate.minusDays(5).format(formatter),
                salatCompleted = true,
                quranCompleted = true,
                azkarCompleted = false,
                duaCompleted = false,
                tasbihCompleted = false,
                isFreezeUsed = false
            )
        )

        // Scattered past active days over the last month for beautiful heatmap visualization
        val pastDaysOffsets = listOf(7L, 8L, 9L, 11L, 12L, 14L, 15L, 16L, 17L, 19L, 20L, 22L, 23L, 24L, 25L, 27L, 28L, 30L, 31L, 33L, 35L)
        for (offset in pastDaysOffsets) {
            val d = todayDate.minusDays(offset)
            list.add(
                StreakDailyLogEntity(
                    date = d.format(formatter),
                    salatCompleted = true,
                    quranCompleted = offset % 2 == 0L,
                    azkarCompleted = offset % 3 == 0L,
                    duaCompleted = offset % 4 == 0L,
                    tasbihCompleted = offset % 2 != 0L,
                    isFreezeUsed = false
                )
            )
        }

        return list
    }
}
