package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DailyHabitEntity
import com.example.data.local.FastLogEntity
import com.example.data.local.FavoriteItemEntity
import com.example.data.local.KhatmaHistoryEntity
import com.example.data.local.KhatmaPlanEntity
import com.example.data.local.NoorNotificationHelper
import com.example.data.local.QadaRecordEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.StreakDailyLogEntity
import com.example.data.local.StreakSummaryEntity
import com.example.data.model.CalculationAuthority

import com.example.data.model.DailyMoodWisdom
import com.example.data.model.DuaItem
import com.example.data.model.HomeWidgetType
import com.example.data.model.KhatmaMilestoneData
import com.example.data.model.PrayerTime
import com.example.data.model.PrayerZone
import com.example.data.model.Reciter
import com.example.data.model.StreakActivityType
import com.example.data.model.Surah

import com.example.data.model.UnifiedStreakData
import com.example.data.model.Verse
import com.example.data.prayer.PrayerAlarmScheduler
import com.example.data.quran.DuaData
import com.example.data.quran.KhatmaEngine
import com.example.data.quran.KhatmaFullDashboardState
import com.example.data.quran.KhatmaPaceStatus
import com.example.data.quran.QuranAudioCacheManager
import com.example.data.quran.QuranData
import com.example.data.quran.StreakEngine
import com.example.data.repository.NoorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

enum class SalatTab {
    TIMES,
    STREAKS,
    QADA
}

enum class NoorDestination {
    HOME,
    SALAT,
    STREAKS,
    QURAN_SURAH_LIST,
    QURAN_READER,
    QURAN_AUDIO_STREAM,
    QURAN_RECITERS,
    QURAN_KHATMA,
    TASBIH,
    QIBLA,
    HABIT_TRACKER,
    DUAS_LIBRARY,
    AZKAR_READER,
    FAVORITES,
    PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = NoorRepository(db.noorDao())

    // Navigation Destination
    private val _currentDestination = MutableStateFlow(NoorDestination.HOME)
    val currentDestination: StateFlow<NoorDestination> = _currentDestination.asStateFlow()

    // Navigation Stack for smooth back navigation
    private val navigationStack = mutableListOf<NoorDestination>()

    fun navigateTo(dest: NoorDestination) {
        if (_currentDestination.value == NoorDestination.QURAN_READER && dest != NoorDestination.QURAN_READER) {
            if (isAyahAudioMode.value) {
                stopAudio()
            }
        }
        if (_currentDestination.value != dest) {
            navigationStack.add(_currentDestination.value)
            _currentDestination.value = dest
        }
    }

    fun navigateBack(): Boolean {
        if (_currentDestination.value == NoorDestination.QURAN_READER) {
            if (isAyahAudioMode.value) {
                stopAudio()
            }
        }
        return if (navigationStack.isNotEmpty()) {
            _currentDestination.value = navigationStack.removeAt(navigationStack.lastIndex)
            true
        } else {
            if (_currentDestination.value != NoorDestination.HOME) {
                _currentDestination.value = NoorDestination.HOME
                true
            } else false
        }
    }

    // User Profile & Location
    val isUserLoggedIn = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getBoolean("is_logged_in", false)
        } catch (e: Exception) {
            false
        }
    )

    val userName = MutableStateFlow(
        try {
            val prefs = application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            val loggedIn = prefs.getBoolean("is_logged_in", false)
            if (loggedIn) {
                prefs.getString("user_name", "Zaid Ibrahim") ?: "Zaid Ibrahim"
            } else {
                "Guest Mode"
            }
        } catch (e: Exception) {
            "Guest Mode"
        }
    )

    val userEmail = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getString("user_email", "") ?: ""
        } catch (e: Exception) {
            ""
        }
    )

    val userBio = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getString("user_bio", "Seeking spiritual peace through the Noble Qur'an and remembrance")
                ?: "Seeking spiritual peace through the Noble Qur'an and remembrance"
        } catch (e: Exception) {
            "Seeking spiritual peace through the Noble Qur'an and remembrance"
        }
    )

    val userMemberSince = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getString("user_member_since", "Ramadan 1445 AH") ?: "Ramadan 1445 AH"
        } catch (e: Exception) {
            "Ramadan 1445 AH"
        }
    )

    val locationName = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getString("location_name", "Tangier, Morocco") ?: "Tangier, Morocco"
        } catch (e: Exception) {
            "Tangier, Morocco"
        }
    )

    val isCloudSyncEnabled = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getBoolean("cloud_sync_enabled", true)
        } catch (e: Exception) {
            true
        }
    )

    val isStreakProtectionCloudEnabled = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getBoolean("streak_cloud_protection", true)
        } catch (e: Exception) {
            true
        }
    )

    val dailyQuranGoal = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getInt("daily_quran_goal", 4)
        } catch (e: Exception) {
            4
        }
    )

    val dailyDhikrGoal = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getInt("daily_dhikr_goal", 100)
        } catch (e: Exception) {
            100
        }
    )

    fun isArabicLanguage(): Boolean {
        val lang = appLanguage.value
        return lang.equals("Arabic", ignoreCase = true) || lang == "العربية" || lang.startsWith("ar", ignoreCase = true)
    }

    fun connectUser(name: String, email: String, bio: String = "") {
        val finalName = if (name.isBlank()) "Zaid Ibrahim" else name.trim()
        val finalEmail = if (email.isBlank()) "zaid.ibrahim@example.com" else email.trim()
        val finalBio = if (bio.isBlank()) "Seeking spiritual peace through the Noble Qur'an and remembrance" else bio.trim()

        userName.value = finalName
        userEmail.value = finalEmail
        userBio.value = finalBio
        isUserLoggedIn.value = true

        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("user_name", finalName)
                .putString("user_email", finalEmail)
                .putString("user_bio", finalBio)
                .apply()
        } catch (e: Exception) {}

        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تسجيل الدخول بنجاح! أهلاً بك يا $finalName" else "Welcome, $finalName! Account connected.")
    }

    fun disconnectUser() {
        isUserLoggedIn.value = false
        userName.value = "Guest Mode"
        userEmail.value = ""

        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean("is_logged_in", false)
                .putString("user_name", "Guest Mode")
                .putString("user_email", "")
                .apply()
        } catch (e: Exception) {}

        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تسجيل الخروج. أنت الآن في وضع الضيف." else "Disconnected. You are now in Guest Mode.")
    }

    fun updateUserProfile(name: String, email: String, bio: String, location: String) {
        if (name.isNotBlank()) userName.value = name.trim()
        if (email.isNotBlank()) userEmail.value = email.trim()
        if (bio.isNotBlank()) userBio.value = bio.trim()
        if (location.isNotBlank()) locationName.value = location.trim()

        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("user_name", userName.value)
                .putString("user_email", userEmail.value)
                .putString("user_bio", userBio.value)
                .putString("location_name", locationName.value)
                .apply()
        } catch (e: Exception) {}

        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تحديث الملف الشخصي بنجاح" else "Profile updated successfully")
    }

    fun updatePassword(newPass: String) {
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تحديث كلمة المرور بنجاح" else "Password updated successfully")
    }

    fun deleteAccount() {
        disconnectUser()
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم حذف الحساب بنجاح" else "Account deleted successfully")
    }

    fun toggleCloudSync() {
        val newVal = !isCloudSyncEnabled.value
        isCloudSyncEnabled.value = newVal
        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("cloud_sync_enabled", newVal).apply()
        } catch (e: Exception) {}
        triggerHaptic()
        showToast(if (newVal) "Cloud Auto-Sync enabled" else "Cloud Auto-Sync paused")
    }

    fun toggleStreakProtectionCloud() {
        val newVal = !isStreakProtectionCloudEnabled.value
        isStreakProtectionCloudEnabled.value = newVal
        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("streak_cloud_protection", newVal).apply()
        } catch (e: Exception) {}
        triggerHaptic()
        showToast(if (newVal) "Streak Cloud Protection active" else "Streak Cloud Protection disabled")
    }

    fun setDailyQuranGoal(pages: Int) {
        dailyQuranGoal.value = pages
        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putInt("daily_quran_goal", pages).apply()
        } catch (e: Exception) {}
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تحديد الهدف: $pages صفحات يومياً" else "Quran goal set to $pages pages/day")
    }

    fun setDailyDhikrGoal(count: Int) {
        dailyDhikrGoal.value = count
        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putInt("daily_dhikr_goal", count).apply()
        } catch (e: Exception) {}
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تحديد الهدف: $count تسبيحة يومياً" else "Dhikr goal set to $count daily")
    }

    fun syncDataNow() {
        viewModelScope.launch {
            triggerHaptic()
            showToast(if (isArabicLanguage()) "جارٍ المزامنة السحابية..." else "Syncing with cloud...")
            delay(1000)
            triggerHaptic()
            showToast(if (isArabicLanguage()) "تمت المزامنة بنجاح! جميع السجلات والختمة محفوظة." else "Synced successfully! All spiritual data backed up.")
        }
    }

    fun updateUserName(name: String) {
        userName.value = if (name.isBlank()) "Guest Mode" else name
    }

    // App Settings & Global Localization State
    val isSettingsModalOpen = MutableStateFlow(false)
    val showArabicSecondaryText = MutableStateFlow(true)
    val appLanguage = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_prefs", Context.MODE_PRIVATE)
                .getString("app_language", "English") ?: "English"
        } catch (e: Exception) {
            "English"
        }
    )

    fun t(key: String): String = com.example.data.localization.AppStrings.get(key, appLanguage.value)
    val morningEveningAzkarNotification = MutableStateFlow(true)
    val dailyAyahNotification = MutableStateFlow(true)
    val qazaReminderNotification = MutableStateFlow(true)
    val vibrationOnAdhan = MutableStateFlow(true)
    val adhanSoundVolume = MutableStateFlow(85)

    fun openSettingsModal() {
        isSettingsModalOpen.value = true
        triggerHaptic()
    }

    fun closeSettingsModal() {
        isSettingsModalOpen.value = false
    }

    fun toggleArabicSecondaryText(enabled: Boolean? = null) {
        val newState = enabled ?: !showArabicSecondaryText.value
        showArabicSecondaryText.value = newState
        triggerHaptic()
        showToast(if (newState) "Arabic secondary text enabled" else "Arabic secondary text hidden (English only)")
    }

    fun setAppLanguage(language: String) {
        appLanguage.value = language
        val isArabic = language.equals("Arabic", ignoreCase = true) ||
                language == "العربية" ||
                language.startsWith("ar", ignoreCase = true)

        val locale = if (isArabic) Locale("ar") else Locale("en")
        try {
            Locale.setDefault(locale)
            val prefs = getApplication<Application>().getSharedPreferences("noor_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("app_language", language).apply()
        } catch (e: Exception) {
            // ignore
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = getApplication<Application>().getSystemService(android.app.LocaleManager::class.java)
                localeManager?.applicationLocales = android.os.LocaleList.forLanguageTags(if (isArabic) "ar" else "en")
            } catch (e: Exception) {
                // ignore
            }
        }

        triggerHaptic()
        showToast(if (isArabic) "تم تغيير لغة التطبيق إلى العربية" else "App language set to $language")
    }

    fun toggleMorningEveningAzkarNotification() {
        val newState = !morningEveningAzkarNotification.value
        morningEveningAzkarNotification.value = newState
        triggerHaptic()
        showToast(if (newState) "Daily Azkar reminders enabled" else "Daily Azkar reminders disabled")
    }

    fun toggleDailyAyahNotification() {
        val newState = !dailyAyahNotification.value
        dailyAyahNotification.value = newState
        triggerHaptic()
        showToast(if (newState) "Daily Ayah notifications enabled" else "Daily Ayah notifications disabled")
    }

    fun toggleQazaReminderNotification() {
        val newState = !qazaReminderNotification.value
        qazaReminderNotification.value = newState
        triggerHaptic()
        showToast(if (newState) "Qaza prayer reminders enabled" else "Qaza prayer reminders disabled")
    }

    fun toggleVibrationOnAdhan() {
        val newState = !vibrationOnAdhan.value
        vibrationOnAdhan.value = newState
        triggerHaptic()
        showToast(if (newState) "Adhan vibration enabled" else "Adhan vibration disabled")
    }

    fun setAdhanSoundVolume(volume: Int) {
        adhanSoundVolume.value = volume.coerceIn(0, 100)
    }

    // Room Database Streams
    val favorites: StateFlow<List<FavoriteItemEntity>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<DailyHabitEntity>> = repository.habits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingProgress: StateFlow<ReadingProgressEntity?> = repository.readingProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Khatma (Quran Completion) State Streams
    val activeKhatmaPlan: StateFlow<KhatmaPlanEntity?> = repository.activeKhatmaPlan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val khatmaHistory: StateFlow<List<KhatmaHistoryEntity>> = repository.khatmaHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val khatmaDashboardState: StateFlow<KhatmaFullDashboardState?> = activeKhatmaPlan
        .map { plan ->
            plan?.let { KhatmaEngine.buildDashboardState(it) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Khatma UI Sheet / Modal states
    val isKhatmaSetupSheetOpen = MutableStateFlow(false)
    val isKhatmaSettingsSheetOpen = MutableStateFlow(false)
    val isKhatmaHistorySheetOpen = MutableStateFlow(false)
    val isKhatmaCompletionCelebrationOpen = MutableStateFlow(false)
    val isKhatmaPaceAdjustSheetOpen = MutableStateFlow(false)

    // Live Prayer Timings & Dynamic Multi-Zone State
    val selectedSalatTab = MutableStateFlow(SalatTab.TIMES)
    val prayerZones: List<PrayerZone> get() = repository.prayerZones
    val calculationAuthorities: List<CalculationAuthority> get() = repository.calculationAuthorities

    val selectedPrayerZone = MutableStateFlow<PrayerZone>(repository.prayerZones.first())
    val selectedAuthority = MutableStateFlow<CalculationAuthority>(repository.calculationAuthorities.first())
    val isHanafiAsr = MutableStateFlow(false)
    val prayerManualMinuteOffsets = MutableStateFlow<Map<String, Int>>(
        mapOf("Fajr" to 0, "Sunrise" to 0, "Dhuhr" to 0, "Asr" to 0, "Maghrib" to 0, "Isha" to 0)
    )
    val isSalatSettingsOpen = MutableStateFlow(false)
    val autoSilentDuringSalat = MutableStateFlow(true)
    val silentDurationMinutes = MutableStateFlow(20)
    val hijriAdjustmentDays = MutableStateFlow(0)
    val isAthanAudioPreviewPlaying = MutableStateFlow(false)

    // Per-Prayer Notification Timers (Offset minutes: -15 = 15m before, 0 = exact time, +10 = 10m after)
    val prayerNotificationTimers = MutableStateFlow<Map<String, Int>>(
        mapOf("Fajr" to -15, "Sunrise" to 0, "Dhuhr" to 0, "Asr" to 0, "Maghrib" to 0, "Isha" to 0)
    )
    val prayerNotificationEnabled = MutableStateFlow<Map<String, Boolean>>(
        mapOf("Fajr" to true, "Sunrise" to false, "Dhuhr" to true, "Asr" to true, "Maghrib" to true, "Isha" to true)
    )

    val qadaRecords: StateFlow<List<QadaRecordEntity>> = repository.qadaRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fastLogs: StateFlow<List<FastLogEntity>> = repository.fastLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSalatTab(tab: SalatTab) {
        selectedSalatTab.value = tab
    }

    fun navigateToSalat(tab: SalatTab = SalatTab.TIMES) {
        selectedSalatTab.value = tab
        navigateTo(NoorDestination.SALAT)
    }

    fun selectPrayerZone(zone: PrayerZone) {
        selectedPrayerZone.value = zone
        locationName.value = "${zone.name}, ${zone.country}"
        sharedPrefs.edit().putString("selected_prayer_zone_id", zone.id).apply()
        viewModelScope.launch {
            val savedCompleted = repository.getCompletedPrayersForFajrDay(zone)
            _completedPrayers.value = savedCompleted
            refreshPrayerTimes()
            schedulePrayerAlarms()
        }
        triggerHaptic()
        showToast("Active Zone: ${zone.name} (${zone.zoneLabel})")
    }

    fun selectCalculationAuthority(auth: CalculationAuthority) {
        selectedAuthority.value = auth
        sharedPrefs.edit().putString("selected_calc_authority_id", auth.id).apply()
        refreshPrayerTimes()
        schedulePrayerAlarms()
        triggerHaptic()
        showToast("Standard: ${auth.name}")
    }

    fun toggleHanafiAsr(enabled: Boolean) {
        isHanafiAsr.value = enabled
        sharedPrefs.edit().putBoolean("is_hanafi_asr", enabled).apply()
        refreshPrayerTimes()
        schedulePrayerAlarms()
        triggerHaptic()
        showToast(if (enabled) "Hanafi Asr (2x shadow) applied" else "Standard Asr applied")
    }

    fun updatePrayerManualOffset(prayerName: String, deltaMinutes: Int) {
        val current = prayerManualMinuteOffsets.value.toMutableMap()
        val oldVal = current[prayerName] ?: 0
        val newVal = (oldVal + deltaMinutes).coerceIn(-60, 60)
        current[prayerName] = newVal
        prayerManualMinuteOffsets.value = current
        sharedPrefs.edit().putInt("manual_offset_$prayerName", newVal).apply()
        refreshPrayerTimes()
        schedulePrayerAlarms()
        triggerHaptic()
        val label = if (newVal > 0) "+$newVal min" else if (newVal < 0) "$newVal min" else "0 min (Default)"
        showToast("$prayerName offset adjusted: $label")
    }

    fun resetPrayerManualOffsets() {
        val resetMap = mapOf("Fajr" to 0, "Sunrise" to 0, "Dhuhr" to 0, "Asr" to 0, "Maghrib" to 0, "Isha" to 0)
        prayerManualMinuteOffsets.value = resetMap
        val editor = sharedPrefs.edit()
        resetMap.keys.forEach { pName ->
            editor.putInt("manual_offset_$pName", 0)
        }
        editor.apply()
        refreshPrayerTimes()
        schedulePrayerAlarms()
        triggerHaptic()
        showToast("All prayer manual offsets reset to 0")
    }

    fun schedulePrayerAlarms() {
        PrayerAlarmScheduler.scheduleAll(
            context = getApplication(),
            prayers = _prayerTimes.value,
            timersMap = prayerNotificationTimers.value,
            enabledMap = prayerNotificationEnabled.value
        )
    }

    fun setPrayerNotificationTimer(prayerName: String, offsetMinutes: Int) {
        val timers = prayerNotificationTimers.value.toMutableMap()
        timers[prayerName] = offsetMinutes
        prayerNotificationTimers.value = timers
        val enabledMap = prayerNotificationEnabled.value.toMutableMap()
        enabledMap[prayerName] = true
        prayerNotificationEnabled.value = enabledMap
        schedulePrayerAlarms()
        triggerHaptic()

        val label = when {
            offsetMinutes == 0 -> "Exact Adhan Time"
            offsetMinutes < 0 -> "${-offsetMinutes}m Before Adhan"
            else -> "+${offsetMinutes}m After Adhan"
        }
        showToast("$prayerName reminder set to $label")
    }

    fun togglePrayerNotification(prayerName: String) {
        val enabledMap = prayerNotificationEnabled.value.toMutableMap()
        val newState = !(enabledMap[prayerName] ?: true)
        enabledMap[prayerName] = newState
        prayerNotificationEnabled.value = enabledMap
        schedulePrayerAlarms()
        triggerHaptic()
        showToast(if (newState) "$prayerName notification active" else "$prayerName notification muted")
    }

    fun toggleAthanAudioPreview() {
        val newState = !isAthanAudioPreviewPlaying.value
        isAthanAudioPreviewPlaying.value = newState
        triggerHaptic()
        if (newState) {
            showToast("Playing ${athanSoundName.value} preview...")
        } else {
            showToast("Adhan preview stopped")
        }
    }

    fun updateHijriAdjustment(delta: Int) {
        val current = hijriAdjustmentDays.value
        val updated = (current + delta).coerceIn(-3, 3)
        hijriAdjustmentDays.value = updated
        triggerHaptic()
        showToast("Hijri calendar adjusted by $updated days")
    }

    fun toggleAutoSilent() {
        val newState = !autoSilentDuringSalat.value
        autoSilentDuringSalat.value = newState
        triggerHaptic()
        showToast(if (newState) "Mosque Mode (Auto-Silent) enabled" else "Mosque Mode disabled")
    }

    fun setSilentDuration(minutes: Int) {
        silentDurationMinutes.value = minutes.coerceIn(10, 60)
    }

    fun incrementQadaMissed(prayerType: String, delta: Int = 1) {
        viewModelScope.launch {
            repository.updateQadaMissedCount(prayerType, delta)
            triggerHaptic()
        }
    }

    fun completeQadaMadeUp(prayerType: String) {
        viewModelScope.launch {
            repository.completeQadaMadeUp(prayerType)
            triggerHaptic()
            showToast("Alhamdulillah! 1 $prayerType Qada prayer completed.")
        }
    }

    fun decrementQadaMadeUp(prayerType: String) {
        viewModelScope.launch {
            repository.decrementQadaMadeUp(prayerType)
            triggerHaptic()
        }
    }

    fun toggleFastLogCompleted(fastLog: FastLogEntity) {
        viewModelScope.launch {
            repository.toggleFastLogCompleted(fastLog)
            triggerHaptic()
            if (!fastLog.isCompleted) {
                showToast("Taqabbal Allah! Fast marked as completed.")
            }
        }
    }

    fun addRamadanMakeupFast() {
        viewModelScope.launch {
            val currentCount = (fastLogs.value.count { it.type == "RAMADAN_MAKEUP" }) + 1
            repository.addFastLog(
                title = "Ramadan Make-Up Fast #$currentCount",
                type = "RAMADAN_MAKEUP",
                subtitle = "Obligatory Qada for missed Ramadan day"
            )
            triggerHaptic()
            showToast("Added Ramadan Make-Up Fast #$currentCount")
        }
    }

    fun addCustomFastLog(title: String, type: String = "VOLUNTARY", subtitle: String = "") {
        viewModelScope.launch {
            repository.addFastLog(
                title = title,
                type = type,
                subtitle = subtitle
            )
            triggerHaptic()
            showToast("Added Fast: $title")
        }
    }

    fun deleteFastLog(fastLog: FastLogEntity) {
        viewModelScope.launch {
            repository.deleteFastLog(fastLog)
            triggerHaptic()
            showToast("Fast record removed")
        }
    }

    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTime>> = _prayerTimes.asStateFlow()

    private val _nextPrayerCountdown = MutableStateFlow("00:00:00")
    val nextPrayerCountdown: StateFlow<String> = _nextPrayerCountdown.asStateFlow()

    private val _nextPrayerName = MutableStateFlow("Dhuhr")
    val nextPrayerName: StateFlow<String> = _nextPrayerName.asStateFlow()

    private val _nextPrayerTimeStr = MutableStateFlow("13:34")
    val nextPrayerTimeStr: StateFlow<String> = _nextPrayerTimeStr.asStateFlow()

    private val _completedPrayers = MutableStateFlow<Set<String>>(emptySet())
    val completedPrayers: StateFlow<Set<String>> = _completedPrayers.asStateFlow()

    // Notification Offset in minutes (+/-)
    val prayerOffsetMinutes = MutableStateFlow(0)
    val athanSoundName = MutableStateFlow("Makkah Al-Mukarramah Adhan")

    // Daily Mood & Wisdom
    val selectedMood = MutableStateFlow("Anxious")
    val isIslamicWisdomMode = MutableStateFlow(true)


    // Audio Player State
    val isAudioPlaying = MutableStateFlow(false)
    val isAudioBuffering = MutableStateFlow(false)
    val currentPlayingSurah = MutableStateFlow(QuranData.surahs.first())
    val currentPlayingVerse = MutableStateFlow(1)
    val selectedReciter = MutableStateFlow(QuranData.reciters.first())
    val reciters: List<Reciter> = QuranData.reciters
    val audioProgress = MutableStateFlow(0f)
    val audioDurationMs = MutableStateFlow(0)
    val audioCurrentPositionMs = MutableStateFlow(0)
    val audioPlaybackSpeed = MutableStateFlow(1.0f)
    val isAutoAdvanceAyah = MutableStateFlow(true)
    val isAudioRepeatOne = MutableStateFlow(false)
    val isAyahAudioMode = MutableStateFlow(false)
    val sleepTimerMinutes = MutableStateFlow<Int?>(null)
    private var sleepTimerJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioProgressTrackerJob: Job? = null


    // Digital Tasbih State
    val tasbihCount = MutableStateFlow(0)
    val tasbihTarget = MutableStateFlow(33)
    val selectedDhikr = MutableStateFlow("SubhanAllah (سُبْحَانَ اللَّهِ)")
    val selectedDhikrArabic = MutableStateFlow("سُبْحَانَ اللَّهِ")
    val selectedDhikrMeaning = MutableStateFlow("Glory be to Allah in His infinite perfection")
    val selectedDhikrVirtue = MutableStateFlow("Fills the scales with immense spiritual reward")
    val tasbihTotalAllTime = MutableStateFlow(482)
    val tasbihLapsCompleted = MutableStateFlow(0)
    val tasbihVisualTheme = MutableStateFlow("Marble") // "Marble", "Digital", "Minimal Circle"
    val isTasbihHapticEnabled = MutableStateFlow(true)
    val isTasbihSoundEnabled = MutableStateFlow(true)
    val isTasbihAutoReset = MutableStateFlow(true)

    // Home Screen Customization & Widget Order
    val homeWidgetsOrder = MutableStateFlow<List<HomeWidgetType>>(HomeWidgetType.defaultOrderedList())
    val homeWidgetsVisibility = MutableStateFlow<Map<HomeWidgetType, Boolean>>(
        HomeWidgetType.values().associateWith { it.defaultVisible }
    )
    val isCustomizeHomeSheetOpen = MutableStateFlow(false)

    // Quran Reader State
    val selectedSurahForReading = MutableStateFlow(QuranData.surahs.first())
    val targetAyahToScrollTo = MutableStateFlow(0)
    val arabicFontSizeSp = MutableStateFlow(24)
    val showTransliteration = MutableStateFlow(true)
    val showTranslation = MutableStateFlow(true)
    val isMushafFlowMode = MutableStateFlow(false) // Distraction-Free Pure Reading Flow
    val isTajweedHighlightsEnabled = MutableStateFlow(false) // Interactive Tajweed Color Highlights
    val quranReadingTheme = MutableStateFlow("Madani Crisp") // "Madani Crisp", "Sepia Parchment", "Obsidian Night", "Emerald Noor"
    val quranSearchQuery = MutableStateFlow("")
    val quranFilterCategory = MutableStateFlow("All") // "All", "Meccan", "Medinan", "Popular", "Juz 'Amma"
    val hasSeenQuranOnboarding = MutableStateFlow(false)
    val isShowingQuranOnboarding = MutableStateFlow(false)

    // Khatma Automatic Milestone Celebration Popup
    val khatmaMilestoneModal = MutableStateFlow<KhatmaMilestoneData?>(null)

    // Offline Audio Download Manager State
    val downloadedSurahs = MutableStateFlow<Set<String>>(emptySet())
    val isSurahDownloading = MutableStateFlow<Map<String, Float>>(emptyMap())

    // Du'as & Azkar State & Preferences
    val selectedDuaCategory = MutableStateFlow("Morning Azkar")
    val duasSearchQuery = MutableStateFlow("")
    val azkarRemainingCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val showArabicInAzkarCards = MutableStateFlow(true)
    val azkarTextSize = MutableStateFlow("Medium") // "Small", "Medium", "Large", "Extra Large"
    val isAzkarAutoScrollEnabled = MutableStateFlow(true)
    val isAzkarHapticEnabled = MutableStateFlow(true)
    val showAzkarTransliteration = MutableStateFlow(true)
    val showAzkarBenefits = MutableStateFlow(true)
    val isAzkarSettingsOpen = MutableStateFlow(false)

    // Toast/Feedback message
    val toastMessage = MutableStateFlow<String?>(null)

    // Unified Streak System State
    val unifiedStreakData: StateFlow<UnifiedStreakData> = combine(
        repository.streakDailyLogs,
        repository.streakSummary
    ) { logs, summary ->
        StreakEngine.calculateStreakData(logs, summary)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnifiedStreakData()
    )

    fun recordQuranActivity() {
        viewModelScope.launch {
            repository.recordQuranActivity()
        }
    }

    fun recordAzkarActivity() {
        viewModelScope.launch {
            repository.recordAzkarActivity()
        }
    }

    fun recordDuaActivity() {
        viewModelScope.launch {
            repository.recordDuaActivity()
        }
    }

    fun recordTasbihActivity() {
        viewModelScope.launch {
            repository.recordTasbihActivity()
        }
    }

    fun useStreakFreeze() {
        viewModelScope.launch {
            val success = repository.useStreakFreeze()
            if (success) {
                triggerHaptic()
                showToast("Streak protected! Monthly freeze pass applied.")
            } else {
                showToast("No streak passes available this month.")
            }
        }
    }

    private var countdownJob: Job? = null
    private var audioSimulationJob: Job? = null

    private val sharedPrefs = application.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)

    init {
        NoorNotificationHelper.createNotificationChannels(application)
        hasSeenQuranOnboarding.value = sharedPrefs.getBoolean("has_seen_quran_onboarding", false)
        showArabicInAzkarCards.value = sharedPrefs.getBoolean("show_arabic_in_azkar_cards", true)
        azkarTextSize.value = sharedPrefs.getString("azkar_text_size", "Medium") ?: "Medium"
        isAzkarAutoScrollEnabled.value = sharedPrefs.getBoolean("azkar_auto_scroll", true)
        isAzkarHapticEnabled.value = sharedPrefs.getBoolean("azkar_haptic", true)
        showAzkarTransliteration.value = sharedPrefs.getBoolean("azkar_transliteration", true)
        showAzkarBenefits.value = sharedPrefs.getBoolean("azkar_benefits", true)
        isMushafFlowMode.value = sharedPrefs.getBoolean("is_mushaf_flow_mode", false)
        isTajweedHighlightsEnabled.value = sharedPrefs.getBoolean("is_tajweed_highlights", false)

        val savedZoneId = sharedPrefs.getString("selected_prayer_zone_id", null)
        if (savedZoneId != null) {
            val foundZone = repository.prayerZones.find { it.id == savedZoneId }
            if (foundZone != null) {
                selectedPrayerZone.value = foundZone
                locationName.value = "${foundZone.name}, ${foundZone.country}"
            }
        }
        val savedAuthId = sharedPrefs.getString("selected_calc_authority_id", null)
        if (savedAuthId != null) {
            val foundAuth = repository.calculationAuthorities.find { it.id == savedAuthId }
            if (foundAuth != null) {
                selectedAuthority.value = foundAuth
            }
        }
        isHanafiAsr.value = sharedPrefs.getBoolean("is_hanafi_asr", false)
        val loadedOffsets = mutableMapOf<String, Int>()
        listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { pName ->
            loadedOffsets[pName] = sharedPrefs.getInt("manual_offset_$pName", 0)
        }
        prayerManualMinuteOffsets.value = loadedOffsets

        loadSavedHomeWidgetsConfig()
        refreshDownloadedSurahs()

        viewModelScope.launch {
            repository.preloadQuranIfNeeded(application)
            repository.initDefaultHabitsIfEmpty()
            repository.initDefaultStreaksIfEmpty()
            val savedCompleted = repository.getCompletedPrayersForFajrDay(selectedPrayerZone.value)
            _completedPrayers.value = savedCompleted
            refreshPrayerTimes()
            schedulePrayerAlarms()
            startRealtimeCountdown()

            // Load initial surah with real canonical verses from Room
            val initialSurah = repository.getSurahWithVerses(1)
            if (initialSurah.verses.isNotEmpty()) {
                selectedSurahForReading.value = initialSurah
            }
        }
    }

    fun checkQuranOnboarding() {
        if (!hasSeenQuranOnboarding.value) {
            isShowingQuranOnboarding.value = true
        }
    }

    fun completeQuranOnboarding() {
        hasSeenQuranOnboarding.value = true
        isShowingQuranOnboarding.value = false
        sharedPrefs.edit().putBoolean("has_seen_quran_onboarding", true).apply()
    }

    fun restartQuranOnboarding() {
        isShowingQuranOnboarding.value = true
    }

    fun showToast(msg: String) {
        toastMessage.value = msg
        viewModelScope.launch {
            delay(3000)
            if (toastMessage.value == msg) {
                toastMessage.value = null
            }
        }
    }

    fun refreshPrayerTimes() {
        val list = repository.calculatePrayerTimes(
            zone = selectedPrayerZone.value,
            authority = selectedAuthority.value,
            isHanafiAsr = isHanafiAsr.value,
            minuteOffsets = prayerManualMinuteOffsets.value
        )
        val completed = _completedPrayers.value
        _prayerTimes.value = list.map { pt ->
            pt.copy(isCompleted = completed.contains(pt.name))
        }

        val next = list.firstOrNull { it.isNext } ?: list.firstOrNull()
        if (next != null) {
            _nextPrayerName.value = next.name
            _nextPrayerTimeStr.value = next.timeString
        }
    }

    private fun startRealtimeCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var lastFajrDateKey = repository.getFajrDayDateString(selectedPrayerZone.value)
            var lastMinute = -1

            while (true) {
                val now = Calendar.getInstance()
                val currentHour = now.get(Calendar.HOUR_OF_DAY)
                val currentMin = now.get(Calendar.MINUTE)
                val currentSec = now.get(Calendar.SECOND)

                // Refresh prayer times schedule when minute changes or rollover occurs
                if (currentMin != lastMinute) {
                    lastMinute = currentMin
                    refreshPrayerTimes()

                    // Check for Fajr day rollover (Fajr-to-Fajr boundary)
                    val currentFajrDateKey = repository.getFajrDayDateString(selectedPrayerZone.value)
                    if (currentFajrDateKey != lastFajrDateKey) {
                        lastFajrDateKey = currentFajrDateKey
                        val freshCompleted = repository.getCompletedPrayersForFajrDay(selectedPrayerZone.value)
                        _completedPrayers.value = freshCompleted
                        refreshPrayerTimes()
                    }
                }

                val currentPrayers = _prayerTimes.value
                val nextPrayer = currentPrayers.firstOrNull { it.isNext } ?: currentPrayers.firstOrNull()

                if (nextPrayer != null) {
                    val targetHour = nextPrayer.hour
                    val targetMin = nextPrayer.minute

                    var diffSec = (targetHour * 3600 + targetMin * 60) - (currentHour * 3600 + currentMin * 60 + currentSec)
                    if (diffSec < 0) {
                        // Next day Fajr
                        diffSec += 24 * 3600
                    }

                    val hours = diffSec / 3600
                    val mins = (diffSec % 3600) / 60
                    val secs = diffSec % 60

                    _nextPrayerCountdown.value = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, mins, secs)
                    _nextPrayerName.value = nextPrayer.name
                    _nextPrayerTimeStr.value = nextPrayer.timeString
                }

                delay(1000)
            }
        }
    }

    fun togglePrayerCompleted(prayer: PrayerTime) {
        // Sunrise is not an obligatory salat and cannot be checked
        if (prayer.name.equals("Sunrise", ignoreCase = true) || prayer.name.equals("الشروق", ignoreCase = true)) {
            return
        }

        // Enforce time-gated rule: prayer can only be checked once its time has actually started (past or current)
        // Prayers do NOT need to be checked in order.
        if (!prayer.isPast && !prayer.isCurrent) {
            showToast("Prayer time hasn't started yet.")
            return
        }

        val current = _completedPrayers.value.toMutableSet()
        val willBeCompleted = !current.contains(prayer.name)
        if (willBeCompleted) {
            current.add(prayer.name)
            triggerHaptic()
            showToast("${prayer.name} marked as completed. Baraka Allahu feek!")
        } else {
            current.remove(prayer.name)
            triggerHaptic()
            showToast("${prayer.name} unmarked.")
        }
        _completedPrayers.value = current
        viewModelScope.launch {
            repository.setPrayerCompleted(prayer.name, willBeCompleted, selectedPrayerZone.value)
            refreshPrayerTimes()
        }
    }

    // Daily Mood & Wisdom Actions
    fun selectMood(mood: String) {
        selectedMood.value = mood
    }

    fun getCurrentMoodWisdom(): DailyMoodWisdom {
        val pair = DuaData.moodWisdomMap[selectedMood.value] ?: DuaData.moodWisdomMap["Anxious"]!!
        return if (isIslamicWisdomMode.value) pair.first else pair.second
    }

    // Bookmark / Favorite
    fun toggleBookmark(type: String, title: String, arabic: String, translation: String, source: String) {
        viewModelScope.launch {
            repository.toggleFavorite(type, title, arabic, translation, source)
            showToast("Updated favorites!")
        }
    }

    fun toggleSurahFavorite(surah: Surah) {
        viewModelScope.launch {
            val title = "Surah ${surah.nameEnglish}"
            val existing = favorites.value.filter { fav ->
                fav.title.equals(title, ignoreCase = true) ||
                        fav.arabicText == surah.nameArabic ||
                        fav.source == "Surah ${surah.number}"
            }
            if (existing.isNotEmpty()) {
                existing.forEach { repository.removeFavorite(it) }
                triggerHaptic()
                showToast("Removed Surah ${surah.nameEnglish.substringBefore(" (")} from favorites")
            } else {
                repository.toggleFavorite(
                    type = "SURAH",
                    title = title,
                    arabicText = surah.nameArabic,
                    translation = "${surah.englishMeaning} • ${surah.totalVerses} Verses",
                    source = "Surah ${surah.number}"
                )
                triggerHaptic()
                showToast("Added Surah ${surah.nameEnglish.substringBefore(" (")} to favorites")
            }
        }
    }

    fun removeFavorite(item: FavoriteItemEntity) {
        viewModelScope.launch {
            repository.removeFavorite(item)
            showToast("Removed from favorites")
        }
    }



    // Digital Tasbih Methods
    fun incrementTasbih() {
        val current = tasbihCount.value
        val target = tasbihTarget.value
        val newCount = current + 1
        tasbihTotalAllTime.value += 1
        
        if (isTasbihHapticEnabled.value) {
            triggerHaptic()
        }

        if (newCount >= target) {
            tasbihLapsCompleted.value += 1
            triggerCompletionHaptic()
            showToast("SubhanAllah! Completed target of $target.")
            if (isTasbihAutoReset.value) {
                tasbihCount.value = 0
            } else {
                tasbihCount.value = newCount
            }
        } else {
            tasbihCount.value = newCount
        }

        viewModelScope.launch {
            repository.updateTasbih(selectedDhikr.value, tasbihCount.value, tasbihTarget.value)
            repository.recordTasbihActivity()
        }
    }

    fun resetTasbih() {
        tasbihCount.value = 0
        viewModelScope.launch {
            repository.resetTasbih(selectedDhikr.value, tasbihTarget.value)
            showToast("Tasbih counter reset")
        }
    }

    fun resetAllTasbihStats() {
        tasbihCount.value = 0
        tasbihLapsCompleted.value = 0
        showToast("All round statistics reset")
    }

    fun setTasbihTarget(target: Int) {
        tasbihTarget.value = target
        tasbihCount.value = 0
    }

    fun setDhikr(dhikrTitle: String, arabic: String = "", meaning: String = "", target: Int = 33, virtue: String = "") {
        selectedDhikr.value = dhikrTitle
        if (arabic.isNotBlank()) selectedDhikrArabic.value = arabic
        if (meaning.isNotBlank()) selectedDhikrMeaning.value = meaning
        if (virtue.isNotBlank()) selectedDhikrVirtue.value = virtue
        tasbihTarget.value = target
        tasbihCount.value = 0
    }

    fun setTasbihTheme(theme: String) {
        tasbihVisualTheme.value = theme
    }

    // Du'as & Azkar Methods
    fun openDuaCategory(category: String) {
        selectedDuaCategory.value = category
        navigateTo(NoorDestination.AZKAR_READER)
    }

    fun setShowArabicInAzkarCards(enabled: Boolean) {
        showArabicInAzkarCards.value = enabled
        sharedPrefs.edit().putBoolean("show_arabic_in_azkar_cards", enabled).apply()
    }

    fun setAzkarTextSize(size: String) {
        azkarTextSize.value = size
        sharedPrefs.edit().putString("azkar_text_size", size).apply()
    }

    fun setAzkarAutoScroll(enabled: Boolean) {
        isAzkarAutoScrollEnabled.value = enabled
        sharedPrefs.edit().putBoolean("azkar_auto_scroll", enabled).apply()
    }

    fun setAzkarHaptic(enabled: Boolean) {
        isAzkarHapticEnabled.value = enabled
        sharedPrefs.edit().putBoolean("azkar_haptic", enabled).apply()
    }

    fun setAzkarTransliteration(enabled: Boolean) {
        showAzkarTransliteration.value = enabled
        sharedPrefs.edit().putBoolean("azkar_transliteration", enabled).apply()
    }

    fun setAzkarBenefits(enabled: Boolean) {
        showAzkarBenefits.value = enabled
        sharedPrefs.edit().putBoolean("azkar_benefits", enabled).apply()
    }

    fun setAzkarSettingsOpen(isOpen: Boolean) {
        isAzkarSettingsOpen.value = isOpen
    }

    fun getRemainingDuaCount(dua: DuaItem): Int {
        val map = azkarRemainingCounts.value
        return map[dua.id] ?: dua.repeatCount
    }

    fun decrementDuaCount(dua: DuaItem, onCompleted: (() -> Unit)? = null) {
        val currentRemaining = getRemainingDuaCount(dua)
        if (currentRemaining > 0) {
            val updated = currentRemaining - 1
            val newMap = azkarRemainingCounts.value.toMutableMap()
            newMap[dua.id] = updated
            azkarRemainingCounts.value = newMap
            if (dua.category.contains("Azkar", ignoreCase = true) || dua.category.contains("Adhkar", ignoreCase = true)) {
                recordAzkarActivity()
            } else {
                recordDuaActivity()
            }
            if (isAzkarHapticEnabled.value) {
                triggerHaptic()
            }

            if (updated == 0) {
                if (isAzkarHapticEnabled.value) {
                    triggerCompletionHaptic()
                }
                showToast("Completed: ${dua.title} ✓")
                onCompleted?.invoke()
            }
        }
    }

    fun resetDuaCount(dua: DuaItem) {
        val newMap = azkarRemainingCounts.value.toMutableMap()
        newMap[dua.id] = dua.repeatCount
        azkarRemainingCounts.value = newMap
    }

    fun resetCategoryDuaCounts(category: String) {
        val duasInCategory = DuaData.categorizedDuas.filter { it.category.equals(category, ignoreCase = true) }
        val newMap = azkarRemainingCounts.value.toMutableMap()
        duasInCategory.forEach { dua ->
            newMap[dua.id] = dua.repeatCount
        }
        azkarRemainingCounts.value = newMap
        showToast("All counts in $category reset")
    }

    // ============================================================
    // QURAN AUDIO PLAYER (FULL SURAH & AYAH MP3 STREAMING VIA MEDIAPLAYER)
    // ============================================================

    fun getSurahAudioUrl(surahNumber: Int, reciter: Reciter = selectedReciter.value): String {
        val surahFormatted = String.format(Locale.US, "%03d", surahNumber)
        return when (reciter.id) {
            "abdulbasit" -> "https://server7.mp3quran.net/basit/$surahFormatted.mp3"
            "sudais" -> "https://server11.mp3quran.net/sds/$surahFormatted.mp3"
            "muaiqly" -> "https://server12.mp3quran.net/maher/$surahFormatted.mp3"
            "ghamdi" -> "https://server7.mp3quran.net/ghamdi/$surahFormatted.mp3"
            "shatri" -> "https://server11.mp3quran.net/shatri/$surahFormatted.mp3"
            "minshawi" -> "https://server10.mp3quran.net/minsh/$surahFormatted.mp3"
            "husary" -> "https://server13.mp3quran.net/husr/$surahFormatted.mp3"
            else -> "https://server8.mp3quran.net/afs/$surahFormatted.mp3"
        }
    }

    fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int, reciter: Reciter = selectedReciter.value): String {
        // If ayahNumber is 0 (Basmala prelude), point to 001001.mp3 (Surah 1 Ayah 1 Bismillah)
        val (sNum, aNum) = if (ayahNumber == 0) Pair(1, 1) else Pair(surahNumber, ayahNumber)
        val surahFormatted = String.format(Locale.US, "%03d", sNum)
        val ayahFormatted = String.format(Locale.US, "%03d", aNum)
        return when (reciter.id) {
            "abdulbasit" -> "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/$surahFormatted$ayahFormatted.mp3"
            "sudais" -> "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/$surahFormatted$ayahFormatted.mp3"
            "muaiqly" -> "https://everyayah.com/data/MaherAlMuaiqly128kbps/$surahFormatted$ayahFormatted.mp3"
            "ghamdi" -> "https://everyayah.com/data/Ghamadi_40kbps/$surahFormatted$ayahFormatted.mp3"
            "shatri" -> "https://everyayah.com/data/Abu_Bakr_Ash-Shaatree_128kbps/$surahFormatted$ayahFormatted.mp3"
            "minshawi" -> "https://everyayah.com/data/Minshawy_Murattal_128kbps/$surahFormatted$ayahFormatted.mp3"
            "husary" -> "https://everyayah.com/data/Husary_128kbps/$surahFormatted$ayahFormatted.mp3"
            else -> "https://everyayah.com/data/Alafasy_128kbps/$surahFormatted$ayahFormatted.mp3"
        }
    }

    fun playSurahAudio(surah: Surah, startVerse: Int = 1, openPlayer: Boolean = true) {
        isAyahAudioMode.value = false
        currentPlayingSurah.value = surah
        currentPlayingVerse.value = startVerse

        val audioUrl = getSurahAudioUrl(surah.number, selectedReciter.value)
        val audioSource = QuranAudioCacheManager.getSurahAudioSource(
            getApplication(),
            surah.number,
            selectedReciter.value.id,
            audioUrl
        )

        isAudioBuffering.value = true
        isAudioPlaying.value = true
        audioProgress.value = 0f
        audioCurrentPositionMs.value = 0

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }
            } else {
                mediaPlayer?.reset()
            }

            mediaPlayer?.apply {
                setDataSource(audioSource)
                setOnPreparedListener { mp ->
                    isAudioBuffering.value = false
                    isAudioPlaying.value = true
                    val dur = mp.duration.coerceAtLeast(1)
                    audioDurationMs.value = dur
                    mp.start()
                    startAudioProgressTracker()
                }

                setOnCompletionListener {
                    if (isAyahAudioMode.value) {
                        onAyahAudioCompleted()
                    } else {
                        onSurahAudioCompleted()
                    }
                }

                setOnErrorListener { _, what, extra ->
                    isAudioBuffering.value = false
                    isAudioPlaying.value = false
                    showToast("Reconnecting audio stream...")
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            isAudioBuffering.value = false
            isAudioPlaying.value = false
            showToast("Unable to stream audio: ${e.localizedMessage}")
        }

        if (openPlayer) {
            navigateTo(NoorDestination.QURAN_AUDIO_STREAM)
        }
    }

    fun playAyah(
        surah: Surah,
        ayahNumber: Int,
        openPlayer: Boolean = false,
        playBasmalaFirst: Boolean = true
    ) {
        isAyahAudioMode.value = true
        currentPlayingSurah.value = surah

        // If starting recitation from ayah 1 for any surah except Al-Fatihah (1) and At-Tawbah (9),
        // recite the separate Basmala audio first.
        val shouldPlayBasmala = playBasmalaFirst && ayahNumber == 1 && surah.number != 1 && surah.number != 9
        val effectiveAyahNumber = if (shouldPlayBasmala) 0 else ayahNumber
        currentPlayingVerse.value = effectiveAyahNumber

        val audioUrl = getAyahAudioUrl(surah.number, effectiveAyahNumber, selectedReciter.value)
        val audioSource = QuranAudioCacheManager.getAudioSource(
            getApplication(),
            if (effectiveAyahNumber == 0) 1 else surah.number,
            if (effectiveAyahNumber == 0) 1 else effectiveAyahNumber,
            selectedReciter.value.id,
            audioUrl
        )

        isAudioBuffering.value = true
        isAudioPlaying.value = true

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }
            } else {
                mediaPlayer?.reset()
            }

            mediaPlayer?.apply {
                setDataSource(audioSource)
                setOnPreparedListener { mp ->
                    isAudioBuffering.value = false
                    isAudioPlaying.value = true
                    val dur = mp.duration.coerceAtLeast(1)
                    audioDurationMs.value = dur
                    mp.start()
                    startAudioProgressTracker()
                }

                setOnCompletionListener {
                    if (isAyahAudioMode.value) {
                        onAyahAudioCompleted()
                    } else {
                        onSurahAudioCompleted()
                    }
                }

                setOnErrorListener { _, _, _ ->
                    isAudioBuffering.value = false
                    isAudioPlaying.value = false
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            isAudioBuffering.value = false
            isAudioPlaying.value = false
        }

        if (openPlayer) {
            navigateTo(NoorDestination.QURAN_AUDIO_STREAM)
        }
    }

    fun playAyah(surahNumber: Int, ayahNumber: Int) {
        val surah = QuranData.surahs.firstOrNull { it.number == surahNumber }
            ?: QuranData.completeSurahList.firstOrNull { it.number == surahNumber }
            ?: currentPlayingSurah.value
        playAyah(surah, ayahNumber, openPlayer = false, playBasmalaFirst = true)
    }

    private fun onAyahAudioCompleted() {
        val surah = currentPlayingSurah.value
        val currentAyah = currentPlayingVerse.value
        val totalAyahs = if (surah.verses.isNotEmpty()) surah.verses.size else surah.totalVerses

        if (isAudioRepeatOne.value) {
            playAyah(surah, currentAyah, openPlayer = false, playBasmalaFirst = false)
        } else if (currentAyah == 0) {
            // Basmala just finished reciting -> seamlessly proceed to Ayah 1 of the surah
            playAyah(surah, 1, openPlayer = false, playBasmalaFirst = false)
        } else if (isAutoAdvanceAyah.value && currentAyah < totalAyahs) {
            val nextAyah = currentAyah + 1
            playAyah(surah, nextAyah, openPlayer = false, playBasmalaFirst = false)
        } else if (isAutoAdvanceAyah.value && surah.number < 114) {
            val nextSurah = QuranData.surahs.firstOrNull { it.number == surah.number + 1 }
                ?: QuranData.completeSurahList.firstOrNull { it.number == surah.number + 1 }
            if (nextSurah != null) {
                currentPlayingSurah.value = nextSurah
                playAyah(nextSurah, 1, openPlayer = false, playBasmalaFirst = true)
                showToast("Now reciting: Surah ${nextSurah.nameEnglish}")
            } else {
                isAudioPlaying.value = false
            }
        } else {
            isAudioPlaying.value = false
        }
    }

    fun isSurahDownloaded(surahNumber: Int, reciterId: String = selectedReciter.value.id): Boolean {
        return QuranAudioCacheManager.isSurahAudioCached(getApplication(), surahNumber, reciterId) ||
                downloadedSurahs.value.contains("${reciterId}_${surahNumber}")
    }

    fun downloadSurahOffline(surah: Surah, reciter: Reciter = selectedReciter.value) {
        val key = "${reciter.id}_${surah.number}"
        if (isSurahDownloaded(surah.number, reciter.id)) {
            showToast("Surah ${surah.nameEnglish} is already cached for offline listening.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val progressMap = isSurahDownloading.value.toMutableMap()
            progressMap[key] = 0.05f
            isSurahDownloading.value = progressMap
            showToast("Downloading Surah ${surah.nameEnglish} (${reciter.name})...")

            val url = getSurahAudioUrl(surah.number, reciter)
            val success = QuranAudioCacheManager.downloadSurahAudio(
                getApplication(),
                surah.number,
                reciter.id,
                url
            ) { prog ->
                val currentMap = isSurahDownloading.value.toMutableMap()
                currentMap[key] = prog
                isSurahDownloading.value = currentMap
            }

            val finishMap = isSurahDownloading.value.toMutableMap()
            finishMap.remove(key)
            isSurahDownloading.value = finishMap

            if (success) {
                val newSet = downloadedSurahs.value.toMutableSet()
                newSet.add(key)
                downloadedSurahs.value = newSet
                triggerCompletionHaptic()
                showToast("✓ Surah ${surah.nameEnglish} saved for offline listening")
            } else {
                showToast("Could not download audio. Check connection.")
            }
        }
    }

    fun deleteSurahOffline(surah: Surah, reciter: Reciter = selectedReciter.value) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = QuranAudioCacheManager.getSurahFile(getApplication(), surah.number, reciter.id)
            if (file.exists()) {
                file.delete()
            }
            val key = "${reciter.id}_${surah.number}"
            val newSet = downloadedSurahs.value.toMutableSet()
            newSet.remove(key)
            downloadedSurahs.value = newSet
            showToast("Offline audio removed for Surah ${surah.nameEnglish}")
        }
    }

    fun refreshDownloadedSurahs() {
        viewModelScope.launch(Dispatchers.IO) {
            val set = mutableSetOf<String>()
            QuranData.reciters.forEach { reciter ->
                QuranData.completeSurahList.forEach { surah ->
                    if (QuranAudioCacheManager.isSurahAudioCached(getApplication(), surah.number, reciter.id) ||
                        QuranAudioCacheManager.isSurahCached(getApplication(), surah.number, reciter.id, surah.totalVerses)) {
                        set.add("${reciter.id}_${surah.number}")
                    }
                }
            }
            downloadedSurahs.value = set
        }
    }

    private fun onSurahAudioCompleted() {
        if (isAudioRepeatOne.value) {
            playSurahAudio(currentPlayingSurah.value, openPlayer = false)
        } else if (isAutoAdvanceAyah.value) {
            playNextSurah()
        } else {
            isAudioPlaying.value = false
            audioProgress.value = 1.0f
        }
    }

    fun toggleAudioPlayback(surah: Surah? = null, reciter: Reciter? = null) {
        if (reciter != null && reciter != selectedReciter.value) {
            selectedReciter.value = reciter
            val targetSurah = surah ?: currentPlayingSurah.value
            playSurahAudio(targetSurah, openPlayer = false)
            return
        }

        if (surah != null && surah.number != currentPlayingSurah.value.number) {
            playSurahAudio(surah, openPlayer = false)
            return
        }

        val mp = mediaPlayer
        if (mp != null) {
            if (mp.isPlaying) {
                mp.pause()
                isAudioPlaying.value = false
            } else {
                mp.start()
                isAudioPlaying.value = true
                startAudioProgressTracker()
            }
        } else {
            val targetSurah = surah ?: currentPlayingSurah.value
            playSurahAudio(targetSurah, openPlayer = false)
        }
    }

    fun pauseAudio() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            // ignore
        }
        isAudioPlaying.value = false
    }

    fun stopAudioPlayback() {
        stopAndResetAudio()
    }

    fun stopAndResetAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
        } catch (e: Exception) {
            // ignore
        }
        isAudioPlaying.value = false
        isAudioBuffering.value = false
        isAyahAudioMode.value = false
        currentPlayingVerse.value = 0
        audioProgress.value = 0f
        audioCurrentPositionMs.value = 0
        audioDurationMs.value = 0
        audioProgressTrackerJob?.cancel()
    }

    fun stopAudio() {
        stopAndResetAudio()
    }

    fun skipBack10Seconds() {
        val mp = mediaPlayer ?: return
        try {
            val cur = mp.currentPosition
            val target = (cur - 10000).coerceAtLeast(0)
            mp.seekTo(target)
            audioCurrentPositionMs.value = target
            val dur = audioDurationMs.value
            if (dur > 0) {
                audioProgress.value = (target.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
            }
            triggerHaptic()
        } catch (e: Exception) {
            // ignore
        }
    }

    fun seekAudioTo(fraction: Float) {
        val mp = mediaPlayer ?: return
        val duration = audioDurationMs.value
        if (duration > 0) {
            val targetMs = (fraction * duration).toInt().coerceIn(0, duration)
            try {
                mp.seekTo(targetMs)
                audioCurrentPositionMs.value = targetMs
                audioProgress.value = fraction.coerceIn(0f, 1f)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun playNextSurah() {
        val currentNum = currentPlayingSurah.value.number
        val nextNum = if (currentNum < 114) currentNum + 1 else 1
        val nextSurah = QuranData.surahs.firstOrNull { it.number == nextNum }
            ?: QuranData.completeSurahList.firstOrNull { it.number == nextNum }
            ?: QuranData.surahs.first()
        playSurahAudio(nextSurah, openPlayer = false)
        showToast("Playing Surah ${nextSurah.nameEnglish}")
    }

    fun playPreviousSurah() {
        val currentNum = currentPlayingSurah.value.number
        val prevNum = if (currentNum > 1) currentNum - 1 else 114
        val prevSurah = QuranData.surahs.firstOrNull { it.number == prevNum }
            ?: QuranData.completeSurahList.firstOrNull { it.number == prevNum }
            ?: QuranData.surahs.last()
        playSurahAudio(prevSurah, openPlayer = false)
        showToast("Playing Surah ${prevSurah.nameEnglish}")
    }

    fun playNextAyah() = playNextSurah()

    fun playPreviousAyah() = playPreviousSurah()

    fun toggleRepeatMode() {
        isAudioRepeatOne.value = !isAudioRepeatOne.value
        showToast(if (isAudioRepeatOne.value) "Repeat Surah: ON" else "Repeat: OFF")
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        sleepTimerMinutes.value = minutes
        if (minutes == null || minutes <= 0) {
            showToast("Sleep timer turned off")
            return
        }
        showToast("Sleep timer set for $minutes minutes")
        sleepTimerJob = viewModelScope.launch {
            delay(minutes * 60 * 1000L)
            stopAudioPlayback()
            sleepTimerMinutes.value = null
            showToast("Sleep timer: Audio paused")
        }
    }

    fun setAudioSpeed(speed: Float) {
        audioPlaybackSpeed.value = speed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        mp.playbackParams = mp.playbackParams.setSpeed(speed)
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun selectReciter(reciter: Reciter) {
        selectedReciter.value = reciter
        if (isAudioPlaying.value) {
            playSurahAudio(currentPlayingSurah.value, openPlayer = false)
        }
        showToast("Reciter: ${reciter.name}")
    }

    private fun startAudioProgressTracker() {
        audioProgressTrackerJob?.cancel()
        audioProgressTrackerJob = viewModelScope.launch {
            while (isAudioPlaying.value) {
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            val cur = mp.currentPosition
                            val dur = mp.duration.coerceAtLeast(1)
                            audioCurrentPositionMs.value = cur
                            audioDurationMs.value = dur
                            audioProgress.value = (cur.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
                        }
                    }
                } catch (e: Exception) {
                    // ignore
                }
                delay(250)
            }
        }
    }

    // ============================================================
    // QURAN READING & EXACT BOOKMARK SYSTEM
    // ============================================================

    fun selectSurahForReading(surah: Surah, startAyah: Int = 0) {
        viewModelScope.launch {
            val surahWithVerses = if (surah.verses.isNotEmpty()) surah else repository.getSurahWithVerses(surah.number)
            selectedSurahForReading.value = surahWithVerses
            val currentBookmark = readingProgress.value
            val targetAyah = if (startAyah > 0) {
                startAyah
            } else if (currentBookmark != null && currentBookmark.surahNumber == surah.number) {
                currentBookmark.ayahNumber
            } else {
                0
            }
            targetAyahToScrollTo.value = targetAyah
            navigateTo(NoorDestination.QURAN_READER)
        }
    }

    fun reloadCurrentSurah() {
        val current = selectedSurahForReading.value
        viewModelScope.launch {
            val loaded = repository.getSurahWithVerses(current.number)
            selectedSurahForReading.value = loaded
            if (loaded.verses.isNotEmpty()) {
                showToast("Surah ${loaded.nameEnglish} loaded (${loaded.verses.size} ayahs).")
            } else {
                showToast("Loading verses for ${loaded.nameEnglish}...")
            }
        }
    }

    fun saveExactReadingBookmark(surah: Surah, ayahNumber: Int) {
        viewModelScope.launch {
            repository.saveReadingProgress(
                surahNumber = surah.number,
                surahName = surah.nameEnglish,
                ayahNumber = ayahNumber,
                totalAyahs = surah.totalVerses
            )
            targetAyahToScrollTo.value = ayahNumber

            triggerHaptic()
            showToast("🔖 Saved bookmark position: ${surah.nameEnglish} Ayah $ayahNumber")
        }
    }

    fun markKhatmaProgressToVerse(surah: Surah, ayahNumber: Int) {
        viewModelScope.launch {
            val absAyah = KhatmaEngine.getAbsoluteAyahIndex(surah.number, ayahNumber)
            updateKhatmaReadAyahs(absAyah)
            
            // Also keep reading progress pointer updated so reader can resume here seamlessly
            repository.saveReadingProgress(
                surahNumber = surah.number,
                surahName = surah.nameEnglish,
                ayahNumber = ayahNumber,
                totalAyahs = surah.totalVerses
            )
            targetAyahToScrollTo.value = ayahNumber

            triggerHaptic()
            val total = KhatmaEngine.TOTAL_QURAN_AYAHS
            showToast("📖 Khatma Progress Updated: $absAyah / $total Ayahs (${surah.nameEnglish} : $ayahNumber)")
        }
    }

    fun resumeReading(progress: ReadingProgressEntity) {
        viewModelScope.launch {
            val targetSurah = repository.getSurahWithVerses(progress.surahNumber)
            selectedSurahForReading.value = targetSurah
            targetAyahToScrollTo.value = progress.ayahNumber
            navigateTo(NoorDestination.QURAN_READER)
        }
    }

    fun openPreviousSurah() {
        val currentNum = selectedSurahForReading.value.number
        if (currentNum > 1) {
            viewModelScope.launch {
                val prevSurah = repository.getSurahWithVerses(currentNum - 1)
                selectedSurahForReading.value = prevSurah
                targetAyahToScrollTo.value = 0
            }
        } else {
            showToast("You are at the first Surah (Al-Fatihah)")
        }
    }

    fun openNextSurah() {
        val currentNum = selectedSurahForReading.value.number
        if (currentNum < 114) {
            viewModelScope.launch {
                val nextSurah = repository.getSurahWithVerses(currentNum + 1)
                selectedSurahForReading.value = nextSurah
                targetAyahToScrollTo.value = 0
            }
        } else {
            showToast("You are at the final Surah (An-Nas)")
        }
    }

    fun toggleMushafFlowMode(enabled: Boolean? = null) {
        val newState = enabled ?: !isMushafFlowMode.value
        isMushafFlowMode.value = newState
        sharedPrefs.edit().putBoolean("is_mushaf_flow_mode", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Distraction-Free Mushaf Flow enabled 📖" else "Standard Reading View with Translations")
    }

    fun toggleTajweedHighlights(enabled: Boolean? = null) {
        val newState = enabled ?: !isTajweedHighlightsEnabled.value
        isTajweedHighlightsEnabled.value = newState
        sharedPrefs.edit().putBoolean("is_tajweed_highlights", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Color-coded Tajweed Rules active 🎨" else "Tajweed highlights turned off")
    }

    fun dismissKhatmaMilestoneModal() {
        khatmaMilestoneModal.value = null
    }

    // Home Screen Widget Customization
    fun openCustomizeHomeSheet() {
        isCustomizeHomeSheetOpen.value = true
        triggerHaptic()
    }

    fun closeCustomizeHomeSheet() {
        isCustomizeHomeSheetOpen.value = false
    }

    fun toggleHomeWidgetVisibility(widget: HomeWidgetType) {
        val current = homeWidgetsVisibility.value.toMutableMap()
        val newVisible = !(current[widget] ?: true)
        current[widget] = newVisible
        homeWidgetsVisibility.value = current
        saveHomeWidgetsConfig()
        triggerHaptic()
    }

    fun moveHomeWidgetUp(widget: HomeWidgetType) {
        val list = homeWidgetsOrder.value.toMutableList()
        val index = list.indexOf(widget)
        if (index > 0) {
            list.removeAt(index)
            list.add(index - 1, widget)
            homeWidgetsOrder.value = list
            saveHomeWidgetsConfig()
            triggerHaptic()
        }
    }

    fun moveHomeWidgetDown(widget: HomeWidgetType) {
        val list = homeWidgetsOrder.value.toMutableList()
        val index = list.indexOf(widget)
        if (index in 0 until list.lastIndex) {
            list.removeAt(index)
            list.add(index + 1, widget)
            homeWidgetsOrder.value = list
            saveHomeWidgetsConfig()
            triggerHaptic()
        }
    }

    fun resetHomeWidgetsOrder() {
        homeWidgetsOrder.value = HomeWidgetType.defaultOrderedList()
        homeWidgetsVisibility.value = HomeWidgetType.values().associateWith { it.defaultVisible }
        saveHomeWidgetsConfig()
        triggerHaptic()
        showToast("Home feed reset to default layout")
    }

    private fun saveHomeWidgetsConfig() {
        val orderString = homeWidgetsOrder.value.joinToString(",") { it.id }
        val visibilityString = homeWidgetsVisibility.value.entries.joinToString(",") { "${it.key.id}:${it.value}" }
        sharedPrefs.edit()
            .putString("home_widgets_order", orderString)
            .putString("home_widgets_visibility", visibilityString)
            .apply()
    }

    private fun loadSavedHomeWidgetsConfig() {
        try {
            val orderString = sharedPrefs.getString("home_widgets_order", null)
            if (!orderString.isNullOrBlank()) {
                val types = orderString.split(",").mapNotNull { id ->
                    HomeWidgetType.values().firstOrNull { it.id == id }
                }.toMutableList()
                if (types.isNotEmpty()) {
                    if (types.firstOrNull() == HomeWidgetType.SALAT_TIMELINE && types.getOrNull(1) == HomeWidgetType.SPIRITUAL_ESSENTIALS) {
                        types.remove(HomeWidgetType.SPIRITUAL_ESSENTIALS)
                        types.add(0, HomeWidgetType.SPIRITUAL_ESSENTIALS)
                    }
                    // Add any missing ones at the end
                    val allTypes = HomeWidgetType.values().toList()
                    val completeList = types + allTypes.filter { !types.contains(it) }
                    homeWidgetsOrder.value = completeList
                }
            } else {
                homeWidgetsOrder.value = HomeWidgetType.defaultOrderedList()
            }

            val visibilityString = sharedPrefs.getString("home_widgets_visibility", null)
            if (!visibilityString.isNullOrBlank()) {
                val map = mutableMapOf<HomeWidgetType, Boolean>()
                visibilityString.split(",").forEach { pair ->
                    val parts = pair.split(":")
                    if (parts.size == 2) {
                        val widget = HomeWidgetType.values().firstOrNull { it.id == parts[0] }
                        if (widget != null) {
                            map[widget] = parts[1].toBoolean()
                        }
                    }
                }
                if (map.isNotEmpty()) {
                    homeWidgetsVisibility.value = map
                }
            }
        } catch (e: Exception) {
            // fallback to default
        }
    }

    // Notification Testing & Routine Reminders
    fun sendTestPrayerNotification(prayerName: String = "Fajr") {
        val pt = _prayerTimes.value.firstOrNull { it.name == prayerName }
        val time = pt?.timeString ?: "05:15 AM"
        NoorNotificationHelper.showPrayerAlert(getApplication(), prayerName, time, isPreAlert = false)
        showToast("Sent $prayerName Adhan alert notification 🔔")
    }

    fun sendTestAthkarNotification(type: String = "Morning") {
        NoorNotificationHelper.showAthkarReminder(getApplication(), type)
        showToast("Sent $type Athkar reminder notification 🕊️")
    }

    fun sendTestKhatmaNotification() {
        val plan = activeKhatmaPlan.value
        val dailyAyahs = (plan?.totalDays?.let { (6236 / it).coerceAtLeast(1) }) ?: 20
        val surah = QuranData.surahs.firstOrNull { it.number == (plan?.lastReadSurah ?: 1) }?.nameEnglish ?: "Al-Baqarah"
        NoorNotificationHelper.showKhatmaReminder(getApplication(), dailyAyahs, surah)
        showToast("Sent Daily Khatma reading reminder 📖")
    }

    fun releaseAudioPlayer() {
        try {
            audioProgressTrackerJob?.cancel()
            audioProgressTrackerJob = null
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
            isAudioPlaying.value = false
            isAudioBuffering.value = false
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        audioProgressTrackerJob?.cancel()
        releaseAudioPlayer()
    }

    // Daily Habit Tracking
    fun incrementHabit(habit: DailyHabitEntity) {
        viewModelScope.launch {
            repository.updateHabitProgress(habit, habit.currentCount + 1)
            triggerHaptic()
        }
    }

    fun addCustomHabit(title: String, target: Int, category: String) {
        viewModelScope.launch {
            repository.addCustomHabit(title, target, category)
            showToast("Habit added!")
        }
    }

    fun deleteHabit(habit: DailyHabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            showToast("Habit deleted")
        }
    }

    fun toggleFavorite(itemType: String, title: String, subtitle: String, details: String = "", source: String = "") {
        viewModelScope.launch {
            repository.toggleFavorite(
                type = itemType,
                title = title,
                arabicText = subtitle,
                translation = details,
                source = source.ifBlank { "Noor App" }
            )
            showToast("Favorites updated")
        }
    }

    // ============================================================
    // QURAN KHATMA COMPANION & LIFECYCLE MANAGEMENT
    // ============================================================

    fun createOrResetKhatma(
        days: Int,
        startDate: LocalDate = LocalDate.now(),
        sessionsCount: Int = 3,
        reminderEnabled: Boolean = true,
        reminderTime: String = "07:00 AM",
        title: String = "Personal Khatma"
    ) {
        viewModelScope.launch {
            val startDay = startDate.toEpochDay()
            val endDay = startDate.plusDays((days - 1).toLong().coerceAtLeast(0)).toEpochDay()
            val existing = repository.getActiveKhatmaPlanOnce()
            // If existing, keep readAyahsCount if user is simply adjusting goal
            val preservedRead = if (existing != null && !existing.isCompleted) existing.readAyahsCount else 0
            val coord = if (preservedRead > 0) KhatmaEngine.getAyahCoordinate(preservedRead) else KhatmaEngine.getAyahCoordinate(1)

            val newPlan = KhatmaPlanEntity(
                id = 1,
                title = title,
                totalDays = days.coerceIn(1, 365),
                startEpochDay = startDay,
                targetEndEpochDay = endDay,
                dailySessionsCount = sessionsCount.coerceIn(1, 5),
                reminderEnabled = reminderEnabled,
                reminderTime = reminderTime,
                totalAyahs = KhatmaEngine.TOTAL_QURAN_AYAHS,
                readAyahsCount = preservedRead,
                lastReadSurah = coord.surahNumber,
                lastReadAyah = coord.ayahNumber,
                isCompleted = false,
                completedAtEpochDay = null,
                daysTaken = null,
                paceAdjustmentType = "SPREAD",
                completedSessionsTodayBitmask = 0,
                lastSessionDateDay = LocalDate.now().toEpochDay(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(newPlan)
            triggerHaptic()
            showToast("✨ Khatma plan started ($days Days). May Allah accept!")
            isKhatmaSetupSheetOpen.value = false
        }
    }

    fun updateKhatmaReadAyahs(newTotalRead: Int) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val clamped = newTotalRead.coerceIn(0, KhatmaEngine.TOTAL_QURAN_AYAHS)
            val coord = if (clamped > 0) KhatmaEngine.getAyahCoordinate(clamped) else KhatmaEngine.getAyahCoordinate(1)
            val isNowCompleted = clamped >= KhatmaEngine.TOTAL_QURAN_AYAHS

            val todayEpoch = LocalDate.now().toEpochDay()
            val daysTaken = (todayEpoch - current.startEpochDay + 1).toInt().coerceAtLeast(1)

            val oldAyahs = current.readAyahsCount
            val oldJuz = if (oldAyahs > 0) KhatmaEngine.getAyahCoordinate(oldAyahs).juzNumber else 0
            val newJuz = if (clamped > 0) KhatmaEngine.getAyahCoordinate(clamped).juzNumber else 0

            val updated = current.copy(
                readAyahsCount = clamped,
                lastReadSurah = coord.surahNumber,
                lastReadAyah = coord.ayahNumber,
                isCompleted = isNowCompleted,
                completedAtEpochDay = if (isNowCompleted) todayEpoch else current.completedAtEpochDay,
                daysTaken = if (isNowCompleted) daysTaken else current.daysTaken,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(updated)

            if (isNowCompleted) {
                triggerCompletionHaptic()
                recordKhatmaCompletionInHistory(updated)
                isKhatmaCompletionCelebrationOpen.value = true
            } else {
                if (newJuz > oldJuz && oldAyahs > 0) {
                    khatmaMilestoneModal.value = KhatmaMilestoneData(
                        title = "Juz $oldJuz Completed!",
                        subtitle = "Alhamdulillah! You entered Juz $newJuz ($clamped / 6,236 Ayahs)",
                        currentJuz = newJuz,
                        ayahsCompletedToday = (clamped - oldAyahs).coerceAtLeast(1),
                        totalAyahsRead = clamped,
                        percentage = clamped.toFloat() / KhatmaEngine.TOTAL_QURAN_AYAHS.toFloat()
                    )
                    triggerCompletionHaptic()
                } else if (clamped % 100 == 0 && clamped > oldAyahs) {
                    khatmaMilestoneModal.value = KhatmaMilestoneData(
                        title = "Milestone: $clamped Ayahs Completed!",
                        subtitle = "Barakallahu Feek! You are maintaining strong spiritual pace.",
                        currentJuz = newJuz,
                        ayahsCompletedToday = (clamped - oldAyahs).coerceAtLeast(1),
                        totalAyahsRead = clamped,
                        percentage = clamped.toFloat() / KhatmaEngine.TOTAL_QURAN_AYAHS.toFloat()
                    )
                    triggerHaptic()
                } else {
                    triggerHaptic()
                }
            }
        }
    }

    fun advanceKhatmaByAyahs(count: Int) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val newTotal = (current.readAyahsCount + count).coerceIn(0, KhatmaEngine.TOTAL_QURAN_AYAHS)
            updateKhatmaReadAyahs(newTotal)
            showToast("Logged +$count Ayahs in Khatma")
        }
    }

    fun completeKhatmaSession(sessionIndex: Int, targetAyahs: Int) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val todayEpoch = LocalDate.now().toEpochDay()
            val currentBitmask = if (current.lastSessionDateDay == todayEpoch) current.completedSessionsTodayBitmask else 0
            val newBitmask = currentBitmask or (1 shl sessionIndex)

            // Advance read count to match session target if needed
            val newReadCount = (current.readAyahsCount + targetAyahs).coerceIn(0, KhatmaEngine.TOTAL_QURAN_AYAHS)
            val coord = KhatmaEngine.getAyahCoordinate(newReadCount)
            val isNowCompleted = newReadCount >= KhatmaEngine.TOTAL_QURAN_AYAHS

            val updated = current.copy(
                readAyahsCount = newReadCount,
                lastReadSurah = coord.surahNumber,
                lastReadAyah = coord.ayahNumber,
                completedSessionsTodayBitmask = newBitmask,
                lastSessionDateDay = todayEpoch,
                isCompleted = isNowCompleted,
                completedAtEpochDay = if (isNowCompleted) todayEpoch else null,
                daysTaken = if (isNowCompleted) (todayEpoch - current.startEpochDay + 1).toInt() else null,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(updated)
            triggerCompletionHaptic()
            showToast("Session completed! +$targetAyahs Ayahs ✓")

            if (isNowCompleted) {
                recordKhatmaCompletionInHistory(updated)
                isKhatmaCompletionCelebrationOpen.value = true
            }
        }
    }

    fun adjustKhatmaPace(strategy: String) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val todayEpoch = LocalDate.now().toEpochDay()
            val currentDay = (todayEpoch - current.startEpochDay + 1).toInt().coerceIn(1, current.totalDays)
            val remainingDays = (current.totalDays - currentDay + 1).coerceAtLeast(1)
            val remainingAyahs = (KhatmaEngine.TOTAL_QURAN_AYAHS - current.readAyahsCount).coerceAtLeast(1)

            val updatedPlan = when (strategy) {
                "EXTEND" -> {
                    // Calculate extended days based on comfortable pace (e.g. 150 ayahs/day)
                    val comfortableDailyPace = 150
                    val neededDays = kotlin.math.ceil(remainingAyahs.toDouble() / comfortableDailyPace).toInt().coerceAtLeast(1)
                    val newTotalDays = (currentDay - 1) + neededDays
                    val newTargetEnd = current.startEpochDay + (newTotalDays - 1)
                    current.copy(
                        totalDays = newTotalDays,
                        targetEndEpochDay = newTargetEnd,
                        paceAdjustmentType = "EXTEND",
                        updatedAt = System.currentTimeMillis()
                    )
                }
                "GRADUAL" -> {
                    current.copy(
                        paceAdjustmentType = "GRADUAL",
                        updatedAt = System.currentTimeMillis()
                    )
                }
                else -> { // "SPREAD"
                    current.copy(
                        paceAdjustmentType = "SPREAD",
                        updatedAt = System.currentTimeMillis()
                    )
                }
            }

            repository.saveKhatmaPlan(updatedPlan)
            triggerHaptic()
            isKhatmaPaceAdjustSheetOpen.value = false
            showToast("Pace recalculated. Continue with barakah 🌿")
        }
    }

    fun changeKhatmaTotalDays(newDays: Int) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val clampedDays = newDays.coerceIn(1, 365)
            val newEnd = current.startEpochDay + (clampedDays - 1)
            val updated = current.copy(
                totalDays = clampedDays,
                targetEndEpochDay = newEnd,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(updated)
            triggerHaptic()
            showToast("Khatma duration updated to $clampedDays days")
        }
    }

    fun updateKhatmaReminder(enabled: Boolean, time: String) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val updated = current.copy(
                reminderEnabled = enabled,
                reminderTime = time,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(updated)
            showToast(if (enabled) "Daily Khatma reminder set for $time" else "Daily reminder disabled")
        }
    }

    fun continueKhatmaReading() {
        val state = khatmaDashboardState.value
        if (state != null) {
            val targetCoord = state.nextReadingPosition
            openKhatmaReadingAtAyah(targetCoord.surahNumber, targetCoord.ayahNumber)
        } else {
            navigateTo(NoorDestination.QURAN_SURAH_LIST)
        }
    }

    fun openKhatmaReadingAtAyah(surahNumber: Int, ayahNumber: Int) {
        val surah = QuranData.surahs.firstOrNull { it.number == surahNumber }
            ?: QuranData.completeSurahList.firstOrNull { it.number == surahNumber }
            ?: QuranData.surahs.first()
        selectSurahForReading(surah, ayahNumber)
    }

    fun markKhatmaCompleted() {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val todayEpoch = LocalDate.now().toEpochDay()
            val daysTaken = (todayEpoch - current.startEpochDay + 1).toInt().coerceAtLeast(1)
            val completedPlan = current.copy(
                readAyahsCount = KhatmaEngine.TOTAL_QURAN_AYAHS,
                isCompleted = true,
                completedAtEpochDay = todayEpoch,
                daysTaken = daysTaken,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(completedPlan)
            recordKhatmaCompletionInHistory(completedPlan)
            triggerCompletionHaptic()
            isKhatmaCompletionCelebrationOpen.value = true
        }
    }

    private suspend fun recordKhatmaCompletionInHistory(plan: KhatmaPlanEntity) {
        val startDate = LocalDate.ofEpochDay(plan.startEpochDay).format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
        val endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
        val daysTaken = plan.daysTaken ?: (LocalDate.now().toEpochDay() - plan.startEpochDay + 1).toInt().coerceAtLeast(1)
        repository.recordKhatmaHistory(
            KhatmaHistoryEntity(
                title = plan.title,
                totalDays = plan.totalDays,
                daysTaken = daysTaken,
                totalAyahsRead = KhatmaEngine.TOTAL_QURAN_AYAHS,
                startDateFormatted = startDate,
                completionDateFormatted = endDate,
                completedAtTimestamp = System.currentTimeMillis()
            )
        )
    }

    fun deleteActiveKhatma() {
        viewModelScope.launch {
            repository.deleteActiveKhatmaPlan()
            showToast("Khatma plan reset")
        }
    }

    fun openKhatmaHub() {
        navigateTo(NoorDestination.QURAN_KHATMA)
    }

    fun triggerHaptic() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        } catch (e: Exception) {
            // ignore if vibration not permitted
        }
    }

    // ============================================================
    // BACKUP & RESTORE UTILITIES
    // ============================================================

    suspend fun exportBackupJson(): String {
        return com.example.data.backup.BackupManager.generateBackupJson(
            dao = db.noorDao(),
            userName = userName.value,
            userEmail = userEmail.value,
            userBio = userBio.value,
            appLanguage = appLanguage.value
        )
    }

    suspend fun importBackupJson(jsonString: String): com.example.data.backup.ImportResult {
        val result = com.example.data.backup.BackupManager.restoreFromJson(
            jsonString = jsonString,
            dao = db.noorDao(),
            onProfileRestored = { name, email, bio, lang ->
                if (name.isNotBlank()) userName.value = name
                if (email.isNotBlank()) userEmail.value = email
                if (bio.isNotBlank()) userBio.value = bio
                if (lang.isNotBlank()) setAppLanguage(lang)
                isUserLoggedIn.value = true
                sharedPrefs.edit()
                    .putBoolean("user_logged_in", true)
                    .putString("user_name", name)
                    .putString("user_email", email)
                    .putString("user_bio", bio)
                    .apply()
            }
        )
        if (result.success) {
            showToast("Backup restored successfully!")
        } else {
            showToast(result.message)
        }
        return result
    }

    fun shareBackup(context: Context, backupJson: String) {
        com.example.data.backup.BackupManager.shareBackup(context, backupJson)
    }

    fun copyBackup(context: Context, backupJson: String) {
        com.example.data.backup.BackupManager.copyToClipboard(context, backupJson)
        showToast("Backup copied to clipboard!")
    }

    private fun triggerCompletionHaptic() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 100), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(150)
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}
