package com.example.ui.duas

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DuaItem
import com.example.data.quran.DuaData
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarReaderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedDuaCategory.collectAsStateWithLifecycle()
    val azkarCountsMap by viewModel.azkarRemainingCounts.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    // Settings preferences from ViewModel
    val showArabicInCards by viewModel.showArabicInAzkarCards.collectAsStateWithLifecycle()
    val azkarTextSize by viewModel.azkarTextSize.collectAsStateWithLifecycle()
    val isAutoScrollEnabled by viewModel.isAzkarAutoScrollEnabled.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isAzkarHapticEnabled.collectAsStateWithLifecycle()
    val showTransliteration by viewModel.showAzkarTransliteration.collectAsStateWithLifecycle()
    val showBenefits by viewModel.showAzkarBenefits.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabicPrimary = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية"

    var showSettingsSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(selectedCategory) {
        viewModel.recordAzkarActivity()
    }

    val currentCategoryModel = remember(selectedCategory) {
        DuaData.categories.firstOrNull { it.id.equals(selectedCategory, ignoreCase = true) }
            ?: DuaData.categories.first()
    }

    val duasList = remember(selectedCategory) {
        DuaData.categorizedDuas.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val completedCount = remember(duasList, azkarCountsMap) {
        duasList.count { dua -> (azkarCountsMap[dua.id] ?: dua.repeatCount) == 0 }
    }

    val totalCount = duasList.size
    val categoryProgress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    Scaffold(
        topBar = {
            NoorTopBar(
                title = currentCategoryModel.titleEnglish,
                eyebrow = if (currentCategoryModel.titleArabic.isNotBlank()) currentCategoryModel.titleArabic else "HISN AL-MUSLIM",
                subtitle = "$completedCount of $totalCount Completed • Daily Protection",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                actions = {
                    // Reset counts button
                    NoorGlassIconButton(
                        onClick = { viewModel.resetCategoryDuaCounts(selectedCategory) },
                        icon = Icons.Default.Refresh,
                        contentDescription = "Reset Counts"
                    )

                    // Settings Button
                    NoorGlassIconButton(
                        onClick = { showSettingsSheet = true },
                        icon = Icons.Default.Tune,
                        contentDescription = "Azkar Settings"
                    )
                }
            )
        },
        containerColor = CanvasMint,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily Completion Progress Card with Gold Circular Progress Indicator on Right Side
            item(key = "category_progress_banner") {
                AzkarDailyCompletionCard(
                    categoryName = currentCategoryModel.titleEnglish,
                    description = currentCategoryModel.description,
                    completed = completedCount,
                    total = totalCount,
                    progress = categoryProgress
                )
            }

            // Duas & Azkar Cards
            itemsIndexed(
                items = duasList,
                key = { _, dua -> dua.id }
            ) { index, dua ->
                val remaining = azkarCountsMap[dua.id] ?: dua.repeatCount
                val isCompleted = remaining == 0
                val isBookmarked = favorites.any { it.title == dua.title }

                InteractiveAzkarCard(
                    dua = dua,
                    remainingCount = remaining,
                    isCompleted = isCompleted,
                    isBookmarked = isBookmarked,
                    showArabic = showArabicInCards,
                    textSize = azkarTextSize,
                    showTransliteration = showTransliteration,
                    showBenefits = showBenefits,
                    isArabicPrimary = isArabicPrimary,
                    onTapCount = {
                        viewModel.decrementDuaCount(dua) {
                            // On completed callback -> Auto-scroll to next Zikr card if enabled
                            if (isAutoScrollEnabled) {
                                coroutineScope.launch {
                                    delay(280) // Graceful moment to see checkmark state
                                    val nextIndex = index + 1
                                    if (nextIndex < duasList.size) {
                                        // +1 offset for the header progress card at index 0
                                        listState.animateScrollToItem(nextIndex + 1)
                                    }
                                }
                            }
                        }
                    },
                    onResetCount = { viewModel.resetDuaCount(dua) },
                    onToggleBookmark = {
                        viewModel.toggleFavorite(
                            itemType = "DUA",
                            title = dua.title,
                            subtitle = if (showArabicInCards) dua.arabicText else dua.translation,
                            details = "${dua.translation}\n\n[${dua.reference}]"
                        )
                    },
                    onCopyDua = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val textToCopy = if (showArabicInCards) {
                            "${dua.title}\n\n${dua.arabicText}\n\n${dua.transliteration}\n\n${dua.translation}\n[${dua.reference}]"
                        } else {
                            "${dua.title}\n\n${dua.transliteration}\n\n${dua.translation}\n[${dua.reference}]"
                        }
                        val clip = ClipData.newPlainText(dua.title, textToCopy)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showToast("Zikr copied to clipboard!")
                    }
                )
            }

            // Footer Navigation (Return to Categories Directory)
            item(key = "footer_nav") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(NoorDestination.DUAS_LIBRARY) },
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceWhite,
                    border = BorderStroke(1.dp, BorderTealGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SoftTealTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = DeepVibrantTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "All Du'a Categories",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkPine
                                    )
                                )
                                Text(
                                    text = "Browse Hisn al-Muslim library",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateTealMuted,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Azkar Settings Modal Bottom Sheet
    if (showSettingsSheet) {
        AzkarSettingsBottomSheet(
            showArabic = showArabicInCards,
            textSize = azkarTextSize,
            isAutoScroll = isAutoScrollEnabled,
            isHaptic = isHapticEnabled,
            showTransliteration = showTransliteration,
            showBenefits = showBenefits,
            categoryName = currentCategoryModel.titleEnglish,
            onDismiss = { showSettingsSheet = false },
            onToggleArabic = { viewModel.setShowArabicInAzkarCards(it) },
            onSelectTextSize = { viewModel.setAzkarTextSize(it) },
            onToggleAutoScroll = { viewModel.setAzkarAutoScroll(it) },
            onToggleHaptic = { viewModel.setAzkarHaptic(it) },
            onToggleTransliteration = { viewModel.setAzkarTransliteration(it) },
            onToggleBenefits = { viewModel.setAzkarBenefits(it) },
            onResetCategory = {
                viewModel.resetCategoryDuaCounts(selectedCategory)
                showSettingsSheet = false
            }
        )
    }
}

/**
 * Daily Completion section with Gold Circular Progress Indicator on the right side.
 * Strictly English content — all Arabic text stripped.
 */
@Composable
fun AzkarDailyCompletionCard(
    categoryName: String,
    description: String,
    completed: Int,
    total: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "goldCircularProgress"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.2.dp, BorderTealGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFCFDFD),
                            Color(0xFFF2FAF8),
                            Color(0xFFFFFDF5)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left & Center Column (Clean English Description & Metadata)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldBadgeBg,
                        border = BorderStroke(1.dp, MetallicGold.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "DAILY COMPLETION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8A5F0C),
                                fontSize = 10.5.sp,
                                letterSpacing = 0.8.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine,
                            fontSize = 17.5.sp
                        )
                    )

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 12.5.sp,
                            lineHeight = 16.5.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (completed == total && total > 0) Color(0xFF16A34A) else MetallicGold)
                        )
                        Text(
                            text = if (completed == total && total > 0) "All Finished ✓" else "$completed of $total Completed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (completed == total && total > 0) Color(0xFF15803D) else DarkPine,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Right Side: Gold Circular Progress Indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(76.dp)
                ) {
                    // Soft Gold Track Circle
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(76.dp),
                        color = Color(0xFFFBEBC8),
                        strokeWidth = 6.5.dp,
                        strokeCap = StrokeCap.Round
                    )

                    // Active Metallic Gold Arc
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(76.dp),
                        color = MetallicGold,
                        strokeWidth = 6.5.dp,
                        strokeCap = StrokeCap.Round
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF8A5F0C),
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "$completed/$total",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SlateTealMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual Zikr Card with customizable font size, optional Arabic display,
 * and a prominent, full-width Action Button positioned at the bottom of the card.
 */
@Composable
fun InteractiveAzkarCard(
    dua: DuaItem,
    remainingCount: Int,
    isCompleted: Boolean,
    isBookmarked: Boolean,
    showArabic: Boolean,
    textSize: String,
    showTransliteration: Boolean,
    showBenefits: Boolean,
    isArabicPrimary: Boolean = false,
    onTapCount: () -> Unit,
    onResetCount: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopyDua: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "actionBtnScale"
    )

    // Dynamic Font Sizes based on user preference
    val translationSize = when (textSize) {
        "Small" -> 14.sp
        "Large" -> 17.5.sp
        "Extra Large" -> 20.sp
        else -> 15.5.sp // Medium
    }
    val translationLineHeight = when (textSize) {
        "Small" -> 21.sp
        "Large" -> 25.sp
        "Extra Large" -> 29.sp
        else -> 23.sp
    }

    val transliterationSize = when (textSize) {
        "Small" -> 12.5.sp
        "Large" -> 15.5.sp
        "Extra Large" -> 17.5.sp
        else -> 14.sp
    }

    val arabicSize = when (textSize) {
        "Small" -> 19.sp
        "Large" -> 25.sp
        "Extra Large" -> 29.sp
        else -> 22.sp
    }
    val arabicLineHeight = when (textSize) {
        "Small" -> 32.sp
        "Large" -> 40.sp
        "Extra Large" -> 46.sp
        else -> 36.sp
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (isCompleted) Color(0xFFF6FDFB) else SurfaceWhite,
        border = BorderStroke(
            if (isCompleted) 1.5.dp else 1.dp,
            if (isCompleted) Color(0xFF22C55E).copy(alpha = 0.55f) else BorderTealGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar: Title, Source Reference & Secondary Actions (Bookmark, Copy)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dua.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "${dua.occasion} • ${dua.reference}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 12.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onCopyDua,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Zikr",
                            tint = SlateTealMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MetallicGold else SlateTealMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (!isArabicPrimary) {
                // ============================================================
                // PRIMARY LANGUAGE = ENGLISH (Default / Selected)
                // 1. Primary Container: English Translation (Prominent Box with distinct background & bold typography)
                // ============================================================
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CanvasMint,
                    border = BorderStroke(1.dp, BorderTealGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dua.translation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DarkPine,
                            fontSize = translationSize,
                            lineHeight = translationLineHeight
                        ),
                        modifier = Modifier.padding(14.dp)
                    )
                }

                // 2. Secondary Layer: Phonetic Transliteration (sits beneath/outside container)
                if (showTransliteration && dua.transliteration.isNotBlank()) {
                    Text(
                        text = dua.transliteration,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = DeepVibrantTeal,
                            fontSize = transliterationSize,
                            lineHeight = (transliterationSize.value * 1.45f).sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }

                // 3. Tertiary Layer: Arabic Script (sits beneath/outside container with regular weight)
                if (showArabic && dua.arabicText.isNotBlank()) {
                    Text(
                        text = dua.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.Serif,
                            color = DarkPine,
                            fontSize = arabicSize,
                            lineHeight = arabicLineHeight
                        ),
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    )
                }
            } else {
                // ============================================================
                // PRIMARY LANGUAGE = ARABIC
                // 1. Primary Container: Arabic Script (Prominent Box with distinct background & bold typography)
                // ============================================================
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CanvasMint,
                    border = BorderStroke(1.dp, BorderTealGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dua.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = DarkPine,
                            fontSize = arabicSize,
                            lineHeight = arabicLineHeight
                        ),
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                // 2. Secondary Layer: Phonetic Transliteration
                if (showTransliteration && dua.transliteration.isNotBlank()) {
                    Text(
                        text = dua.transliteration,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = DeepVibrantTeal,
                            fontSize = transliterationSize,
                            lineHeight = (transliterationSize.value * 1.45f).sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }

                // 3. Tertiary Layer: English Translation (sits beneath/outside container)
                if (dua.translation.isNotBlank()) {
                    Text(
                        text = dua.translation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF2D4B46),
                            fontSize = translationSize,
                            lineHeight = translationLineHeight
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }

            // Spiritual Benefit / Hadith Virtue Note
            if (showBenefits && dua.benefit.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldBadgeBg,
                    border = BorderStroke(0.6.dp, MetallicGold.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Virtue",
                            tint = MetallicGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = dua.benefit,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF8A5F0C),
                                fontSize = 12.sp,
                                lineHeight = 16.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // ============================================================
            // PROMINENT COMPLETION ACTION BUTTON (Positioned at Bottom of Card)
            // ============================================================
            if (isCompleted) {
                // Completed State: Prominent Success Button with Tap to Reset
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFDCFCE7),
                    border = BorderStroke(1.2.dp, Color(0xFF86EFAC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(buttonScale)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onResetCount
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Completed (${dua.repeatCount}x) • Tap to Reset",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }
            } else {
                // Incomplete State: Prominent Action Button with remaining repetition count
                val actionLabel = if (dua.repeatCount > 1) {
                    "Tap to Count  •  $remainingCount Remaining of ${dua.repeatCount}x"
                } else {
                    "Mark as Completed (1x)"
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DeepVibrantTeal,
                    border = BorderStroke(1.dp, MetallicGold.copy(alpha = 0.4f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(buttonScale)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onTapCount
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Tap to Count",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp,
                                letterSpacing = 0.3.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Settings Modal Bottom Sheet for Duas & Azkar.
 * Includes Text Size options, Arabic visibility toggle, Auto-scroll toggle,
 * Haptic feedback toggle, and Transliteration/Virtues controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarSettingsBottomSheet(
    showArabic: Boolean,
    textSize: String,
    isAutoScroll: Boolean,
    isHaptic: Boolean,
    showTransliteration: Boolean,
    showBenefits: Boolean,
    categoryName: String,
    onDismiss: () -> Unit,
    onToggleArabic: (Boolean) -> Unit,
    onSelectTextSize: (String) -> Unit,
    onToggleAutoScroll: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onToggleTransliteration: (Boolean) -> Unit,
    onToggleBenefits: (Boolean) -> Unit,
    onResetCategory: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(BorderTealGray)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SoftTealTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Azkar & Du'a Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine,
                            fontSize = 18.sp
                        )
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SlateTealMuted
                    )
                }
            }

            HorizontalDivider(color = BorderTealGray.copy(alpha = 0.6f))

            // ============================================================
            // 1. TEXT SIZE SELECTOR
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = DeepVibrantTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Text & Typography Size",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine
                        )
                    )
                }

                val sizes = listOf("Small", "Medium", "Large", "Extra Large")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sizes.forEach { sizeOption ->
                        val isSelected = textSize == sizeOption
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) DeepVibrantTeal else SoftTealTint,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) DeepVibrantTeal else BorderTealGray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectTextSize(sizeOption) }
                        ) {
                            Text(
                                text = if (sizeOption == "Extra Large") "XL" else sizeOption,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else DarkPine,
                                    fontSize = 11.5.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = BorderTealGray.copy(alpha = 0.6f))

            // ============================================================
            // 2. CONTENT & LANGUAGE PREFERENCES
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Content Display",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SlateTealMuted,
                        fontSize = 12.sp
                    )
                )

                // Show Arabic in Cards Toggle
                SettingsSwitchRow(
                    title = "Show Arabic Text in Cards",
                    subtitle = "Display original Arabic script inside Zikr cards",
                    checked = showArabic,
                    onCheckedChange = onToggleArabic
                )

                // Show Transliteration Toggle
                SettingsSwitchRow(
                    title = "Phonetic Transliteration",
                    subtitle = "Assist with accurate English pronunciation",
                    checked = showTransliteration,
                    onCheckedChange = onToggleTransliteration
                )

                // Show Spiritual Virtues Toggle
                SettingsSwitchRow(
                    title = "Spiritual Virtues & Hadith",
                    subtitle = "Display authentic references and rewards",
                    checked = showBenefits,
                    onCheckedChange = onToggleBenefits
                )
            }

            HorizontalDivider(color = BorderTealGray.copy(alpha = 0.6f))

            // ============================================================
            // 3. BEHAVIOR & INTERACTION PREFERENCES
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Behavior & Feedback",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SlateTealMuted,
                        fontSize = 12.sp
                    )
                )

                // Auto-Scroll Toggle
                SettingsSwitchRow(
                    title = "Auto-Scroll on Completion",
                    subtitle = "Smoothly advance to the next card when count is complete",
                    checked = isAutoScroll,
                    onCheckedChange = onToggleAutoScroll
                )

                // Haptic Feedback Toggle
                SettingsSwitchRow(
                    title = "Vibration & Haptic Feedback",
                    subtitle = "Gentle vibration on each tap and completion",
                    checked = isHaptic,
                    onCheckedChange = onToggleHaptic
                )
            }

            HorizontalDivider(color = BorderTealGray.copy(alpha = 0.6f))

            // ============================================================
            // 4. RESET ACTIONS
            // ============================================================
            OutlinedButton(
                onClick = onResetCategory,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkPine
                ),
                border = BorderStroke(1.dp, BorderTealGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = DarkPine,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset All Counts in $categoryName",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkPine,
                    fontSize = 14.5.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SlateTealMuted,
                    fontSize = 12.sp
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DeepVibrantTeal,
                uncheckedThumbColor = SlateTealMuted,
                uncheckedTrackColor = SoftTealTint
            )
        )
    }
}
