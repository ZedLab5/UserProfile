package com.example.ui.quran

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Surah
import com.example.data.quran.KhatmaEngine
import com.example.data.quran.QuranData
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSurahSelectionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val currentPlayingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val khatmaState by viewModel.khatmaDashboardState.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf("All") } // "All", "Juz", "Favorites"
    var selectedJuzNumber by remember { mutableIntStateOf(1) } // 1..30

    val filteredSurahs = remember(selectedTab, selectedJuzNumber, favorites) {
        when (selectedTab) {
            "Favorites" -> {
                QuranData.surahs.filter { surah ->
                    favorites.any { fav ->
                        fav.title.contains("Surah ${surah.nameEnglish}", ignoreCase = true) ||
                                fav.title.contains(surah.nameArabic) ||
                                fav.source.contains("Surah ${surah.number}", ignoreCase = true)
                    }
                }
            }
            "Juz" -> {
                val juzIndex = (selectedJuzNumber - 1).coerceIn(0, 29)
                val startSurah = KhatmaEngine.juzStartPoints[juzIndex].first
                val endSurah = if (juzIndex < 29) KhatmaEngine.juzStartPoints[juzIndex + 1].first else 114
                QuranData.surahs.filter { it.number in startSurah..endSurah }
            }
            else -> QuranData.surahs
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Holy Qur'an",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "القرآن الكريم • 114 Surahs",
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
                    // Sleek Primary Outline Button: Start a Khatma / View Khatma Plan
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(NoorDestination.QURAN_KHATMA) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DeepVibrantTeal
                        ),
                        border = BorderStroke(1.2.dp, DeepVibrantTeal.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = DeepVibrantTeal,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (khatmaState != null && !khatmaState!!.plan.isCompleted) {
                                    "Khatma (${khatmaState!!.progressPercentage}%)"
                                } else {
                                    "Start a Khatma"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepVibrantTeal,
                                    fontSize = 12.5.sp
                                )
                            )
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Streamlined Filter Navigation Tabs: "All", "Juz", and "Favorites"
            item(key = "tab_navigation") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderTealGray, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("All", "Juz", "Favorites")
                    tabs.forEach { tabName ->
                        val isSelected = selectedTab == tabName
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) DeepVibrantTeal else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tabName }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (tabName) {
                                        "All" -> "All (114)"
                                        "Juz" -> "Juz (1-30)"
                                        "Favorites" -> "Favorites"
                                        else -> tabName
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else DarkPine,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // If Juz tab is active: Horizontal Juz selector chips (Juz 1 to 30)
            if (selectedTab == "Juz") {
                item(key = "juz_selector") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items((1..30).toList()) { juz ->
                            val isJuzSelected = selectedJuzNumber == juz
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isJuzSelected) DeepVibrantTeal else SurfaceWhite,
                                border = BorderStroke(
                                    1.dp,
                                    if (isJuzSelected) DeepVibrantTeal else BorderTealGray
                                ),
                                modifier = Modifier.clickable { selectedJuzNumber = juz }
                            ) {
                                Text(
                                    text = "Juz $juz",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isJuzSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isJuzSelected) Color.White else DarkPine,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Reading Progress Resume Card (only shown when readingProgress != null)
            readingProgress?.let { prog ->
                item(key = "reading_resume_card") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.resumeReading(prog)
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceWhite,
                        border = BorderStroke(1.2.dp, MetallicGold.copy(alpha = 0.4f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFFFDF5),
                                            Color(0xFFFFF9EC),
                                            Color(0xFFFFF3DB)
                                        )
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(GoldBadgeBg)
                                            .border(1.dp, MetallicGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = "Bookmark",
                                            tint = MetallicGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Continue Reading",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MetallicGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = prog.surahName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DarkPine,
                                                fontSize = 15.sp
                                            )
                                        )
                                        Text(
                                            text = "Ayah ${prog.ayahNumber} of ${prog.totalAyahs}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SlateTealMuted,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MetallicGold,
                                    modifier = Modifier.clickable {
                                        viewModel.resumeReading(prog)
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Resume",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
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

            // Section results header
            item(key = "results_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedTab) {
                            "Favorites" -> "${filteredSurahs.size} Favorite Surahs"
                            "Juz" -> "Juz $selectedJuzNumber • ${filteredSurahs.size} Surahs"
                            else -> "${filteredSurahs.size} Surahs available"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateTealMuted,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Standard Madani Hafs",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DeepVibrantTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Favorites Empty State if tab is Favorites and nothing is saved
            if (selectedTab == "Favorites" && filteredSurahs.isEmpty()) {
                item(key = "favorites_empty") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceWhite,
                        border = BorderStroke(1.dp, BorderTealGray)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = SlateTealMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No Favorites Saved Yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPine
                                )
                            )
                            Text(
                                text = "Tap the heart icon on any Surah to view your favorites here.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateTealMuted,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }

            // Surahs List Items
            items(
                items = filteredSurahs,
                key = { it.number }
            ) { surah ->
                val isPlayingThis = isAudioPlaying && currentPlayingSurah.number == surah.number
                val isFav = favorites.any { fav ->
                    fav.title.contains("Surah ${surah.nameEnglish}", ignoreCase = true) ||
                            fav.arabicText == surah.nameArabic ||
                            fav.source.contains("Surah ${surah.number}", ignoreCase = true)
                }

                SurahListItemCard(
                    surah = surah,
                    isAudioPlaying = isPlayingThis,
                    isFavorite = isFav,
                    onClick = {
                        viewModel.selectSurahForReading(surah)
                    },
                    onPlayAudio = {
                        viewModel.playSurahAudio(surah)
                        viewModel.navigateTo(NoorDestination.QURAN_AUDIO_STREAM)
                    },
                    onToggleFavorite = {
                        viewModel.toggleSurahFavorite(surah)
                    }
                )
            }
        }
    }
}

@Composable
fun SurahListItemCard(
    surah: Surah,
    isAudioPlaying: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onPlayAudio: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Strip out extra explanations/translations in brackets for clean title display
    val simplifiedEnglishName = surah.nameEnglish.substringBefore(" (").trim()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 0.8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Play Button on the left
            IconButton(
                onClick = onPlayAudio,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isAudioPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                    contentDescription = "Play Surah Audio",
                    tint = if (isAudioPlaying) MetallicGold else DeepVibrantTeal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Surah Number - Name & Revelation / Verses Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${surah.number} - $simplifiedEnglishName",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${surah.revelationType} • ${surah.totalVerses} verses",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateTealMuted,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3. Favorites / Heart button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) MetallicGold else SlateTealMuted.copy(alpha = 0.6f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 4. Arabic Surah Name on the right
            Box(
                modifier = Modifier.widthIn(min = 64.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = surah.nameArabic,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
    }
}
