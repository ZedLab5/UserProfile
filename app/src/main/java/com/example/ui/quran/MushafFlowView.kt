package com.example.ui.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Surah
import com.example.data.repository.NoorRepository
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination

val AmiriQuranFontFamily = FontFamily(
    Font(R.font.amiri_quran, FontWeight.Normal)
)

/**
 * Helper to convert standard Int ayah number into Arabic-Indic digits string (U+0660–U+0669).
 */
fun toArabicIndic(number: Int): String {
    val sb = StringBuilder()
    for (ch in number.toString()) {
        if (ch in '0'..'9') {
            sb.append((ch - '0' + 0x0660).toChar())
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

/**
 * Continuous Reading Mode:
 * Renders all ayahs of the surah as one uninterrupted, justified paragraph of Arabic text
 * in an Uthmani-style font with native U+06DD End-of-Ayah ornament and Arabic-Indic digit markers.
 * Supports active audio ayah highlighting and synchronized smooth auto-scrolling.
 */
@Composable
fun MushafFlowView(
    surah: Surah,
    fontSizeSp: Int,
    themeColors: QuranReadingThemeColors,
    isPlaying: Boolean = false,
    currentPlayingVerse: Int = 0,
    isAudioDisabled: Boolean = false,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val verses = surah.verses
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var textTopInParent by remember { mutableFloatStateOf(0f) }

    // Build a single continuous AnnotatedString with per-ayah character ranges & native ornament + digit markers
    val (annotatedText, verseRanges) = remember(
        verses,
        fontSizeSp,
        themeColors,
        isPlaying,
        currentPlayingVerse,
        surah.number
    ) {
        val ranges = mutableMapOf<Int, Pair<Int, Int>>() // verseNumber -> (startIndex, endIndex) of Arabic text only

        val builder = buildAnnotatedString {
            verses.forEachIndexed { index, verse ->
                val isCurrentlyPlaying = isPlaying && currentPlayingVerse == verse.verseNumber
                val startIndex = length
                val cleanArabic = NoorRepository.sanitizeArabicVerseText(
                    surah.number,
                    verse.verseNumber,
                    verse.arabicText.trim()
                )
                append(cleanArabic)
                val endIndex = length
                ranges[verse.verseNumber] = Pair(startIndex, endIndex)

                if (isCurrentlyPlaying) {
                    addStyle(
                        style = SpanStyle(
                            background = themeColors.accent.copy(alpha = 0.22f),
                            color = themeColors.arabicText,
                            fontWeight = FontWeight.SemiBold
                        ),
                        start = startIndex,
                        end = endIndex
                    )
                }

                // Append native End-of-Ayah ornament (U+06DD) + Arabic-Indic digit representation
                val markerStartIndex = length
                append(" \u06DD")
                append(toArabicIndic(verse.verseNumber))
                append(" ")
                val markerEndIndex = length

                addStyle(
                    style = SpanStyle(
                        color = themeColors.accent,
                        fontSize = (fontSizeSp * 0.85).sp,
                        fontWeight = FontWeight.Bold
                    ),
                    start = markerStartIndex,
                    end = markerEndIndex
                )
            }
        }

        Pair(builder, ranges)
    }

    // Auto-scroll when active playing ayah changes in Reading Mode
    LaunchedEffect(currentPlayingVerse, isPlaying, textLayoutResult, surah.number) {
        if (isPlaying) {
            if (currentPlayingVerse == 0) {
                // Basmala is reciting -> scroll to top
                scrollState.animateScrollTo(0)
            } else if (currentPlayingVerse > 0) {
                val range = verseRanges[currentPlayingVerse]
                val layout = textLayoutResult
                if (range != null && layout != null) {
                    val line = layout.getLineForOffset(range.first)
                    val lineTop = layout.getLineTop(line)
                    val targetPx = textTopInParent + lineTop - with(density) { 100.dp.toPx() }
                    scrollState.animateScrollTo(targetPx.coerceAtLeast(0f).toInt())
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(themeColors.background)
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Surah Header Banner (keeps standard banner with prev/next navigation)
        SurahHeaderBanner(
            surah = surah,
            themeColors = themeColors,
            onPreviousSurah = { viewModel.openPreviousSurah() },
            onNextSurah = { viewModel.openNextSurah() }
        )

        // Notice if MP3 audio player is active
        if (isAudioDisabled) {
            Mp3PlaybackActiveNotice()
        }

        // 2. Bismillah Header (for all except Surah 9 At-Tawbah)
        if (surah.number != 9) {
            BismillahBannerCard(
                themeColors = themeColors,
                isActive = isPlaying && currentPlayingVerse == 0
            )
        }

        // 3. Verses Rendering (Continuous justified paragraph on plain background)
        if (verses.isEmpty()) {
            QuranEmptyVersesCard(
                surah = surah,
                themeColors = themeColors,
                viewModel = viewModel,
                onRetry = { viewModel.reloadCurrentSurah() }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 8.dp)
                    .onGloballyPositioned { coordinates ->
                        textTopInParent = coordinates.positionInParent().y
                    }
            ) {
                Text(
                    text = annotatedText,
                    onTextLayout = { textLayoutResult = it },
                    style = TextStyle(
                        fontFamily = AmiriQuranFontFamily,
                        fontSize = fontSizeSp.sp,
                        lineHeight = (fontSizeSp * 2.3).sp, // Enhanced line-height for Uthmani diacritics
                        fontWeight = FontWeight.Normal,
                        color = themeColors.arabicText,
                        textAlign = TextAlign.Justify,
                        textDirection = TextDirection.Rtl
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 4. Surah Navigation Footer
        SurahNavigationFooter(
            currentSurah = surah,
            themeColors = themeColors,
            onPrevious = { viewModel.openPreviousSurah() },
            onNext = { viewModel.openNextSurah() },
            onOpenList = { viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
        )
    }
}
