package com.example.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.localization.tr
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SurfaceWhite

@Composable
fun UnifiedStreakHomeCard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val streakData by viewModel.unifiedStreakData.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable {
                viewModel.navigateTo(NoorDestination.STREAKS)
            }
            .testTag("unified_streak_home_card"),
        shape = RoundedCornerShape(22.dp),
        color = SurfaceWhite,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Title & Streak Flame Badge
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (streakData.currentStreak > 0) GoldBadgeBg else Color(0xFFF1F5F3)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Streak Flame",
                            tint = if (streakData.currentStreak > 0) MetallicGold else SlateTealMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = tr("home_streak_title", viewModel),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (streakData.currentStreak > 0) {
                                String.format(tr("home_streak_days_count", viewModel), streakData.currentStreak)
                            } else {
                                tr("home_streak_start", viewModel)
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Days Count Badge - Clean Neutral Container with soft border
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF6F8F7),
                    border = BorderStroke(1.dp, Color(0xFFE5ECE8))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${streakData.currentStreak}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (streakData.currentStreak > 0) DeepVibrantTeal else DarkPine,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = if (streakData.currentStreak == 1) {
                                tr("home_streak_day_singular", viewModel)
                            } else {
                                tr("home_streak_day_plural", viewModel)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateTealMuted,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle & Status: "Any 1 activity keeps your streak active"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(tr("home_streak_progress_summary", viewModel), streakData.todayCompletedCount),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = SlateTealMuted,
                        fontSize = 12.5.sp
                    )
                )
                if (streakData.isTodayAnyCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = tr("home_streak_active_today", viewModel),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepVibrantTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { (streakData.todayCompletedCount / 4f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (streakData.todayCompletedCount >= 4) MetallicGold else DeepVibrantTeal,
                trackColor = Color(0xFFEAF1ED)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Compact, Premium 2x2 Grid (Salat, Quran, Azkar, Tasbih)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: Salat & Quran
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StreakProgressCompactCard(
                        title = tr("home_streak_salat", viewModel),
                        isDone = streakData.todaySalatDone,
                        icon = Icons.Filled.AccessTime,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(NoorDestination.SALAT) }
                    )
                    StreakProgressCompactCard(
                        title = tr("home_streak_quran", viewModel),
                        isDone = streakData.todayQuranDone,
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(NoorDestination.QURAN_READER) }
                    )
                }

                // Row 2: Azkar & Tasbih
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StreakProgressCompactCard(
                        title = tr("home_streak_azkar", viewModel),
                        isDone = streakData.todayAzkarDone,
                        icon = Icons.Filled.WbSunny,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(NoorDestination.AZKAR_READER) }
                    )
                    StreakProgressCompactCard(
                        title = tr("home_streak_tasbih", viewModel),
                        isDone = streakData.todayTasbihDone,
                        icon = Icons.Filled.Spa,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(NoorDestination.TASBIH) }
                    )
                }
            }

            // Streak Freeze Available Banner (if yesterday missed)
            if (streakData.isYesterdayMissed && streakData.freezesRemaining > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F8F6),
                    border = BorderStroke(1.dp, Color(0xFFD4E9E2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AcUnit,
                                contentDescription = null,
                                tint = DeepVibrantTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = String.format(tr("home_streak_freeze_pass", viewModel), streakData.freezesRemaining),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DarkPine,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Text(
                            text = tr("home_streak_protect", viewModel),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = DeepVibrantTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { viewModel.useStreakFreeze() }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tr("home_streak_view_history", viewModel),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DeepVibrantTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = DeepVibrantTeal,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun StreakProgressCompactCard(
    title: String,
    isDone: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isDone) Color(0xFFF7FAF8) else Color(0xFFFAFBFA),
        label = "compactCardBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isDone) Color(0xFFD3E7DC) else Color(0xFFE7ECE9),
        label = "compactCardBorder"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon / Checkmark Container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDone) Color(0xFFE2F1E9) else Color(0xFFF0F4F2))
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = DeepVibrantTeal,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = SlateTealMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isDone) DarkPine else Color(0xFF4A5568),
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = if (isDone) "Completed" else "Log daily",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isDone) DeepVibrantTeal else SlateTealMuted,
                        fontSize = 10.5.sp,
                        fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}
