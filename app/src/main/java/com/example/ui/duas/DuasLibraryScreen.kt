package com.example.ui.duas

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.data.quran.DuaData
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuasLibraryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.duasSearchQuery.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val showArabicInCards by viewModel.showArabicInAzkarCards.collectAsStateWithLifecycle()
    val azkarTextSize by viewModel.azkarTextSize.collectAsStateWithLifecycle()
    val isAutoScrollEnabled by viewModel.isAzkarAutoScrollEnabled.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isAzkarHapticEnabled.collectAsStateWithLifecycle()
    val showTransliteration by viewModel.showAzkarTransliteration.collectAsStateWithLifecycle()
    val showBenefits by viewModel.showAzkarBenefits.collectAsStateWithLifecycle()
    var showSettingsSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.recordDuaActivity()
    }

    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList() else {
            DuaData.categorizedDuas.filter { dua ->
                dua.title.contains(searchQuery, ignoreCase = true) ||
                        dua.translation.contains(searchQuery, ignoreCase = true) ||
                        dua.arabicText.contains(searchQuery) ||
                        dua.category.contains(searchQuery, ignoreCase = true) ||
                        dua.occasion.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Du'as & Azkar",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "Hisn al-Muslim • Authentic Supplications",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepVibrantTeal,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkPine
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(NoorDestination.FAVORITES) }) {
                        Surface(
                            shape = CircleShape,
                            color = SoftTealTint,
                            border = BorderStroke(1.dp, BorderTealGray)
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Favorites",
                                    tint = DeepVibrantTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    IconButton(onClick = { showSettingsSheet = true }) {
                        Surface(
                            shape = CircleShape,
                            color = SoftTealTint,
                            border = BorderStroke(1.dp, BorderTealGray)
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Azkar Settings",
                                    tint = DeepVibrantTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CanvasMint)
            )
        },
        containerColor = CanvasMint,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar
            item(key = "search_bar") {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.duasSearchQuery.value = it },
                    placeholder = {
                        Text(
                            text = "Search du'as by keyword, situation, Arabic...",
                            style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DeepVibrantTeal
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.duasSearchQuery.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = SlateTealMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite,
                        focusedBorderColor = DeepVibrantTeal,
                        unfocusedBorderColor = BorderTealGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (searchQuery.isNotBlank()) {
                // Search Results Mode
                item(key = "search_header") {
                    Text(
                        text = "Search Results (${searchResults.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine
                        )
                    )
                }

                if (searchResults.isEmpty()) {
                    item(key = "no_search_results") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceWhite,
                            border = BorderStroke(1.dp, BorderTealGray)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = SlateTealMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "No Du'as found for \"$searchQuery\"",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkPine
                                    )
                                )
                                Text(
                                    text = "Try searching for morning, protection, travel, forgiveness, or peace",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateTealMuted,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }
                } else {
                    items(searchResults, key = { it.id }) { dua ->
                        val isBookmarked = favorites.any { it.title == dua.title }
                        SearchResultDuaCard(
                            dua = dua,
                            isBookmarked = isBookmarked,
                            onOpenCategory = {
                                viewModel.openDuaCategory(dua.category)
                            },
                            onToggleBookmark = {
                                viewModel.toggleFavorite(
                                    itemType = "DUA",
                                    title = dua.title,
                                    subtitle = dua.arabicText,
                                    details = "${dua.translation}\n\n[${dua.reference}]"
                                )
                            },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    dua.title,
                                    "${dua.arabicText}\n\n${dua.transliteration}\n\n${dua.translation}\n[${dua.reference}]"
                                )
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToast("Dua copied to clipboard!")
                            }
                        )
                    }
                }
            } else {
                // Category Browsing Mode - Collections Directory
                item(key = "categories_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Authentic Azkar Collections",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "8 Categories",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepVibrantTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                // Category Cards List
                items(DuaData.categories, key = { it.id }) { category ->
                    DuaCategorySelectionCard(
                        category = category,
                        onClick = {
                            viewModel.openDuaCategory(category.id)
                        }
                    )
                }
            }
        }
    }

    if (showSettingsSheet) {
        AzkarSettingsBottomSheet(
            showArabic = showArabicInCards,
            textSize = azkarTextSize,
            isAutoScroll = isAutoScrollEnabled,
            isHaptic = isHapticEnabled,
            showTransliteration = showTransliteration,
            showBenefits = showBenefits,
            categoryName = "All Azkar",
            onDismiss = { showSettingsSheet = false },
            onToggleArabic = { viewModel.setShowArabicInAzkarCards(it) },
            onSelectTextSize = { viewModel.setAzkarTextSize(it) },
            onToggleAutoScroll = { viewModel.setAzkarAutoScroll(it) },
            onToggleHaptic = { viewModel.setAzkarHaptic(it) },
            onToggleTransliteration = { viewModel.setAzkarTransliteration(it) },
            onToggleBenefits = { viewModel.setAzkarBenefits(it) },
            onResetCategory = {
                viewModel.resetCategoryDuaCounts("Morning Azkar")
                viewModel.resetCategoryDuaCounts("Evening Azkar")
                showSettingsSheet = false
            }
        )
    }
}

@Composable
fun DuaCategorySelectionCard(
    category: DuaCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryIcon = when (category.iconType) {
        "morning" -> Icons.Default.WbSunny
        "evening" -> Icons.Default.Bedtime
        "salah" -> Icons.Default.Mosque
        "sleep" -> Icons.Default.Bedtime
        "shield" -> Icons.Default.Shield
        "health" -> Icons.Default.Healing
        "travel" -> Icons.Default.Flight
        else -> Icons.Default.MenuBook
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.dp, BorderTealGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Category Icon Box
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(SoftTealTint)
                        .border(1.dp, BorderTealGray, RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = category.titleEnglish,
                        tint = DeepVibrantTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = category.titleEnglish,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 16.sp
                            )
                        )

                        // Count Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SoftTealTint,
                            border = BorderStroke(0.6.dp, DeepVibrantTeal.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${category.itemCount} Du'as",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DeepVibrantTeal,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Forward Navigation Arrow
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SoftTealTint)
                    .border(1.dp, BorderTealGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open ${category.titleEnglish}",
                    tint = DeepVibrantTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun DailyDuaShowcaseBanner(
    dua: DuaItem,
    showArabic: Boolean = true,
    onOpenReader: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenReader),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.2.dp, MetallicGold.copy(alpha = 0.45f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFDF5),
                            Color(0xFFFFF9EE),
                            Color(0xFFFFF4E0)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MetallicGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Prophetic Supplication of the Day",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MetallicGold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFBF0D5),
                        border = BorderStroke(0.8.dp, MetallicGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Hisn al-Muslim",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8A5F0C),
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // 1. Primary Layer: English Translation (No quotes, clear focus)
                Text(
                    text = dua.translation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF4A443B),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )

                // 2. Secondary Layer: Phonetic Transliteration
                if (dua.transliteration.isNotBlank()) {
                    Text(
                        text = dua.transliteration,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = SlateTealMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }

                // 3. Tertiary Layer: Arabic Script (Regular weight)
                if (showArabic && dua.arabicText.isNotBlank()) {
                    Text(
                        text = dua.arabicText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.Serif,
                            color = DarkPine,
                            fontSize = 18.sp,
                            lineHeight = 28.sp
                        ),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Source: ${dua.reference}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateTealMuted,
                            fontSize = 11.sp
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MetallicGold,
                        modifier = Modifier.clickable(onClick = onOpenReader)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Recite Azkar",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultDuaCard(
    dua: DuaItem,
    isBookmarked: Boolean,
    onOpenCategory: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.dp, BorderTealGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SoftTealTint,
                    border = BorderStroke(0.6.dp, DeepVibrantTeal.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable(onClick = onOpenCategory)
                ) {
                    Text(
                        text = dua.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepVibrantTeal,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = SlateTealMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MetallicGold else SlateTealMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = dua.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkPine,
                    fontSize = 15.sp
                )
            )

            // 1. Primary English Translation
            Text(
                text = dua.translation,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF233B37),
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            )

            // 2. Secondary Phonetic Transliteration
            if (dua.transliteration.isNotBlank()) {
                Text(
                    text = dua.transliteration,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = DeepVibrantTeal,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )
                )
            }

            // 3. Tertiary Traditional Arabic Script (Regular weight)
            if (dua.arabicText.isNotBlank()) {
                Text(
                    text = dua.arabicText,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Serif,
                        color = DarkPine,
                        fontSize = 19.sp,
                        lineHeight = 30.sp
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "${dua.occasion} • ${dua.reference}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SlateTealMuted,
                    fontSize = 11.sp
                )
            )
        }
    }
}
