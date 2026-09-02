package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.ui.duas.AzkarReaderScreen
import com.example.ui.duas.DuasLibraryScreen
import com.example.ui.favorites.FavoritesScreen
import com.example.ui.home.HomeScreen
import com.example.ui.khatma.QuranKhatmaScreen
import com.example.ui.profile.UserProfileScreen
import com.example.ui.qibla.QiblaScreen
import com.example.ui.quran.QuranAudioPlayerScreen
import com.example.ui.quran.QuranReaderScreen
import com.example.ui.quran.QuranRecitersListScreen
import com.example.ui.quran.QuranSurahSelectionScreen
import com.example.ui.routines.HabitTrackerScreen
import com.example.ui.salat.SalatScreen
import com.example.ui.settings.AppSettingsModal
import com.example.ui.streaks.StreaksScreen
import com.example.ui.tasbih.TasbihScreen
import com.example.ui.tools.AllToolsScreen
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldAccentGradient
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceWhite

import androidx.compose.ui.res.stringResource
import com.example.R

data class BottomNavItem(
    val destination: NoorDestination?,
    val labelRes: Int,
    val icon: ImageVector,
    val onClickOverride: (() -> Unit)? = null
)

@Composable
fun NoorApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentDest by viewModel.currentDestination.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val currentPlayingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val currentPlayingVerse by viewModel.currentPlayingVerse.collectAsStateWithLifecycle()
    val isAyahAudioMode by viewModel.isAyahAudioMode.collectAsStateWithLifecycle()
    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val toastMsg by viewModel.toastMessage.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)
    val isSettingsOpen by viewModel.isSettingsModalOpen.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMsg) {
        toastMsg?.let { snackbarHostState.showSnackbar(it) }
    }

    BackHandler(enabled = currentDest != NoorDestination.HOME) {
        viewModel.navigateBack()
    }

    val bottomNavItems = listOf(
        BottomNavItem(NoorDestination.HOME, R.string.nav_home, Icons.Default.Home),
        BottomNavItem(NoorDestination.SALAT, R.string.nav_salat, Icons.Default.AccessTime),
        BottomNavItem(NoorDestination.QURAN_SURAH_LIST, R.string.nav_quran, Icons.AutoMirrored.Filled.MenuBook),
        BottomNavItem(NoorDestination.PROFILE, R.string.nav_profile, Icons.Default.Person),
        BottomNavItem(
            destination = null,
            labelRes = R.string.nav_settings,
            icon = Icons.Default.Settings,
            onClickOverride = { viewModel.openSettingsModal() }
        )
    )

    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = CanvasMint,
        bottomBar = {
            if (currentDest != NoorDestination.QURAN_AUDIO_STREAM) {
                // Geometric Balance Tactile Navigation Bar with Settings Quick Access
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = DeepVibrantTeal.copy(alpha = 0.10f), spotColor = DeepVibrantTeal.copy(alpha = 0.16f))
                        .border(1.dp, BorderTealGray, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    color = SurfaceWhite
                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = when (item.destination) {
                            null -> isSettingsOpen
                            NoorDestination.QURAN_SURAH_LIST -> currentDest == NoorDestination.QURAN_SURAH_LIST || currentDest == NoorDestination.QURAN_READER || currentDest == NoorDestination.QURAN_KHATMA
                            else -> currentDest == item.destination && !isSettingsOpen
                        }
                        val itemLabel = stringResource(item.labelRes)

                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (item.onClickOverride != null) {
                                        item.onClickOverride.invoke()
                                    } else if (item.destination != null) {
                                        viewModel.navigateTo(item.destination)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = itemLabel,
                                tint = if (isSelected) DeepVibrantTeal else SlateTealMuted.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = itemLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) DarkPine else SlateTealMuted.copy(alpha = 0.7f),
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            AnimatedContent(
                targetState = currentDest,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "navAnimation"
            ) { dest ->
                when (dest) {
                    NoorDestination.HOME -> HomeScreen(viewModel = viewModel)
                    NoorDestination.STREAKS -> StreaksScreen(viewModel = viewModel)
                    NoorDestination.QURAN_SURAH_LIST -> QuranSurahSelectionScreen(viewModel = viewModel)
                    NoorDestination.QURAN_READER -> QuranReaderScreen(viewModel = viewModel)
                    NoorDestination.QURAN_AUDIO_STREAM -> QuranAudioPlayerScreen(viewModel = viewModel)
                    NoorDestination.QURAN_RECITERS -> QuranRecitersListScreen(viewModel = viewModel)
                    NoorDestination.QURAN_KHATMA -> QuranKhatmaScreen(
                        viewModel = viewModel,
                        onNavigateBack = { viewModel.navigateBack() }
                    )
                    NoorDestination.TASBIH -> TasbihScreen(viewModel = viewModel)
                    NoorDestination.QIBLA -> QiblaScreen(viewModel = viewModel)
                    NoorDestination.SALAT -> SalatScreen(viewModel = viewModel)
                    NoorDestination.HABIT_TRACKER -> HabitTrackerScreen(viewModel = viewModel)
                    NoorDestination.DUAS_LIBRARY -> DuasLibraryScreen(viewModel = viewModel)
                    NoorDestination.AZKAR_READER -> AzkarReaderScreen(viewModel = viewModel)
                    NoorDestination.FAVORITES -> FavoritesScreen(viewModel = viewModel)
                    NoorDestination.PROFILE -> UserProfileScreen(
                        viewModel = viewModel,
                        onNavigateBack = { viewModel.navigateBack() }
                    )
                    NoorDestination.ALL_TOOLS -> AllToolsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { viewModel.navigateBack() }
                    )
                }
            }

            // Comprehensive App Settings Modal
            if (isSettingsOpen) {
                AppSettingsModal(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeSettingsModal() }
                )
            }

            // Floating Audio Mini-Player Widget (Only shown when MP3 player is active, not during Quran reader ayah recitation)
            AnimatedVisibility(
                visible = isAudioPlaying && !isAyahAudioMode && currentDest != NoorDestination.QURAN_AUDIO_STREAM,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(NoorDestination.QURAN_AUDIO_STREAM) }
                        .shadow(14.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFF091410)),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0C1D17),
                    border = BorderStroke(1.2.dp, Color(0xFFE5C378).copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF16382C))
                                    .border(1.dp, Color(0xFFE5C378), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = "Now Playing",
                                    tint = Color(0xFFE5C378),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                val currentSurahDisplayName = if (isArabic) currentPlayingSurah.nameArabic else currentPlayingSurah.nameEnglish
                                Text(
                                    text = stringResource(R.string.player_surah_title, currentSurahDisplayName),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = stringResource(R.string.player_audio_sub, selectedReciter.name),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFA3C9BD),
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Quick Play / Pause Toggle
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE5C378).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFFE5C378).copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        viewModel.toggleAudioPlayback()
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isAudioPlaying) "Pause" else "Play",
                                        tint = Color(0xFFE5C378),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Close / Stop Audio Button
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        viewModel.pauseAudio()
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color(0xFFA3C9BD),
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
}
