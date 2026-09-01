package com.example.ui.streaks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DayStreakStatus
import com.example.data.model.StreakActivityType
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreaksScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val streakData by viewModel.unifiedStreakData.collectAsStateWithLifecycle()
    var selectedDayForDetail by remember { mutableStateOf<DayStreakStatus?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier.testTag("streaks_screen"),
        topBar = {
            NoorTopBar(
                title = "Spiritual Consistency",
                eyebrow = "ISTIQAMAH",
                subtitle = "Unified Daily Spiritual Tracker",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back"
            )
        },
        containerColor = CanvasMint
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Hero Summary Card: Current Streak + Stats
            item {
                StreakHeroSummaryCard(
                    currentStreak = streakData.currentStreak,
                    longestStreak = streakData.longestStreak,
                    freezesRemaining = streakData.freezesRemaining,
                    isTodayCompleted = streakData.isTodayAnyCompleted,
                    todayActivitiesCount = streakData.todayCompletedCount
                )
            }

            // 2. 60-Day Contribution Heatmap Grid
            item {
                StreakCalendarHeatmapCard(
                    recentDays = streakData.recentDays,
                    onDayClick = { selectedDayForDetail = it }
                )
            }

            // 3. Section Title: Individual Activity Breakdown
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Activity Consistency",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine
                        )
                    )
                    Text(
                        text = "Any 1 keeps streak alive",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateTealMuted
                        )
                    )
                }
            }

            // 4. Five Activity Cards with Sub-streaks
            item {
                StreakActivityDetailCard(
                    type = StreakActivityType.SALAT,
                    isCompletedToday = streakData.todaySalatDone,
                    subStreak = streakData.salatStreak,
                    icon = Icons.Filled.AccessTime,
                    actionLabel = "Open Salat Schedule",
                    onActionClick = { viewModel.navigateTo(NoorDestination.SALAT) },
                    recentHistory = streakData.recentDays.takeLast(7).map { it.salatCompleted }
                )
            }

            item {
                StreakActivityDetailCard(
                    type = StreakActivityType.QURAN,
                    isCompletedToday = streakData.todayQuranDone,
                    subStreak = streakData.quranStreak,
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    actionLabel = "Open Qur'an Reader",
                    onActionClick = { viewModel.navigateTo(NoorDestination.QURAN_READER) },
                    recentHistory = streakData.recentDays.takeLast(7).map { it.quranCompleted }
                )
            }

            item {
                StreakActivityDetailCard(
                    type = StreakActivityType.AZKAR,
                    isCompletedToday = streakData.todayAzkarDone,
                    subStreak = streakData.azkarStreak,
                    icon = Icons.Filled.WbSunny,
                    actionLabel = "Recite Morning/Evening Azkar",
                    onActionClick = { viewModel.navigateTo(NoorDestination.AZKAR_READER) },
                    recentHistory = streakData.recentDays.takeLast(7).map { it.azkarCompleted }
                )
            }

            item {
                StreakActivityDetailCard(
                    type = StreakActivityType.DUA,
                    isCompletedToday = streakData.todayDuaDone,
                    subStreak = streakData.duaStreak,
                    icon = Icons.Filled.Favorite,
                    actionLabel = "Explore Daily Du'as",
                    onActionClick = { viewModel.navigateTo(NoorDestination.DUAS_LIBRARY) },
                    recentHistory = streakData.recentDays.takeLast(7).map { it.duaCompleted }
                )
            }

            item {
                StreakActivityDetailCard(
                    type = StreakActivityType.TASBIH,
                    isCompletedToday = streakData.todayTasbihDone,
                    subStreak = streakData.tasbihStreak,
                    icon = Icons.Filled.Spa,
                    actionLabel = "Open Smart Tasbih",
                    onActionClick = { viewModel.navigateTo(NoorDestination.TASBIH) },
                    recentHistory = streakData.recentDays.takeLast(7).map { it.tasbihCompleted }
                )
            }

            // 5. Streak Freeze Protection Card
            item {
                StreakFreezeProtectionCard(
                    freezesRemaining = streakData.freezesRemaining,
                    isYesterdayMissed = streakData.isYesterdayMissed,
                    onUseFreeze = { viewModel.useStreakFreeze() }
                )
            }

            // 6. Spiritual Quote Reflection (Non-judgmental & Warm)
            item {
                SpiritualConsistencyQuoteCard()
            }
        }
    }

    // Modal BottomSheet for Detailed Day Inspection
    if (selectedDayForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedDayForDetail = null },
            sheetState = sheetState,
            containerColor = SurfaceWhite
        ) {
            DayDetailSheetContent(
                day = selectedDayForDetail!!,
                onClose = { selectedDayForDetail = null }
            )
        }
    }
}

@Composable
private fun StreakHeroSummaryCard(
    currentStreak: Int,
    longestStreak: Int,
    freezesRemaining: Int,
    isTodayCompleted: Boolean,
    todayActivitiesCount: Int
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF0F2E24),
        border = BorderStroke(1.2.dp, Color(0xFFE5C378).copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Consistency",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFFE5C378).copy(alpha = 0.85f),
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$currentStreak",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 42.sp
                            )
                        )
                        Text(
                            text = if (currentStreak == 1) "Day Streak" else "Days Streak",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFE5C378),
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                // Streak Flame Art Box
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFE5C378).copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                        .border(1.5.dp, Color(0xFFE5C378), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Streak Flame",
                        tint = MetallicGold,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Today's Status Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isTodayCompleted) Color(0xFF163E32) else Color(0xFF224439),
                border = BorderStroke(1.dp, if (isTodayCompleted) Color(0xFFE5C378).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isTodayCompleted) Icons.Filled.CheckCircle else Icons.Filled.Info,
                            contentDescription = null,
                            tint = if (isTodayCompleted) MetallicGold else Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isTodayCompleted) {
                                "Today is complete ($todayActivitiesCount of 4 activities)"
                            } else {
                                "Complete 1 activity today to extend streak"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stats row: Best Streak & Monthly Freezes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF163E32),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Longest Streak",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFB8CFC8),
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$longestStreak Days",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF163E32),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Freezes Left (Month)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFB8CFC8),
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$freezesRemaining / 2 Available",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (freezesRemaining > 0) Color(0xFF81E6D9) else Color(0xFFE2E8F0)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakCalendarHeatmapCard(
    recentDays: List<DayStreakStatus>,
    onDayClick: (DayStreakStatus) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.2.dp, BorderTealGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Consistency Map",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine
                        )
                    )
                    Text(
                        text = "Past 60 days of spiritual practice",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 12.sp
                        )
                    )
                }

                Text(
                    text = "Tap a day",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DeepVibrantTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of 60 days (10 columns x 6 rows)
            LazyVerticalGrid(
                columns = GridCells.Fixed(10),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                items(recentDays) { day ->
                    HeatmapDaySquare(
                        day = day,
                        onClick = { onDayClick(day) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SlateTealMuted,
                        fontSize = 10.sp
                    )
                )
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(SoftTealTint))
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF81C784)))
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(DeepVibrantTeal))
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF0C4A3A)))
                Text(
                    text = "More",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SlateTealMuted,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF4FD1C5)))
                    Text(
                        text = "Freeze used",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateTealMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun HeatmapDaySquare(
    day: DayStreakStatus,
    onClick: () -> Unit
) {
    val cellColor = when {
        day.isFreezeUsed -> Color(0xFF4FD1C5)
        day.completedCount >= 4 -> Color(0xFF0C4A3A)
        day.completedCount == 3 -> DeepVibrantTeal
        day.completedCount in 1..2 -> Color(0xFF81C784)
        else -> SoftTealTint
    }

    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(cellColor)
            .border(
                width = 0.5.dp,
                color = if (day.isAnyCompleted) Color.Black.copy(alpha = 0.1f) else BorderTealGray,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (day.isFreezeUsed) {
            Icon(
                imageVector = Icons.Filled.AcUnit,
                contentDescription = "Freeze",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        } else if (day.completedCount >= 4) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Perfect Day",
                tint = MetallicGold,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun StreakActivityDetailCard(
    type: StreakActivityType,
    isCompletedToday: Boolean,
    subStreak: Int,
    icon: ImageVector,
    actionLabel: String,
    onActionClick: () -> Unit,
    recentHistory: List<Boolean>
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.2.dp, if (isCompletedToday) DeepVibrantTeal.copy(alpha = 0.5f) else BorderTealGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isCompletedToday) Color(0xFFE6F4F1) else SoftTealTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = type.title,
                            tint = if (isCompletedToday) DeepVibrantTeal else DarkPine,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = type.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = type.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                // Sub Streak Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (subStreak > 0) GoldBadgeBg else SoftTealTint
                ) {
                    Text(
                        text = if (subStreak > 0) "$subStreak days" else "0 days",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (subStreak > 0) DarkPine else SlateTealMuted,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 7-day dot timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Past 7 days:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateTealMuted,
                            fontSize = 10.5.sp
                        )
                    )
                    recentHistory.forEach { done ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (done) DeepVibrantTeal else SoftTealTint)
                                .border(0.5.dp, BorderTealGray, CircleShape)
                        )
                    }
                }

                if (isCompletedToday) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Done",
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Done Today",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepVibrantTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                } else {
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateTealMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CTA Button
            OutlinedButton(
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DeepVibrantTeal.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DeepVibrantTeal
                ),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun StreakFreezeProtectionCard(
    freezesRemaining: Int,
    isYesterdayMissed: Boolean,
    onUseFreeze: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFEFF8F6),
        border = BorderStroke(1.2.dp, Color(0xFFBFE3DC)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DeepVibrantTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AcUnit,
                        contentDescription = "Streak Freeze",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Streak Freeze Protection",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine
                        )
                    )
                    Text(
                        text = "$freezesRemaining monthly passes available",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Each month you receive 2 free streak passes to protect your consistency during busy travel, illness, or rest days. No guilt, no stress — spiritual habits are built over a lifetime.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DarkPine.copy(alpha = 0.85f),
                    lineHeight = 18.sp,
                    fontSize = 12.sp
                )
            )

            if (isYesterdayMissed && freezesRemaining > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onUseFreeze,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepVibrantTeal,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Protect Missed Day with Freeze Pass",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SpiritualConsistencyQuoteCard() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.dp, BorderTealGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FormatQuote,
                    contentDescription = "Quote",
                    tint = MetallicGold,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Spiritual Wisdom",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkPine
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "«أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ»",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkPine,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"The deeds most loved by Allah are those that are done consistently, even if they are small.\"",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic,
                    color = DarkPine,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "— Sahih al-Bukhari 6464",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SlateTealMuted,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DayDetailSheetContent(
    day: DayStreakStatus,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Activity Log: ${day.date}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkPine
                    )
                )
                Text(
                    text = if (day.isFreezeUsed) {
                        "Streak protected by Freeze Pass"
                    } else if (day.completedCount > 0) {
                        "${day.completedCount} of 5 activities completed"
                    } else {
                        "Rest day"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (day.isAnyCompleted) DeepVibrantTeal else SlateTealMuted
                    )
                )
            }

            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = DarkPine)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        DayActivityCheckRow(
            title = "Daily Obligatory Salat",
            isDone = day.salatCompleted,
            icon = Icons.Filled.AccessTime
        )
        DayActivityCheckRow(
            title = "Holy Qur'an Reading",
            isDone = day.quranCompleted,
            icon = Icons.AutoMirrored.Filled.MenuBook
        )
        DayActivityCheckRow(
            title = "Morning & Evening Azkar",
            isDone = day.azkarCompleted,
            icon = Icons.Filled.WbSunny
        )
        DayActivityCheckRow(
            title = "Daily Du'as & Supplications",
            isDone = day.duaCompleted,
            icon = Icons.Filled.Favorite
        )
        DayActivityCheckRow(
            title = "Tasbih & Remembrance",
            isDone = day.tasbihCompleted,
            icon = Icons.Filled.Spa
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DayActivityCheckRow(
    title: String,
    isDone: Boolean,
    icon: ImageVector
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDone) Color(0xFFE8F5F1) else SoftTealTint.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, if (isDone) DeepVibrantTeal else BorderTealGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDone) DeepVibrantTeal else SlateTealMuted,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isDone) DarkPine else SlateTealMuted
                    )
                )
            }

            Icon(
                imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Filled.Close,
                contentDescription = null,
                tint = if (isDone) DeepVibrantTeal else SlateTealMuted.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
