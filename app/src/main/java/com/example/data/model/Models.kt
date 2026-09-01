package com.example.data.model

data class PrayerTime(
    val name: String,
    val arabicName: String,
    val timeString: String,
    val hour: Int,
    val minute: Int,
    val isNext: Boolean = false,
    val isPast: Boolean = false,
    val isCurrent: Boolean = false,
    val isCompleted: Boolean = false
)

data class SurahMeta(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalVerses: Int,
    val revelationType: String // "Meccan" or "Medinan"
)

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalVerses: Int,
    val revelationType: String, // Meccan or Medinan
    val verses: List<Verse> = emptyList()
) {
    val meta: SurahMeta
        get() = SurahMeta(
            number = number,
            nameArabic = nameArabic,
            nameEnglish = nameEnglish,
            englishMeaning = englishMeaning,
            totalVerses = totalVerses,
            revelationType = revelationType
        )
}

data class Verse(
    val surahNumber: Int,
    val verseNumber: Int,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val tafsirShort: String = "",
    val juz: Int = 1,
    val page: Int = 1,
    val absoluteAyahIndex: Int = 0
)

data class Reciter(
    val id: String,
    val name: String,
    val style: String,
    val country: String,
    val avatarUrl: String = "",
    val sampleAudioUrl: String = "",
    val nameAr: String = "",
    val styleAr: String = ""
)

data class DhikrItem(
    val id: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val defaultTarget: Int = 33,
    val virtue: String = ""
)

data class DailyMoodWisdom(
    val mood: String,
    val isIslamic: Boolean,
    val arabicText: String,
    val translation: String,
    val source: String,
    val explanation: String,
    val sourceAr: String = "",
    val explanationAr: String = ""
)

data class DuaItem(
    val id: String,
    val category: String,
    val title: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val occasion: String = "",
    val repeatCount: Int = 1,
    val benefit: String = "",
    val categoryAr: String = "",
    val referenceAr: String = ""
)

data class DuaCategory(
    val id: String,
    val titleEnglish: String,
    val titleArabic: String,
    val description: String,
    val itemCount: Int,
    val iconType: String = "dua"
)




data class PrayerZone(
    val id: String,
    val name: String,
    val arabicName: String,
    val country: String,
    val zoneLabel: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneId: String = "UTC"
)

data class CalculationAuthority(
    val id: String,
    val name: String,
    val description: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaIntervalMinutes: Int? = null
)
