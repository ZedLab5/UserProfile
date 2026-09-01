package com.example.ui.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.quran.KhatmaEngine
import com.example.ui.MainViewModel
import com.example.data.localization.tr
import com.example.ui.NoorDestination
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceWhite
import kotlinx.coroutines.launch

data class QuranReadingThemeColors(
    val background: Color,
    val surface: Color,
    val border: Color,
    val arabicText: Color,
    val translationText: Color,
    val transliterationText: Color,
    val accent: Color,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentSurah by viewModel.selectedSurahForReading.collectAsStateWithLifecycle()
    val fontSizeSp by viewModel.arabicFontSizeSp.collectAsStateWithLifecycle()
    val showTransliteration by viewModel.showTransliteration.collectAsStateWithLifecycle()
    val showTranslation by viewModel.showTranslation.collectAsStateWithLifecycle()
    val readingThemeName by viewModel.quranReadingTheme.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val currentPlayingVerse by viewModel.currentPlayingVerse.collectAsStateWithLifecycle()
    val currentPlayingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val isAyahAudioMode by viewModel.isAyahAudioMode.collectAsStateWithLifecycle()
    val isMp3PlayerRunning = isAudioPlaying && !isAyahAudioMode
    val isCurrentSurahPlaying = isAudioPlaying && currentPlayingSurah.number == currentSurah.number
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val targetAyahToScrollTo by viewModel.targetAyahToScrollTo.collectAsStateWithLifecycle()
    val khatmaState by viewModel.khatmaDashboardState.collectAsStateWithLifecycle()
    val isMushafFlowMode by viewModel.isMushafFlowMode.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showSettingsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Automatically stop Ayah recitation audio whenever user leaves the Quran page
    DisposableEffect(Unit) {
        onDispose {
            if (viewModel.isAyahAudioMode.value) {
                viewModel.stopAudio()
            }
        }
    }

    // Record daily Quran reading streak activity
    LaunchedEffect(currentSurah.number) {
        viewModel.recordQuranActivity()
    }

    // Auto-scroll to target bookmarked Ayah (only if targetAyahToScrollTo > 1, otherwise always open from the top at item 0)
    LaunchedEffect(targetAyahToScrollTo, currentSurah.number) {
        if (targetAyahToScrollTo > 1 && currentSurah.number != 1) {
            var offset = 1 // SurahHeaderBanner
            if (isMp3PlayerRunning) offset++
            if (currentSurah.number != 9) offset++
            val targetIndex = (targetAyahToScrollTo - 1 + offset).coerceIn(0, currentSurah.verses.size + offset)
            listState.animateScrollToItem(targetIndex)
        } else {
            listState.scrollToItem(0)
        }
    }

    // Smart Auto-Scrolling Audio Player: dynamically follow active Ayah only during Ayah-by-Ayah recitation mode
    LaunchedEffect(currentPlayingVerse, isAudioPlaying, isAyahAudioMode, currentPlayingSurah.number, currentSurah.number) {
        if (isAudioPlaying && isAyahAudioMode && currentPlayingSurah.number == currentSurah.number) {
            var offset = 1 // SurahHeaderBanner
            if (isMp3PlayerRunning) offset++
            if (currentSurah.number != 9) offset++

            if (currentPlayingVerse == 0 && currentSurah.number != 9) {
                val bismillahIndex = if (isMp3PlayerRunning) 2 else 1
                listState.animateScrollToItem(bismillahIndex)
            } else if (currentPlayingVerse >= 1) {
                val targetIndex = (currentPlayingVerse - 1 + offset).coerceIn(0, currentSurah.verses.size + offset)
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    // Determine current theme colors
    val themeColors = when (readingThemeName) {
        "Sepia Parchment" -> QuranReadingThemeColors(
            background = Color(0xFFF9F4E8),
            surface = Color(0xFFFFFDF5),
            border = Color(0xFFE8DCC2),
            arabicText = Color(0xFF2C221E),
            translationText = Color(0xFF5C4F48),
            transliterationText = Color(0xFF8C7355),
            accent = Color(0xFFB57E1A),
            name = "Sepia Parchment"
        )
        "Obsidian Night" -> QuranReadingThemeColors(
            background = Color(0xFF0F1418),
            surface = Color(0xFF182026),
            border = Color(0xFF26333C),
            arabicText = Color(0xFFE2E8F0),
            translationText = Color(0xFF94A3B8),
            transliterationText = Color(0xFF38BDF8),
            accent = Color(0xFF10B981),
            name = "Obsidian Night"
        )
        "Emerald Noor" -> QuranReadingThemeColors(
            background = Color(0xFFEBF7F5),
            surface = Color(0xFFF4FAF9),
            border = Color(0xFFBDE3DC),
            arabicText = Color(0xFF0F2E2B),
            translationText = Color(0xFF2D5A54),
            transliterationText = Color(0xFF1A7A6E),
            accent = DeepVibrantTeal,
            name = "Emerald Noor"
        )
        else -> QuranReadingThemeColors(
            background = CanvasMint,
            surface = SurfaceWhite,
            border = BorderTealGray,
            arabicText = DarkPine,
            translationText = Color(0xFF334E4A),
            transliterationText = DeepVibrantTeal,
            accent = DeepVibrantTeal,
            name = "Madani Crisp"
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                NoorTopBar(
                    title = "${currentSurah.number}. ${currentSurah.nameEnglish}",
                    eyebrow = "${currentSurah.revelationType.uppercase()} • ${currentSurah.nameArabic}",
                    subtitle = "${currentSurah.totalVerses} Ayahs • ${currentSurah.englishMeaning}",
                    onBackClick = { viewModel.navigateBack() },
                    backContentDescription = "Back",
                    actions = {
                        // Quick Toggle: Mushaf Flow Mode
                        NoorGlassIconButton(
                            onClick = { viewModel.toggleMushafFlowMode() },
                            icon = Icons.Default.ViewAgenda,
                            contentDescription = "Distraction-Free Mushaf Flow",
                            isActive = isMushafFlowMode
                        )

                        // Minimal Audio Play/Pause Button for Reading & Listening
                        NoorGlassIconButton(
                            onClick = {
                                if (isMp3PlayerRunning) {
                                    viewModel.showToast("MP3 player is active. Pause it using the floating bar to start recitation here.")
                                } else if (isCurrentSurahPlaying && isAyahAudioMode) {
                                    viewModel.toggleAudioPlayback(currentSurah)
                                } else {
                                    val startVerse = if (currentPlayingVerse > 0) currentPlayingVerse else 1
                                    viewModel.playAyah(currentSurah, startVerse, openPlayer = false)
                                }
                            },
                            icon = if (isCurrentSurahPlaying && isAyahAudioMode) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isCurrentSurahPlaying && isAyahAudioMode) "Pause Recitation" else "Play Recitation",
                            isActive = isCurrentSurahPlaying && isAyahAudioMode
                        )

                        // Reading Display Settings
                        NoorGlassIconButton(
                            onClick = { showSettingsSheet = true },
                            icon = Icons.Default.Settings,
                            contentDescription = "Reading Settings"
                        )
                    }
                )
            },
            containerColor = themeColors.background,
            modifier = modifier
        ) { paddingValues ->
            if (isMushafFlowMode) {
                // Distraction-Free Continuous Reading Mode (Pure Uthmani text)
                MushafFlowView(
                    surah = currentSurah,
                    fontSizeSp = fontSizeSp,
                    themeColors = themeColors,
                    isPlaying = isCurrentSurahPlaying && isAyahAudioMode,
                    currentPlayingVerse = currentPlayingVerse,
                    isAudioDisabled = isMp3PlayerRunning,
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Surah Header Banner with Previous / Next Navigation
                    item(key = "surah_header") {
                        SurahHeaderBanner(
                            surah = currentSurah,
                            themeColors = themeColors,
                            onPreviousSurah = { viewModel.openPreviousSurah() },
                            onNextSurah = { viewModel.openNextSurah() }
                        )
                    }

                    // Static audio notice directly beneath hero section when MP3 player is active
                    if (isMp3PlayerRunning) {
                        item(key = "mp3_audio_notice") {
                            Mp3PlaybackActiveNotice()
                        }
                    }

                    // Bismillah Header (for all except Surah 9 At-Tawbah)
                    if (currentSurah.number != 9) {
                        item(key = "bismillah_card") {
                            BismillahBannerCard(
                                themeColors = themeColors,
                                isActive = isCurrentSurahPlaying && isAyahAudioMode && currentPlayingVerse == 0
                            )
                        }
                    }

                    // Empty state fallback if verses are still loading or empty
                    if (currentSurah.verses.isEmpty()) {
                        item(key = "empty_verses_card") {
                            QuranEmptyVersesCard(
                                surah = currentSurah,
                                themeColors = themeColors,
                                viewModel = viewModel,
                                onRetry = { viewModel.reloadCurrentSurah() }
                            )
                        }
                    } else {
                        // Verses List
                        items(
                            items = currentSurah.verses,
                            key = { "${currentSurah.number}_${it.verseNumber}" }
                        ) { verse ->
                            val isVerseActive = isCurrentSurahPlaying && currentPlayingVerse == verse.verseNumber
                            val isFavorite = favorites.any { it.title.contains("Surah ${currentSurah.nameEnglish} Ayah ${verse.verseNumber}") }
                            val isExactBookmark = readingProgress?.surahNumber == currentSurah.number &&
                                    readingProgress?.ayahNumber == verse.verseNumber

                            val currentPlan = khatmaState?.plan
                            val isKhatmaActive = currentPlan != null && !currentPlan.isCompleted
                            val currentReadCount = currentPlan?.readAyahsCount ?: 0
                            val verseAbsIndex = remember(currentSurah.number, verse.verseNumber) {
                                KhatmaEngine.getAbsoluteAyahIndex(currentSurah.number, verse.verseNumber)
                            }
                            val isKhatmaRead = isKhatmaActive && verseAbsIndex <= currentReadCount
                            val isKhatmaCurrentPointer = isKhatmaActive && verseAbsIndex == currentReadCount

                            VerseCardItem(
                                verse = verse,
                                surah = currentSurah,
                                fontSizeSp = fontSizeSp,
                                showTransliteration = showTransliteration,
                                showTranslation = showTranslation,
                                isActive = isVerseActive && isAyahAudioMode,
                                isBookmarked = isFavorite,
                                isReadingBookmark = isExactBookmark,
                                isKhatmaActive = isKhatmaActive,
                                isKhatmaRead = isKhatmaRead,
                                isKhatmaCurrentPointer = isKhatmaCurrentPointer,
                                isAudioDisabled = isMp3PlayerRunning,
                                themeColors = themeColors,
                                onPlayVerse = {
                                    if (isMp3PlayerRunning) {
                                        viewModel.showToast("MP3 player is currently active. Pause it to recite individual verses.")
                                    } else if (isCurrentSurahPlaying && isAyahAudioMode && currentPlayingVerse == verse.verseNumber) {
                                        viewModel.toggleAudioPlayback(currentSurah)
                                    } else {
                                        viewModel.playAyah(currentSurah, verse.verseNumber)
                                    }
                                },
                                onToggleBookmark = {
                                    viewModel.saveExactReadingBookmark(currentSurah, verse.verseNumber)
                                },
                                onMarkKhatma = {
                                    viewModel.markKhatmaProgressToVerse(currentSurah, verse.verseNumber)
                                },
                                onCopyVerse = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(
                                        "Ayah ${currentSurah.nameEnglish} ${verse.verseNumber}",
                                        "${verse.arabicText}\n\n${verse.transliteration}\n\n${verse.translation}\n[Qur'an ${currentSurah.number}:${verse.verseNumber}]"
                                    )
                                    clipboard.setPrimaryClip(clip)
                                    viewModel.showToast("Ayah copied to clipboard!")
                                }
                            )
                        }
                    }

                    // Next / Previous Surah Navigation Footer
                    item(key = "surah_nav_footer") {
                        SurahNavigationFooter(
                            currentSurah = currentSurah,
                            themeColors = themeColors,
                            onPrevious = { viewModel.openPreviousSurah() },
                            onNext = { viewModel.openNextSurah() },
                            onOpenList = { viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
                        )
                    }
                }
            }
        }

        // Reading Settings Modal Bottom Sheet
        if (showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSettingsSheet = false },
                sheetState = sheetState,
                containerColor = themeColors.surface
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reading Preferences",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText,
                                    fontSize = 19.sp
                                )
                            )

                            IconButton(onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showSettingsSheet = false
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = themeColors.arabicText
                                )
                            }
                        }

                        // 1. Arabic Font Size Controls (Straightforward Predefined Options: Small, Medium, Large)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = null,
                                        tint = themeColors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Arabic Font Size",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText
                                        )
                                    )
                                }
                                Text(
                                    text = "${fontSizeSp}sp",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.accent
                                    )
                                )
                            }

                            // Predefined Selector Tabs (Small, Medium, Large)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(themeColors.background)
                                    .border(1.dp, themeColors.border, RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val fontOptions = listOf("Small" to 22, "Medium" to 28, "Large" to 34)
                                fontOptions.forEach { (label, spSize) ->
                                    val isSelected = when (label) {
                                        "Small" -> fontSizeSp <= 24
                                        "Medium" -> fontSizeSp in 25..30
                                        else -> fontSizeSp > 30
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) themeColors.accent else Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.arabicFontSizeSp.value = spSize }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else themeColors.arabicText,
                                                    fontSize = 13.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Reading Canvas Theme Options (Tightened vertical padding)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Reading Canvas Theme",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText
                                )
                            )

                            val themes = listOf(
                                "Madani Crisp" to Color(0xFFF2FBF9),
                                "Sepia Parchment" to Color(0xFFF9F4E8),
                                "Obsidian Night" to Color(0xFF0F1418),
                                "Emerald Noor" to Color(0xFFEBF7F5)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                themes.forEach { (name, color) ->
                                    val isSelected = readingThemeName == name
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.quranReadingTheme.value = name },
                                        shape = RoundedCornerShape(10.dp),
                                        color = color,
                                        border = BorderStroke(
                                            if (isSelected) 1.8.dp else 1.dp,
                                            if (isSelected) themeColors.accent else themeColors.border.copy(alpha = 0.5f)
                                        ),
                                        shadowElevation = if (isSelected) 1.dp else 0.dp
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = name.split(" ").first(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (name == "Obsidian Night") Color.White else if (isSelected) themeColors.accent else DarkPine
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Translation & Transliteration Toggles
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "English Translation",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText
                                        )
                                    )
                                    Text(
                                        text = "Clear Sahih International translation",
                                        style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                                    )
                                }
                                Switch(
                                    checked = showTranslation,
                                    onCheckedChange = { viewModel.showTranslation.value = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = themeColors.accent
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Phonetic Transliteration",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText
                                        )
                                    )
                                    Text(
                                        text = "Helps non-Arabic readers pronounce correctly",
                                        style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                                    )
                                }
                                Switch(
                                    checked = showTransliteration,
                                    onCheckedChange = { viewModel.showTransliteration.value = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = themeColors.accent
                                    )
                                )
                            }

                            HorizontalDivider(color = themeColors.border.copy(alpha = 0.5f))

                            // Mushaf Flow Mode Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Distraction-Free Mushaf Flow",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText
                                        )
                                    )
                                    Text(
                                        text = "Continuous Arabic calligraphy with inline verse markers",
                                        style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                                    )
                                }
                                Switch(
                                    checked = isMushafFlowMode,
                                    onCheckedChange = { viewModel.toggleMushafFlowMode() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = themeColors.accent
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SurahHeaderBanner(
    surah: Surah,
    themeColors: QuranReadingThemeColors,
    onPreviousSurah: () -> Unit,
    onNextSurah: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0D151C),
        border = BorderStroke(1.2.dp, MetallicGold.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_pinterest_hero),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.40f,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x660D151C),
                                Color(0x990D151C),
                                Color(0xE60D151C)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Navigation Bar: Left: Surah position | Right: Prev/Next buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Surah Position Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        border = BorderStroke(0.8.dp, MetallicGold.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Surah ${surah.number} / 114",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }

                    // Navigation Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (surah.number > 1) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.10f),
                                border = BorderStroke(0.8.dp, MetallicGold.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable(onClick = onPreviousSurah)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Previous Surah",
                                        tint = MetallicGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Prev",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (surah.number < 114) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.10f),
                                border = BorderStroke(0.8.dp, MetallicGold.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable(onClick = onNextSurah)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Next",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next Surah",
                                        tint = MetallicGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Left-to-Right Hero Details Row (Left: English Names & Metadata | Right: Arabic Calligraphy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Revelation Type & Verses Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MetallicGold.copy(alpha = 0.20f),
                                border = BorderStroke(0.8.dp, MetallicGold.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = surah.revelationType.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MetallicGold,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = "${surah.totalVerses} Ayahs",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        // English Name
                        Text(
                            text = surah.nameEnglish,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 22.sp
                            )
                        )

                        // Meaning
                        Text(
                            text = "\"${surah.englishMeaning}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MetallicGold,
                                fontSize = 13.sp
                            )
                        )
                    }

                    // Arabic Calligraphy on Right
                    Text(
                        text = "سُورَةُ ${surah.nameArabic}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 25.sp
                        ),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun Mp3PlaybackActiveNotice(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFF2F2),
        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ayah Audio Playback Unavailable",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF991B1B),
                        fontSize = 13.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "The MP3 player is currently active. Pause it to enable Ayah recitation.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun QuranEmptyVersesCard(
    surah: Surah,
    themeColors: QuranReadingThemeColors,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = themeColors.surface,
        border = BorderStroke(1.dp, themeColors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(themeColors.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = themeColors.accent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = String.format(tr("surah_format", viewModel), surah.nameEnglish, surah.nameArabic),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = tr("verses_loading_message", viewModel),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = themeColors.translationText
                ),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = tr("load_verses_button", viewModel),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun BismillahBannerCard(
    themeColors: QuranReadingThemeColors,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) themeColors.accent.copy(alpha = 0.12f) else themeColors.surface,
        border = BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = if (isActive) themeColors.accent else themeColors.border
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "In the Name of Allah, the Most Gracious, the Most Merciful",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = themeColors.translationText.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun VerseCardItem(
    verse: Verse,
    surah: Surah,
    fontSizeSp: Int,
    showTransliteration: Boolean,
    showTranslation: Boolean,
    isActive: Boolean,
    isBookmarked: Boolean,
    isReadingBookmark: Boolean,
    isKhatmaActive: Boolean = false,
    isKhatmaRead: Boolean = false,
    isKhatmaCurrentPointer: Boolean = false,
    isAudioDisabled: Boolean = false,
    themeColors: QuranReadingThemeColors,
    onPlayVerse: () -> Unit,
    onToggleBookmark: () -> Unit,
    onMarkKhatma: () -> Unit = {},
    onCopyVerse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) themeColors.accent.copy(alpha = 0.08f) else themeColors.surface,
        border = BorderStroke(
            if (isActive) 1.5.dp else if (isKhatmaCurrentPointer || isReadingBookmark) 1.2.dp else 0.8.dp,
            if (isActive) themeColors.accent else if (isKhatmaCurrentPointer) Color(0xFF10B981) else if (isReadingBookmark) MetallicGold else themeColors.border.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Verse Header Bar (Left: Clean Verse Number Pill & Bookmark/Khatma labels | Right: Ghost Line Action Icons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Verse Number + (Bookmark OR Khatma Bookmark swapped based on active Khatma)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isActive) themeColors.accent else themeColors.accent.copy(alpha = 0.10f),
                        border = BorderStroke(0.8.dp, themeColors.accent.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "${surah.number}:${verse.verseNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color.White else themeColors.accent,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (!isKhatmaActive) {
                        // Normal Reading Mode: Only Normal Bookmark appears
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isReadingBookmark || isBookmarked) GoldBadgeBg else Color.Transparent,
                            border = BorderStroke(
                                0.8.dp,
                                if (isReadingBookmark || isBookmarked) MetallicGold.copy(alpha = 0.6f) else themeColors.border.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.clickable { onToggleBookmark() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = if (isReadingBookmark || isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isReadingBookmark || isBookmarked) MetallicGold else themeColors.translationText.copy(alpha = 0.7f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isReadingBookmark || isBookmarked) "Bookmarked" else "Bookmark",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isReadingBookmark || isBookmarked) MetallicGold else themeColors.translationText.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                    } else {
                        // Khatma Mode Active: Normal bookmark is swapped with Khatma bookmark
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                isKhatmaCurrentPointer -> Color(0xFFFEF3C7)
                                isKhatmaRead -> SoftTealTint
                                else -> Color.Transparent
                            },
                            border = BorderStroke(
                                0.8.dp,
                                when {
                                    isKhatmaCurrentPointer -> Color(0xFFF59E0B)
                                    isKhatmaRead -> DeepVibrantTeal.copy(alpha = 0.4f)
                                    else -> themeColors.border.copy(alpha = 0.7f)
                                }
                            ),
                            modifier = Modifier.clickable { onMarkKhatma() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = if (isKhatmaRead || isKhatmaCurrentPointer) Icons.Default.CheckCircle else Icons.Default.AutoStories,
                                    contentDescription = "Khatma Progress",
                                    tint = when {
                                        isKhatmaCurrentPointer -> Color(0xFFD97706)
                                        isKhatmaRead -> DeepVibrantTeal
                                        else -> themeColors.translationText.copy(alpha = 0.7f)
                                    },
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = when {
                                        isKhatmaCurrentPointer -> "Current"
                                        isKhatmaRead -> "Khatma Done"
                                        else -> "Mark Khatma"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isKhatmaCurrentPointer -> Color(0xFFD97706)
                                            isKhatmaRead -> DeepVibrantTeal
                                            else -> themeColors.translationText.copy(alpha = 0.8f)
                                        }
                                    )
                                )
                            }
                        }
                    }
                }

                // Right Side: Action Icons (Audio, Copy) - Flag/Bookmark icon removed to avoid repetition
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Audio Recite Icon
                    IconButton(
                        onClick = onPlayVerse,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Recite Ayah",
                            tint = if (isAudioDisabled) {
                                themeColors.translationText.copy(alpha = 0.28f)
                            } else if (isActive) {
                                MetallicGold
                            } else {
                                themeColors.translationText.copy(alpha = 0.65f)
                            },
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Copy Icon
                    IconButton(
                        onClick = onCopyVerse,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Ayah Text",
                            tint = themeColors.translationText.copy(alpha = 0.65f),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // Pure Arabic Text Display (Centered/Right-aligned, beautifully rendered)
            Text(
                text = verse.arabicText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp * 1.75).sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText
                ),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            // Transliteration (if enabled)
            if (showTransliteration && verse.transliteration.isNotBlank()) {
                Text(
                    text = verse.transliteration,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeColors.transliterationText,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // English Translation (if enabled)
            if (showTranslation && verse.translation.isNotBlank()) {
                Text(
                    text = verse.translation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeColors.translationText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            }

            // Short Tafsir / Spiritual Context (if available)
            if (verse.tafsirShort.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = themeColors.background,
                    border = BorderStroke(0.8.dp, themeColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tafsir Note",
                            tint = themeColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = verse.tafsirShort,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurahNavigationFooter(
    currentSurah: Surah,
    themeColors: QuranReadingThemeColors,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onOpenList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = themeColors.surface,
        border = BorderStroke(1.dp, themeColors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = currentSurah.number > 1,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = themeColors.accent
                    ),
                    border = BorderStroke(1.dp, themeColors.border)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Surah",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Prev Surah",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = onOpenList,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.accent,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "All Surahs",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "114 Surahs",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = onNext,
                    enabled = currentSurah.number < 114,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = themeColors.accent
                    ),
                    border = BorderStroke(1.dp, themeColors.border)
                ) {
                    Text(
                        text = "Next Surah",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Surah",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
