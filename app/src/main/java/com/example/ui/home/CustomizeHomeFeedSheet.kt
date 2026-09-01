package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.localization.tr
import com.example.data.model.HomeWidgetType
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.SlateTealMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeHomeFeedSheet(
    viewModel: MainViewModel,
    widgetsOrder: List<HomeWidgetType>,
    widgetsVisibility: Map<HomeWidgetType, Boolean>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 42.dp, height = 4.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFD4E0DA))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE2F1E9),
                        border = BorderStroke(1.dp, Color(0xFFBDDEC9)),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DashboardCustomize,
                                contentDescription = null,
                                tint = DeepVibrantTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = tr("home_customize_feed", viewModel),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = DarkPine,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = tr("home_customize_feed_sub", viewModel),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SlateTealMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE5EBE8))
            Spacer(modifier = Modifier.height(12.dp))

            // Reorder & Toggle List
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(widgetsOrder, key = { _, item -> item.id }) { index, widget ->
                    val isVisible = widgetsVisibility[widget] ?: true

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isVisible) Color(0xFFF7FAF8) else Color(0xFFFAFBFA),
                        border = BorderStroke(
                            1.dp,
                            if (isVisible) Color(0xFFD3E7DC) else Color(0xFFE7ECE9)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Reorder Arrows
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.moveHomeWidgetUp(widget) },
                                    enabled = index > 0,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = tr("home_customize_move_up", viewModel),
                                        tint = if (index > 0) DeepVibrantTeal else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.moveHomeWidgetDown(widget) },
                                    enabled = index < widgetsOrder.lastIndex,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = tr("home_customize_move_down", viewModel),
                                        tint = if (index < widgetsOrder.lastIndex) DeepVibrantTeal else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Widget Name & Description
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = getLocalizedWidgetTitle(widget, viewModel),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isVisible) DarkPine else SlateTealMuted,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = getLocalizedWidgetDescription(widget, viewModel),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isVisible) SlateTealMuted else Color(0xFF94A3B8),
                                        fontSize = 11.5.sp
                                    ),
                                    maxLines = 1
                                )
                            }

                            // Toggle Switch
                            Switch(
                                checked = isVisible,
                                onCheckedChange = { viewModel.toggleHomeWidgetVisibility(widget) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = DeepVibrantTeal,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetHomeWidgetsOrder() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DeepVibrantTeal.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepVibrantTeal)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(tr("home_customize_reset", viewModel))
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepVibrantTeal,
                        contentColor = Color.White
                    )
                ) {
                    Text(tr("home_more", viewModel), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun getLocalizedWidgetTitle(widget: HomeWidgetType, viewModel: MainViewModel): String {
    return when (widget) {
        HomeWidgetType.SALAT_TIMELINE -> tr("home_prayer_timeline", viewModel)
        HomeWidgetType.SPIRITUAL_ESSENTIALS -> tr("home_spiritual_essentials", viewModel)
        HomeWidgetType.UNIFIED_STREAKS -> tr("home_unified_streaks", viewModel)
        HomeWidgetType.DAILY_REVELATION -> tr("home_daily_revelation", viewModel)
        HomeWidgetType.KHATMA_TRACKER -> tr("home_khatma_tracker", viewModel)
        HomeWidgetType.MOOD_REFLECTION -> tr("home_mood_reflection", viewModel)
        HomeWidgetType.AUDIO_RECITERS -> tr("home_audio_reciters", viewModel)
        HomeWidgetType.PREMIUM_BANNER -> tr("home_premium_title", viewModel)
    }
}

@Composable
private fun getLocalizedWidgetDescription(widget: HomeWidgetType, viewModel: MainViewModel): String {
    return when (widget) {
        HomeWidgetType.SALAT_TIMELINE -> tr("home_prayer_timeline_sub", viewModel)
        HomeWidgetType.SPIRITUAL_ESSENTIALS -> tr("home_spiritual_essentials_sub", viewModel)
        HomeWidgetType.UNIFIED_STREAKS -> tr("home_unified_streaks_sub", viewModel)
        HomeWidgetType.DAILY_REVELATION -> tr("home_daily_revelation_sub", viewModel)
        HomeWidgetType.KHATMA_TRACKER -> tr("home_khatma_tracker_sub", viewModel)
        HomeWidgetType.MOOD_REFLECTION -> tr("home_mood_reflection_sub", viewModel)
        HomeWidgetType.AUDIO_RECITERS -> tr("home_audio_reciters_sub", viewModel)
        HomeWidgetType.PREMIUM_BANNER -> tr("home_premium_sub", viewModel)
    }
}
