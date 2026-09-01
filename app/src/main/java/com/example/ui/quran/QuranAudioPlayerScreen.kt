package com.example.ui.quran

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.quran.QuranData
import com.example.ui.MainViewModel
import com.example.ui.components.IslamicStarOrnament
import java.util.Locale

// Fresh MP3 Player Design Palette: Dark background + Soft Gold & Green Accents
private val DarkPlayerBgTop = Color(0xFF07120E)
private val DarkPlayerBgMid = Color(0xFF0C1E17)
private val DarkPlayerBgBottom = Color(0xFF050B08)

private val SoftGold = Color(0xFFE5C378)
private val SoftGoldLight = Color(0xFFF7E8C6)
private val SoftGoldDark = Color(0xFFC39E4D)
private val SoftGoldContainer = Color(0x2AE5C378)
private val SoftGoldBorder = Color(0x60E5C378)

private val GreenAccentPrimary = Color(0xFF10B981)
private val GreenAccentSoft = Color(0xFF34D399)
private val GreenCardSurface = Color(0xFF10271E)
private val GreenCardBorder = Color(0xFF1B4032)
private val MutedSageText = Color(0xFFA5C4B9)
private val DarkButtonText = Color(0xFF06150F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAudioPlayerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isAudioBuffering.collectAsStateWithLifecycle()
    val playingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val progress by viewModel.audioProgress.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.audioCurrentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.audioDurationMs.collectAsStateWithLifecycle()
    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val reciters = viewModel.reciters
    val isRepeatOne by viewModel.isAudioRepeatOne.collectAsStateWithLifecycle()
    val sleepTimerMins by viewModel.sleepTimerMinutes.collectAsStateWithLifecycle()
    val downloadedSurahs by viewModel.downloadedSurahs.collectAsStateWithLifecycle()
    val isDownloadingMap by viewModel.isSurahDownloading.collectAsStateWithLifecycle()

    var showReciterSheet by remember { mutableStateOf(false) }
    var showSurahListSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val downloadKey = "${selectedReciter.id}_${playingSurah.number}"
    val isDownloaded = downloadedSurahs.contains(downloadKey) || viewModel.isSurahDownloaded(playingSurah.number, selectedReciter.id)
    val downloadProgress = isDownloadingMap[downloadKey]

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPlaying) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Dedicated Full-Screen Canvas with deep dark green gradient & soft gold aura
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkPlayerBgTop,
                        DarkPlayerBgMid,
                        DarkPlayerBgBottom
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Ambient background Islamic geometric watermark
        IslamicStarOrnament(
            modifier = Modifier
                .size(390.dp)
                .align(Alignment.Center)
                .scale(pulseScale),
            color = SoftGold.copy(alpha = 0.04f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ------------------------------------------------------------
            // 1. TOP BAR: Minimize + Dynamic Status Badge + Settings
            // ------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minimize Button
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GreenCardSurface)
                        .border(1.dp, GreenCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize Player",
                        tint = SoftGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Center Header & Mode Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftGoldContainer,
                        border = BorderStroke(1.dp, SoftGoldBorder)
                    ) {
                        Text(
                            text = "SURAH MP3 PLAYER",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftGold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDownloaded) "Offline Audio (HQ Cache)" else "Studio MP3 Recitation",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDownloaded) GreenAccentSoft else MutedSageText,
                            fontSize = 11.sp
                        )
                    )
                }

                // Settings Button (Sleep Timer & Options)
                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GreenCardSurface)
                        .border(1.dp, GreenCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Audio Settings",
                        tint = SoftGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // ------------------------------------------------------------
            // 2. CENTERPIECE: Minimalist Medallion with Soft Gold & Green Accents
            // ------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .aspectRatio(1f)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer subtle glowing aura ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GreenAccentPrimary.copy(alpha = if (isPlaying) 0.28f else 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Medallion Disc
                Surface(
                    modifier = Modifier
                        .fillMaxSize(0.92f)
                        .shadow(
                            elevation = 18.dp,
                            shape = CircleShape,
                            ambientColor = SoftGold.copy(alpha = 0.25f),
                            spotColor = GreenAccentPrimary.copy(alpha = 0.35f)
                        ),
                    shape = CircleShape,
                    color = GreenCardSurface,
                    border = BorderStroke(2.dp, SoftGoldBorder)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        IslamicStarOrnament(
                            modifier = Modifier.size(200.dp),
                            color = SoftGold.copy(alpha = 0.08f)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(18.dp)
                        ) {
                            // Surah Number Chip
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SoftGoldContainer,
                                border = BorderStroke(1.dp, SoftGoldBorder)
                            ) {
                                Text(
                                    text = "SURAH ${playingSurah.number}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SoftGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Large Arabic Calligraphy Name
                            Text(
                                text = playingSurah.nameArabic,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,
                                    color = SoftGoldLight,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${playingSurah.revelationType} • ${playingSurah.totalVerses} Ayahs",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MutedSageText,
                                    fontSize = 11.5.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            }

            // ------------------------------------------------------------
            // 3. SURAH METADATA & REFINED RECITER CARD
            // ------------------------------------------------------------
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = playingSurah.nameEnglish,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 23.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = playingSurah.englishMeaning,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MutedSageText,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                )

                // Choose Reciter Card with Dark & Soft Gold Accents
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showReciterSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    color = GreenCardSurface,
                    border = BorderStroke(1.dp, GreenCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SoftGoldContainer)
                                    .border(1.dp, SoftGoldBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = "Reciter",
                                    tint = SoftGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Reciter: ${selectedReciter.name}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${selectedReciter.style} • ${selectedReciter.country}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MutedSageText,
                                        fontSize = 11.5.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Change Button Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SoftGoldContainer,
                            border = BorderStroke(1.dp, SoftGoldBorder)
                        ) {
                            Text(
                                text = "Change",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SoftGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // ------------------------------------------------------------
            // 4. PROGRESS SLIDER & ACCURATE TIME LABELS
            // ------------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            ) {
                Slider(
                    value = progress,
                    onValueChange = { viewModel.seekAudioTo(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = SoftGold,
                        activeTrackColor = SoftGold,
                        inactiveTrackColor = GreenCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDurationMs(currentPositionMs),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MutedSageText,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )

                    if (isBuffering) {
                        Text(
                            text = "Streaming Studio Audio...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GreenAccentSoft,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = formatDurationMs(durationMs),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MutedSageText,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // ------------------------------------------------------------
            // 5. CONTROLS: Prev + Skip Back 10s + Play/Pause + Repeat + Next
            // (Repeat positioned right next to Play; no speed; no waveform)
            // ------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Surah
                IconButton(
                    onClick = { viewModel.playPreviousSurah() },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GreenCardSurface)
                        .border(1.dp, GreenCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Surah",
                        tint = SoftGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Skip-Back 10 Seconds
                IconButton(
                    onClick = { viewModel.skipBack10Seconds() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(GreenCardSurface)
                        .border(1.dp, SoftGoldBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Skip Back 10 Seconds",
                        tint = SoftGold,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // HERO PLAY / PAUSE BUTTON (Elevated in Soft Gold with Dark Icon)
                Surface(
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(14.dp, CircleShape, spotColor = SoftGold)
                        .clip(CircleShape)
                        .clickable { viewModel.toggleAudioPlayback() },
                    shape = CircleShape,
                    color = SoftGold,
                    border = BorderStroke(2.dp, SoftGoldLight)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = DarkButtonText,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = DarkButtonText,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                // REPEAT BUTTON (Directly to the right of Play)
                IconButton(
                    onClick = { viewModel.toggleRepeatMode() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isRepeatOne) SoftGoldContainer else GreenCardSurface)
                        .border(1.dp, if (isRepeatOne) SoftGold else GreenCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isRepeatOne) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repeat Surah",
                        tint = if (isRepeatOne) SoftGold else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Next Surah
                IconButton(
                    onClick = { viewModel.playNextSurah() },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GreenCardSurface)
                        .border(1.dp, GreenCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Surah",
                        tint = SoftGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ------------------------------------------------------------
            // 6. BOTTOM TWO BUTTONS: "Choose Surah" & "Download for Offline"
            // Both clearly visible and prominently styled in white
            // ------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button 1: "Choose Surah"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showSurahListSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = null,
                            tint = DarkButtonText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Choose Surah",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = DarkButtonText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }

                // Button 2: "Download for Offline"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (isDownloaded) {
                                viewModel.deleteSurahOffline(playingSurah, selectedReciter)
                            } else {
                                viewModel.downloadSurahOffline(playingSurah, selectedReciter)
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDownloaded) Color(0xFFE6F7F0) else Color.White,
                    border = if (isDownloaded) BorderStroke(1.5.dp, GreenAccentPrimary) else null,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (downloadProgress != null) {
                            CircularProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.size(20.dp),
                                color = DarkButtonText,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = if (isDownloaded) GreenAccentPrimary else DarkButtonText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDownloaded) "Downloaded" else "Download for Offline",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isDownloaded) Color(0xFF065F46) else DarkButtonText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------
    // MODAL BOTTOM SHEET: CHOOSE RECITER
    // ------------------------------------------------------------
    if (showReciterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReciterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFF0B1914)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Choose Reciter",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Studio high-definition recitation streams",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MutedSageText
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = SoftGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(reciters) { reciter ->
                        val isCurrent = reciter.id == selectedReciter.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.selectReciter(reciter)
                                    showReciterSheet = false
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCurrent) Color(0xFF16382B) else GreenCardSurface,
                            border = if (isCurrent) BorderStroke(1.5.dp, SoftGold) else BorderStroke(1.dp, GreenCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
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
                                            .clip(CircleShape)
                                            .background(if (isCurrent) SoftGold else GreenCardBorder),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = reciter.name,
                                            tint = if (isCurrent) DarkButtonText else Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = reciter.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isCurrent) Color.White else Color(0xFFE2E8F0)
                                            )
                                        )
                                        Text(
                                            text = "${reciter.style} • ${reciter.country}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isCurrent) SoftGold else MutedSageText,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }

                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(SoftGold),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = DarkButtonText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------
    // MODAL BOTTOM SHEET: SELECT SURAH QUEUE
    // ------------------------------------------------------------
    if (showSurahListSheet) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredSurahs = remember(searchQuery) {
            if (searchQuery.isBlank()) {
                QuranData.completeSurahList
            } else {
                QuranData.completeSurahList.filter {
                    it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                            it.nameArabic.contains(searchQuery) ||
                            it.number.toString() == searchQuery.trim()
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showSurahListSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFF0B1914)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Select Surah",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name or number...", color = MutedSageText) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = SoftGold)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SoftGold,
                        unfocusedBorderColor = GreenCardBorder,
                        focusedContainerColor = GreenCardSurface,
                        unfocusedContainerColor = GreenCardSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    items(filteredSurahs) { surah ->
                        val isPlayingThis = surah.number == playingSurah.number
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.playSurahAudio(surah, openPlayer = false)
                                    showSurahListSheet = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPlayingThis) Color(0xFF16382B) else GreenCardSurface,
                            border = if (isPlayingThis) BorderStroke(1.dp, SoftGold) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isPlayingThis) SoftGold else GreenCardBorder,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${surah.number}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isPlayingThis) DarkButtonText else Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = surah.nameEnglish,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.White,
                                                fontWeight = if (isPlayingThis) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                        Text(
                                            text = "${surah.revelationType} • ${surah.totalVerses} Ayahs",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MutedSageText,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = surah.nameArabic,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        color = if (isPlayingThis) SoftGold else Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------
    // MODAL BOTTOM SHEET: PLAYER SETTINGS (SLEEP TIMER & RECITER)
    // ------------------------------------------------------------
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFF0B1914)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Player Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = SoftGold
                    )
                }

                // Sleep Timer Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sleep Timer",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = SoftGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (sleepTimerMins != null) {
                            Text(
                                text = "Active: $sleepTimerMins min",
                                style = MaterialTheme.typography.labelSmall.copy(color = GreenAccentSoft)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            null to "Off",
                            15 to "15m",
                            30 to "30m",
                            45 to "45m",
                            60 to "60m"
                        ).forEach { (mins, label) ->
                            val isSelected = sleepTimerMins == mins
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setSleepTimer(mins) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) SoftGold else GreenCardSurface,
                                border = if (isSelected) BorderStroke(1.dp, SoftGoldLight) else BorderStroke(1.dp, GreenCardBorder)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) DarkButtonText else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Switch Reciter quick row
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            showSettingsSheet = false
                            showReciterSheet = true
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = GreenCardSurface,
                    border = BorderStroke(1.dp, GreenCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Switch Reciter",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Current: ${selectedReciter.name}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MutedSageText)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = SoftGold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formats duration in milliseconds to MM:SS or HH:MM:SS format
 */
private fun formatDurationMs(ms: Int): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return if (minutes >= 60) {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        String.format(Locale.US, "%d:%02d:%02d", hours, remainingMinutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}
