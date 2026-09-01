package com.example.data.model

enum class HomeWidgetType(
    val id: String,
    val title: String,
    val description: String,
    val defaultVisible: Boolean = true
) {
    SALAT_TIMELINE(
        id = "salat_timeline",
        title = "Salat Timeline & Countdown",
        description = "Live prayer times tracker with adhan indicators",
        defaultVisible = true
    ),
    SPIRITUAL_ESSENTIALS(
        id = "spiritual_essentials",
        title = "Spiritual Essentials Grid",
        description = "Quick access to Quran, Duas, Tasbih, Salat, and Qibla",
        defaultVisible = true
    ),
    UNIFIED_STREAKS(
        id = "unified_streaks",
        title = "Daily Spiritual Consistency (Streaks)",
        description = "Unified consistency tracker for Quran, Azkar, Du'as, and Tasbih",
        defaultVisible = true
    ),
    DAILY_REVELATION(
        id = "daily_revelation",
        title = "Daily Revelation (Ayah & Dua)",
        description = "Inspirational Quranic verse and daily supplication showcase",
        defaultVisible = true
    ),
    KHATMA_TRACKER(
        id = "khatma_tracker",
        title = "Quran Khatma Tracker",
        description = "Active reading plan progress, remaining Juz, and pace status",
        defaultVisible = true
    ),
    MOOD_REFLECTION(
        id = "mood_reflection",
        title = "Heart & Soul Reflection Engine",
        description = "Spiritual guidance tailored to your emotional state",
        defaultVisible = true
    ),
    AUDIO_RECITERS(
        id = "audio_reciters",
        title = "Quran Reciters Showcase",
        description = "Listen to Mishary Alafasy, Abdul Basit, and master Qaris",
        defaultVisible = true
    ),
    PREMIUM_BANNER(
        id = "premium_banner",
        title = "Noor Plus Banner",
        description = "Offline audio and deep reflection highlights",
        defaultVisible = false
    );

    companion object {
        fun defaultOrderedList(): List<HomeWidgetType> = listOf(
            SPIRITUAL_ESSENTIALS,
            SALAT_TIMELINE,
            UNIFIED_STREAKS,
            DAILY_REVELATION,
            KHATMA_TRACKER,
            MOOD_REFLECTION,
            AUDIO_RECITERS,
            PREMIUM_BANNER
        )
    }
}
