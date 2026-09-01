package com.example.data.quran

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

data class TajweedRuleInfo(
    val name: String,
    val nameArabic: String,
    val color: Color,
    val description: String,
    val letters: String
)

object TajweedHelper {

    val GhunnahColor = Color(0xFF10B981) // Emerald Green
    val QalqalahColor = Color(0xFFF59E0B) // Amber Gold
    val MaddColor = Color(0xFF3B82F6) // Sapphire Blue
    val IkhfaColor = Color(0xFFA855F7) // Purple / Orchid
    val IdghamColor = Color(0xFF06B6D4) // Cyan / Teal

    val rulesList = listOf(
        TajweedRuleInfo(
            name = "Ghunnah",
            nameArabic = "غُنَّة",
            color = GhunnahColor,
            description = "Nasal resonance held for 2 counts on Noon or Meem with Shaddah (نّ / مّ).",
            letters = "نّ, مّ"
        ),
        TajweedRuleInfo(
            name = "Qalqalah",
            nameArabic = "قَلْقَلَة",
            color = QalqalahColor,
            description = "Echoing or bouncing sound produced on five letters when with Sukun or stopped.",
            letters = "ق - ط - ب - ج - د (قُطْبُ جَدٍّ)"
        ),
        TajweedRuleInfo(
            name = "Madd (Elongation)",
            nameArabic = "مَدّ",
            color = MaddColor,
            description = "Prolongation of the vowel sound for 4 to 6 counts.",
            letters = "آ, ــٰ, ــُو, ــِي"
        ),
        TajweedRuleInfo(
            name = "Ikhfa & Idgham",
            nameArabic = "إِخْفَاء وَإِدْغَام",
            color = IkhfaColor,
            description = "Hiding or merging of Noon Sakinah or Tanween into adjacent letters.",
            letters = "ي - ر - م - ل - و - ن / حروف الإخفاء"
        )
    )

    fun buildTajweedAnnotatedString(
        arabicText: String,
        isEnabled: Boolean,
        defaultTextColor: Color
    ): AnnotatedString {
        if (!isEnabled) {
            return AnnotatedString(arabicText)
        }

        return buildAnnotatedString {
            append(arabicText)

            // Combining Arabic diacritic marks range
            val diacritics = "[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]*"

            // 1. Highlight Ghunnah (نّ and مّ with shaddah and accompanying marks)
            val ghunnahRegex = Regex("[نم]ّ$diacritics")
            ghunnahRegex.findAll(arabicText).forEach { match ->
                addStyle(
                    style = SpanStyle(color = GhunnahColor),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }

            // 2. Highlight Qalqalah (قطب جد with sukun or at pause)
            val qalqalahRegex = Regex("[قطبجد][ْ]|$diacritics[قطبجد]ْ?")
            qalqalahRegex.findAll(arabicText).forEach { match ->
                val str = match.value
                if (str.contains('ْ') || match.range.last >= arabicText.length - 2) {
                    addStyle(
                        style = SpanStyle(color = QalqalahColor),
                        start = match.range.first,
                        end = match.range.last + 1
                    )
                }
            }

            // 3. Highlight Madd (آ, dagger alif ٰ, maddah ٓ, or elongated vowels)
            val maddRegex = Regex("[آٓـٰ]$diacritics|[اوي]ٓ$diacritics")
            maddRegex.findAll(arabicText).forEach { match ->
                addStyle(
                    style = SpanStyle(color = MaddColor),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }
        }
    }
}
