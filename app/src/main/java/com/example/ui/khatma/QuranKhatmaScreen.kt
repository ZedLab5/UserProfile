package com.example.ui.khatma

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.KhatmaHistoryEntity
import com.example.data.local.KhatmaPlanEntity
import com.example.data.quran.KhatmaDayItem
import com.example.data.quran.KhatmaEngine
import com.example.data.quran.KhatmaFullDashboardState
import com.example.data.quran.KhatmaPaceStatus
import com.example.data.quran.KhatmaSessionInfo
import com.example.ui.MainViewModel
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.BorderTealLight
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldAccentGradient
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranKhatmaScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val dashboardState by viewModel.khatmaDashboardState.collectAsState()
    val isSetupOpen by viewModel.isKhatmaSetupSheetOpen.collectAsState()
    val isSettingsOpen by viewModel.isKhatmaSettingsSheetOpen.collectAsState()
    val isHistoryOpen by viewModel.isKhatmaHistorySheetOpen.collectAsState()
    val isCompletionOpen by viewModel.isKhatmaCompletionCelebrationOpen.collectAsState()
    val isPaceAdjustOpen by viewModel.isKhatmaPaceAdjustSheetOpen.collectAsState()
    val historyList by viewModel.khatmaHistory.collectAsState()

    var showQuickAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = "Quran Khatma",
                eyebrow = "ختمة القرآن",
                subtitle = "Completion Planner & Progress",
                onBackClick = onNavigateBack,
                backContentDescription = "Back",
                actions = {
                    NoorGlassIconButton(
                        onClick = { viewModel.isKhatmaHistorySheetOpen.value = true },
                        icon = Icons.Default.History,
                        contentDescription = "Khatma History"
                    )
                    if (dashboardState != null) {
                        NoorGlassIconButton(
                            onClick = { viewModel.isKhatmaSettingsSheetOpen.value = true },
                            icon = Icons.Default.Settings,
                            contentDescription = "Khatma Settings"
                        )
                    }
                }
            )
        },
        containerColor = CanvasMint
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val state = dashboardState
            if (state == null || state.plan.isCompleted) {
                // Empty or completed state -> Onboarding Setup
                KhatmaSetupView(
                    viewModel = viewModel,
                    isExistingKhatmaCompleted = state?.plan?.isCompleted == true,
                    onOpenHistory = { viewModel.isKhatmaHistorySheetOpen.value = true }
                )
            } else {
                // Active Khatma Dashboard
                KhatmaDashboardContent(
                    state = state,
                    viewModel = viewModel,
                    onOpenSettings = { viewModel.isKhatmaSettingsSheetOpen.value = true },
                    onOpenPaceAdjust = { viewModel.isKhatmaPaceAdjustSheetOpen.value = true },
                    onQuickAdd = { showQuickAddDialog = true }
                )
            }
        }
    }

    // Modal Sheets & Dialogs
    if (isSetupOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.isKhatmaSetupSheetOpen.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceWhite
        ) {
            KhatmaSetupSheetContent(
                viewModel = viewModel,
                onDismiss = { viewModel.isKhatmaSetupSheetOpen.value = false }
            )
        }
    }

    if (isSettingsOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.isKhatmaSettingsSheetOpen.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceWhite
        ) {
            KhatmaSettingsSheetContent(
                viewModel = viewModel,
                state = dashboardState,
                onDismiss = { viewModel.isKhatmaSettingsSheetOpen.value = false },
                onOpenPaceAdjust = {
                    viewModel.isKhatmaSettingsSheetOpen.value = false
                    viewModel.isKhatmaPaceAdjustSheetOpen.value = true
                }
            )
        }
    }

    if (isPaceAdjustOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.isKhatmaPaceAdjustSheetOpen.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceWhite
        ) {
            KhatmaPaceAdjustmentSheetContent(
                viewModel = viewModel,
                state = dashboardState,
                onDismiss = { viewModel.isKhatmaPaceAdjustSheetOpen.value = false }
            )
        }
    }

    if (isHistoryOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.isKhatmaHistorySheetOpen.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceWhite
        ) {
            KhatmaHistorySheetContent(
                historyList = historyList,
                onDismiss = { viewModel.isKhatmaHistorySheetOpen.value = false }
            )
        }
    }

    if (isCompletionOpen) {
        KhatmaCompletionCelebrationDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.isKhatmaCompletionCelebrationOpen.value = false }
        )
    }

    if (showQuickAddDialog) {
        QuickLogAyahsDialog(
            onDismiss = { showQuickAddDialog = false },
            onAdd = { count ->
                viewModel.advanceKhatmaByAyahs(count)
                showQuickAddDialog = false
            }
        )
    }
}

/**
 * Empty / Setup View to create a new Khatma
 */
@Composable
fun KhatmaSetupView(
    viewModel: MainViewModel,
    isExistingKhatmaCompleted: Boolean,
    onOpenHistory: () -> Unit
) {
    var selectedDays by remember { mutableIntStateOf(30) }
    var customDays by remember { mutableFloatStateOf(30f) }
    var isCustomSelected by remember { mutableStateOf(false) }
    var selectedSessions by remember { mutableIntStateOf(3) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderTime by remember { mutableStateOf("07:00 AM") }
    var planTitle by remember { mutableStateOf("Personal Khatma") }

    val effectiveDays = if (isCustomSelected) customDays.roundToInt() else selectedDays
    val dailyTargetAyahs = kotlin.math.ceil(KhatmaEngine.TOTAL_QURAN_AYAHS.toDouble() / effectiveDays).toInt()
    val estCompletionDate = LocalDate.now().plusDays((effectiveDays - 1).toLong())
        .format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault()))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Spiritual Welcome Banner
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                border = BorderStroke(1.dp, BorderTealGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepVibrantTeal,
                            fontFamily = FontFamily.Serif
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isExistingKhatmaCompleted) "Start a Fresh Khatma" else "Begin Your Quran Khatma",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine,
                            letterSpacing = (-0.5).sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Set a peaceful, structured reading plan. Track your progress verse by verse with daily barakah.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SlateTealMuted
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            // Duration Presets
            Text(
                text = "Choose Completion Goal",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkPine
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            val presets = listOf(7, 15, 30, 45, 60, 90)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(presets) { days ->
                    val isSelected = !isCustomSelected && selectedDays == days
                    val chipBg by animateColorAsState(
                        if (isSelected) DeepVibrantTeal else SurfaceWhite,
                        label = "chipBg"
                    )
                    val contentColor = if (isSelected) Color.White else DarkPine

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = chipBg,
                        border = if (isSelected) null else BorderStroke(1.dp, BorderTealGray),
                        modifier = Modifier
                            .clickable {
                                isCustomSelected = false
                                selectedDays = days
                            }
                            .testTag("khatma_preset_${days}_days")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$days Days",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            )
                            Text(
                                text = when (days) {
                                    7 -> "Intensive"
                                    15 -> "1/2 Month"
                                    30 -> "1 Juz / Day"
                                    45 -> "Steady Pace"
                                    60 -> "1 Hizb / Day"
                                    else -> "Gentle Journey"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else SlateTealMuted
                                )
                            )
                        }
                    }
                }

                item {
                    val isSelected = isCustomSelected
                    val chipBg by animateColorAsState(
                        if (isSelected) DeepVibrantTeal else SurfaceWhite,
                        label = "chipBg"
                    )
                    val contentColor = if (isSelected) Color.White else DarkPine

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = chipBg,
                        border = if (isSelected) null else BorderStroke(1.dp, BorderTealGray),
                        modifier = Modifier
                            .clickable { isCustomSelected = true }
                            .testTag("khatma_preset_custom_days")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Custom",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            )
                            Text(
                                text = "${customDays.roundToInt()} Days",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else SlateTealMuted
                                )
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isCustomSelected,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Custom Duration",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = DarkPine
                            )
                        )
                        Text(
                            text = "${customDays.roundToInt()} Days",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepVibrantTeal
                            )
                        )
                    }
                    Slider(
                        value = customDays,
                        onValueChange = { customDays = it },
                        valueRange = 5f..180f,
                        steps = 34,
                        colors = SliderDefaults.colors(
                            thumbColor = DeepVibrantTeal,
                            activeTrackColor = DeepVibrantTeal,
                            inactiveTrackColor = BorderTealLight
                        ),
                        modifier = Modifier.testTag("khatma_custom_slider")
                    )
                }
            }
        }

        item {
            // Daily Split Sessions Selector
            Text(
                text = "Daily Reading Sessions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkPine
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Split your daily target into bite-sized reflection sessions",
                style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val sessionOptions = listOf(
                Pair(1, "1 Session (Daily)"),
                Pair(2, "2 Sessions (Morning / Evening)"),
                Pair(3, "3 Sessions (Morning / Afternoon / Night)"),
                Pair(5, "5 Sessions (After Each Prayer)")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sessionOptions.forEach { (count, label) ->
                    val isSelected = selectedSessions == count
                    OutlinedCard(
                        onClick = { selectedSessions = count },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) SoftTealTint else SurfaceWhite
                        ),
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) DeepVibrantTeal else BorderTealGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) DeepVibrantTeal else DarkPine
                                )
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = DeepVibrantTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Daily Reminder Switch & Time
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderTealGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (reminderEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Reminder",
                            tint = if (reminderEnabled) DeepVibrantTeal else SlateTealMuted
                        )
                        Column {
                            Text(
                                text = "Daily Reminder",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkPine
                                )
                            )
                            Text(
                                text = if (reminderEnabled) "Notify at $reminderTime" else "Disabled",
                                style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                            )
                        }
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DeepVibrantTeal,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = BorderTealLight
                        )
                    )
                }
            }
        }

        item {
            // Plan Summary & Calculation Preview
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderTealGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Plan Summary",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepVibrantTeal
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Daily Target:", style = MaterialTheme.typography.bodyMedium.copy(color = SlateTealMuted))
                        Text(
                            text = "~$dailyTargetAyahs Ayahs / day",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimated Completion:", style = MaterialTheme.typography.bodyMedium.copy(color = SlateTealMuted))
                        Text(
                            text = estCompletionDate,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Holy Quran:", style = MaterialTheme.typography.bodyMedium.copy(color = SlateTealMuted))
                        Text(
                            text = "6,236 Ayahs (114 Surahs)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                        )
                    }
                }
            }
        }

        item {
            // Start Khatma CTA Button
            Button(
                onClick = {
                    viewModel.createOrResetKhatma(
                        days = effectiveDays,
                        startDate = LocalDate.now(),
                        sessionsCount = selectedSessions,
                        reminderEnabled = reminderEnabled,
                        reminderTime = reminderTime,
                        title = planTitle
                    )
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("create_khatma_button")
            ) {
                Icon(imageVector = Icons.Default.AutoStories, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Begin Khatma (بِسْمِ اللَّهِ)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        if (isExistingKhatmaCompleted) {
            item {
                OutlinedButton(
                    onClick = onOpenHistory,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, BorderTealGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = DeepVibrantTeal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Completed Khatma History", color = DeepVibrantTeal, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/**
 * Main Active Dashboard Content
 */
@Composable
fun KhatmaDashboardContent(
    state: KhatmaFullDashboardState,
    viewModel: MainViewModel,
    onOpenSettings: () -> Unit,
    onOpenPaceAdjust: () -> Unit,
    onQuickAdd: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dashboard, 1: Timeline Plan

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CanvasMint,
            contentColor = DeepVibrantTeal,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = DeepVibrantTeal
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Dashboard", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) DeepVibrantTeal else SlateTealMuted) },
                icon = { Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (selectedTab == 0) DeepVibrantTeal else SlateTealMuted) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Reading Plan (${state.totalDays} Days)", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) DeepVibrantTeal else SlateTealMuted) },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (selectedTab == 1) DeepVibrantTeal else SlateTealMuted) }
            )
        }

        when (selectedTab) {
            0 -> KhatmaDashboardOverview(
                state = state,
                viewModel = viewModel,
                onOpenPaceAdjust = onOpenPaceAdjust,
                onQuickAdd = onQuickAdd
            )
            1 -> KhatmaTimelineView(
                timeline = state.dayPlanTimeline,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun KhatmaDashboardOverview(
    state: KhatmaFullDashboardState,
    viewModel: MainViewModel,
    onOpenPaceAdjust: () -> Unit,
    onQuickAdd: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Hero Progress Card with Circular / Linear Progress & "Continue Reading"
            KhatmaHeroProgressCard(
                state = state,
                onContinueReading = { viewModel.continueKhatmaReading() },
                onQuickAdd = onQuickAdd
            )
        }

        item {
            // Today's Target & Session Breakdown
            KhatmaTodaySessionsCard(
                state = state,
                onCompleteSession = { sessionIndex, targetAyahs ->
                    viewModel.completeKhatmaSession(sessionIndex, targetAyahs)
                },
                onReadSessionPortion = { surahNum, ayahNum ->
                    viewModel.openKhatmaReadingAtAyah(surahNum, ayahNum)
                }
            )
        }

        item {
            // Pace Status & Smart Adjuster Banner
            KhatmaPaceBannerCard(
                state = state,
                onOpenPaceAdjust = onOpenPaceAdjust
            )
        }

        item {
            // Khatma Journey Stats Matrix
            KhatmaStatsMatrix(state = state)
        }

        item {
            // Quick Action Row (Mark 100% complete, adjust pace)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenPaceAdjust,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderTealGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp), tint = DeepVibrantTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Adjust Pace", fontSize = 13.sp, color = DeepVibrantTeal, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.markKhatmaCompleted() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepVibrantTeal
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Finish Khatma", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Hero Progress Card with Large Overall Progress Bar & Percentage
 */
@Composable
fun KhatmaHeroProgressCard(
    state: KhatmaFullDashboardState,
    onContinueReading: () -> Unit,
    onQuickAdd: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progressFraction,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "heroProgress"
    )

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        border = BorderStroke(1.dp, BorderTealGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Title & Pace Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = state.plan.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                    )
                    Text(
                        text = "Day ${state.currentDayNumber} of ${state.totalDays} • ${state.daysRemaining} Days remaining",
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                    )
                }

                PaceBadge(status = state.paceStatus, diff = state.paceDiffAyahs)
            }

            // Central Progress Gauge / Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${state.readAyahsCount.formatNumber()} / ${state.totalAyahs.formatNumber()}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkPine,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = "Ayahs completed (${state.progressPercentage}%)",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTealMuted)
                    )
                }

                // Circular Progress Indicator Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFFE2EBE6),
                        strokeWidth = 6.dp
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = DeepVibrantTeal,
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${state.progressPercentage}%",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepVibrantTeal
                        )
                    )
                }
            }

            // Linear Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = DeepVibrantTeal,
                    trackColor = Color(0xFFE2EBE6),
                    strokeCap = StrokeCap.Round
                )
            }

            // Current Reading Position Badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceElevated,
                border = BorderStroke(1.dp, BorderTealLight),
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
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Current Position",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTealMuted)
                            )
                            Text(
                                text = "${state.currentPosition.surahNameEnglish} (Ayah ${state.currentPosition.ayahNumber})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldBadgeBg,
                        border = BorderStroke(1.dp, MetallicGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Juz ${state.currentPosition.juzNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MetallicGold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Prominent "Continue Reading" Button
            Button(
                onClick = onContinueReading,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("continue_reading_button")
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continue Reading (${state.nextReadingPosition.displayShort})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

/**
 * Today's Target & Split Sessions Card
 */
@Composable
fun KhatmaTodaySessionsCard(
    state: KhatmaFullDashboardState,
    onCompleteSession: (Int, Int) -> Unit,
    onReadSessionPortion: (Int, Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderTealGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Target",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                    )
                    Text(
                        text = "${state.todayReadAyahs} / ${state.todayTargetAyahs} Ayahs",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (state.isTodayTargetAchieved) DeepVibrantTeal else DarkPine
                        )
                    )
                }

                if (state.isTodayTargetAchieved) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepVibrantTeal
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Today's Goal Done!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                } else {
                    Text(
                        text = "${state.todayRemainingAyahs} Ayahs remaining",
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                    )
                }
            }

            // Today's Linear Progress
            val todayProgress = (state.todayReadAyahs.toFloat() / state.todayTargetAyahs.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { todayProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = DeepVibrantTeal,
                trackColor = Color(0xFFE2EBE6),
                strokeCap = StrokeCap.Round
            )

            // Split Sessions List
            Text(
                text = "Daily Reflection Sessions (${state.dailySessions.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SlateTealMuted
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.dailySessions.forEach { session ->
                    SessionRowItem(
                        session = session,
                        onComplete = { onCompleteSession(session.index, session.targetAyahsCount) },
                        onRead = { onReadSessionPortion(session.startAyahCoord.surahNumber, session.startAyahCoord.ayahNumber) }
                    )
                }
            }
        }
    }
}

@Composable
fun SessionRowItem(
    session: KhatmaSessionInfo,
    onComplete: () -> Unit,
    onRead: () -> Unit
) {
    val isDone = session.isCompleted
    val containerBg by animateColorAsState(
        if (isDone) SoftTealTint else SurfaceElevated,
        label = "sessionBg"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerBg,
        border = BorderStroke(
            1.dp,
            if (isDone) DeepVibrantTeal.copy(alpha = 0.4f) else BorderTealLight
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onComplete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("session_complete_btn_${session.index}")
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                        contentDescription = if (isDone) "Completed" else "Mark Complete",
                        tint = if (isDone) DeepVibrantTeal else SlateTealMuted
                    )
                }

                Column {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDone) DeepVibrantTeal else DarkPine
                        )
                    )
                    Text(
                        text = "${session.startAyahCoord.displayShort} → ${session.endAyahCoord.displayShort} (${session.targetAyahsCount} Ayahs)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            OutlinedButton(
                onClick = onRead,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderTealGray),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = if (isDone) "Review" else "Read",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepVibrantTeal
                )
            }
        }
    }
}

/**
 * Pace Status & Smart Pace Adjuster Banner
 */
@Composable
fun KhatmaPaceBannerCard(
    state: KhatmaFullDashboardState,
    onOpenPaceAdjust: () -> Unit
) {
    val (bgColor, borderColor, icon, title, description) = when (state.paceStatus) {
        KhatmaPaceStatus.AHEAD -> Quintuple(
            SurfaceWhite,
            BorderTealGray,
            Icons.Default.TrendingUp,
            "Ahead of Schedule (+${state.paceDiffAyahs} Ayahs)",
            "Masha'Allah! You are reading ahead of your planned timeline. Keep this blessed momentum."
        )
        KhatmaPaceStatus.BEHIND -> Quintuple(
            SurfaceWhite,
            Color(0xFFE57373).copy(alpha = 0.5f),
            Icons.Default.Speed,
            "Behind Schedule (${state.paceDiffAyahs} Ayahs)",
            "Life happens. Choose a gentle catch-up pace or extend your timeline with peace and barakah."
        )
        KhatmaPaceStatus.ON_TRACK -> Quintuple(
            SurfaceWhite,
            BorderTealGray,
            Icons.Default.CheckCircle,
            "Right on Track",
            "You are adhering faithfully to your daily Khatma goals. May Allah accept every letter."
        )
        KhatmaPaceStatus.COMPLETED -> Quintuple(
            SurfaceWhite,
            BorderTealGray,
            Icons.Default.Star,
            "Khatma Completed! Alhamdulillah",
            "You have recited all 6,236 Ayahs of the Holy Quran."
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (state.paceStatus == KhatmaPaceStatus.BEHIND) Color(0xFFD32F2F) else DeepVibrantTeal
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkPine
                    )
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SlateTealMuted
                )
            )

            if (state.paceStatus == KhatmaPaceStatus.BEHIND) {
                Button(
                    onClick = onOpenPaceAdjust,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compassionate Pace Adjuster", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 2x2 Stats Matrix (Estimated Date, Days Left, Total Surahs, Daily Target)
 */
@Composable
fun KhatmaStatsMatrix(state: KhatmaFullDashboardState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatGridItem(
                title = "Est. Completion",
                value = state.estimatedCompletionDate,
                subtitle = "In ${state.daysRemaining} days",
                icon = Icons.Default.DateRange,
                modifier = Modifier.weight(1f)
            )
            StatGridItem(
                title = "Daily Average",
                value = "${state.todayTargetAyahs} Ayahs",
                subtitle = "${state.plan.dailySessionsCount} sessions/day",
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatGridItem(
                title = "Current Surah",
                value = state.currentPosition.surahNameEnglish,
                subtitle = state.currentPosition.surahNameArabic,
                icon = Icons.Default.AutoStories,
                modifier = Modifier.weight(1f)
            )
            StatGridItem(
                title = "Current Juz",
                value = "Juz ${state.currentPosition.juzNumber}",
                subtitle = "of 30 Juz's",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatGridItem(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderTealGray),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(color = SlateTealMuted)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DeepVibrantTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = SlateTealMuted),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Full Day Timeline / Calendar Tab
 */
@Composable
fun KhatmaTimelineView(
    timeline: List<KhatmaDayItem>,
    viewModel: MainViewModel
) {
    var filterMode by remember { mutableIntStateOf(0) } // 0: All, 1: Upcoming, 2: Completed

    val filteredList = remember(filterMode, timeline) {
        when (filterMode) {
            1 -> timeline.filter { it.isUpcoming || it.isToday }
            2 -> timeline.filter { it.isCompleted }
            else -> timeline
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            // Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = filterMode == 0,
                    onClick = { filterMode = 0 },
                    label = { Text("All Days (${timeline.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepVibrantTeal,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceWhite,
                        labelColor = DarkPine
                    ),
                    border = BorderStroke(1.dp, if (filterMode == 0) DeepVibrantTeal else BorderTealGray)
                )
                FilterChip(
                    selected = filterMode == 1,
                    onClick = { filterMode = 1 },
                    label = { Text("Upcoming") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepVibrantTeal,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceWhite,
                        labelColor = DarkPine
                    ),
                    border = BorderStroke(1.dp, if (filterMode == 1) DeepVibrantTeal else BorderTealGray)
                )
                FilterChip(
                    selected = filterMode == 2,
                    onClick = { filterMode = 2 },
                    label = { Text("Completed") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepVibrantTeal,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceWhite,
                        labelColor = DarkPine
                    ),
                    border = BorderStroke(1.dp, if (filterMode == 2) DeepVibrantTeal else BorderTealGray)
                )
            }
        }

        items(filteredList) { item ->
            TimelineDayCard(
                dayItem = item,
                onReadDayPortion = {
                    viewModel.openKhatmaReadingAtAyah(item.startCoord.surahNumber, item.startCoord.ayahNumber)
                }
            )
        }
    }
}

@Composable
fun TimelineDayCard(
    dayItem: KhatmaDayItem,
    onReadDayPortion: () -> Unit
) {
    val isToday = dayItem.isToday
    val isCompleted = dayItem.isCompleted

    val containerBg = when {
        isToday -> SoftTealTint
        else -> SurfaceWhite
    }

    val borderColor = when {
        isToday -> DeepVibrantTeal
        else -> BorderTealGray
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(if (isToday) 1.5.dp else 1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Status Indicator Icon
                Surface(
                    shape = CircleShape,
                    color = when {
                        isCompleted -> DeepVibrantTeal
                        isToday -> DeepVibrantTeal.copy(alpha = 0.15f)
                        else -> SurfaceElevated
                    },
                    border = BorderStroke(1.dp, if (isToday) DeepVibrantTeal else BorderTealLight),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "${dayItem.dayNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) DeepVibrantTeal else SlateTealMuted
                                )
                            )
                        }
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Day ${dayItem.dayNumber}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                        )
                        Text(
                            text = "• ${dayItem.dateFormatted}",
                            style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                        )
                        if (isToday) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DeepVibrantTeal
                            ) {
                                Text(
                                    text = "TODAY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${dayItem.startCoord.displayShort} → ${dayItem.endCoord.displayShort}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DarkPine,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Text(
                        text = "${dayItem.targetAyahsCount} Ayahs • Juz ${dayItem.startCoord.juzNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted
                        )
                    )
                }
            }

            OutlinedButton(
                onClick = onReadDayPortion,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderTealGray),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = if (isCompleted) "Review" else "Read",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepVibrantTeal
                )
            }
        }
    }
}

/**
 * Pace Badge Indicator
 */
@Composable
fun PaceBadge(status: KhatmaPaceStatus, diff: Int) {
    val (bg, text, border, label) = when (status) {
        KhatmaPaceStatus.AHEAD -> Quadruple(
            GoldBadgeBg,
            MetallicGold,
            MetallicGold.copy(alpha = 0.4f),
            "+$diff Ahead"
        )
        KhatmaPaceStatus.BEHIND -> Quadruple(
            Color(0xFFFFEBEE),
            Color(0xFFD32F2F),
            Color(0xFFEF9A9A),
            "$diff Behind"
        )
        KhatmaPaceStatus.ON_TRACK -> Quadruple(
            SurfaceElevated,
            DeepVibrantTeal,
            BorderTealLight,
            "On Track"
        )
        KhatmaPaceStatus.COMPLETED -> Quadruple(
            DeepVibrantTeal,
            Color.White,
            DeepVibrantTeal,
            "Completed"
        )
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = text
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Bottom Sheet for Settings & Changing Duration / Reset
 */
@Composable
fun KhatmaSettingsSheetContent(
    viewModel: MainViewModel,
    state: KhatmaFullDashboardState?,
    onDismiss: () -> Unit,
    onOpenPaceAdjust: () -> Unit
) {
    if (state == null) return

    var selectedDays by remember { mutableIntStateOf(state.totalDays) }
    var reminderEnabled by remember { mutableStateOf(state.plan.reminderEnabled) }
    var reminderTime by remember { mutableStateOf(state.plan.reminderTime) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Khatma Plan Settings",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DarkPine)
        )

        // Duration Adjustment
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Change Total Duration",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = DarkPine)
            )
            val presets = listOf(7, 15, 30, 45, 60, 90)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets) { days ->
                    FilterChip(
                        selected = selectedDays == days,
                        onClick = {
                            selectedDays = days
                            viewModel.changeKhatmaTotalDays(days)
                        },
                        label = { Text("$days Days") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepVibrantTeal,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceWhite,
                            labelColor = DarkPine
                        ),
                        border = BorderStroke(1.dp, if (selectedDays == days) DeepVibrantTeal else BorderTealGray)
                    )
                }
            }
        }

        // Reminder Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Daily Reminder",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = DarkPine)
                )
                Text(
                    text = if (reminderEnabled) "Reminder at $reminderTime" else "Disabled",
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                )
            }
            Switch(
                checked = reminderEnabled,
                onCheckedChange = {
                    reminderEnabled = it
                    viewModel.updateKhatmaReminder(it, reminderTime)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = DeepVibrantTeal,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = BorderTealLight
                )
            )
        }

        // Compassionate Pace Adjustment
        Button(
            onClick = onOpenPaceAdjust,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Speed, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Open Pace Adjuster",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Reset / Delete Plan
        OutlinedButton(
            onClick = { showDeleteConfirm = true },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
            border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Khatma Plan", color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Reset Khatma?", fontWeight = FontWeight.Bold, color = DarkPine) },
            text = { Text("Are you sure you want to reset your current Khatma plan? You can start a new one anytime.", color = SlateTealMuted) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteActiveKhatma()
                        showDeleteConfirm = false
                        onDismiss()
                    }
                ) {
                    Text("Reset Plan", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = SlateTealMuted)
                }
            },
            containerColor = SurfaceWhite
        )
    }
}

/**
 * Compassionate Pace Adjustment Sheet
 */
@Composable
fun KhatmaPaceAdjustmentSheetContent(
    viewModel: MainViewModel,
    state: KhatmaFullDashboardState?,
    onDismiss: () -> Unit
) {
    if (state == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Smart Pace Adjuster",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DarkPine)
        )
        Text(
            text = "Reciting the Quran is a spiritual relationship built on devotion, not stress. Choose how you would like to comfortably adapt your reading goals:",
            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTealMuted)
        )

        // Option 1: Spread evenly
        OutlinedCard(
            onClick = {
                viewModel.adjustKhatmaPace("SPREAD")
                onDismiss()
            },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderTealGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. Spread Evenly Across Remaining Days",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Recalculates the remaining ${KhatmaEngine.TOTAL_QURAN_AYAHS - state.readAyahsCount} Ayahs equally over the remaining ${state.daysRemaining} days.",
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                )
            }
        }

        // Option 2: Catch up gradually
        OutlinedCard(
            onClick = {
                viewModel.adjustKhatmaPace("GRADUAL")
                onDismiss()
            },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderTealGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2. Catch Up Gradually (+15 Ayahs / Day)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Adds a small, manageable booster to your daily sessions until you are back on track.",
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                )
            }
        }

        // Option 3: Extend completion deadline
        OutlinedCard(
            onClick = {
                viewModel.adjustKhatmaPace("EXTEND")
                onDismiss()
            },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, BorderTealGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "3. Extend Completion Deadline",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Maintains a calm, comfortable daily pace and smoothly pushes the target completion date outward.",
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Khatma History Sheet
 */
@Composable
fun KhatmaHistorySheetContent(
    historyList: List<KhatmaHistoryEntity>,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Completed Khatmas History",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DarkPine)
        )

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = SlateTealMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No completed Khatmas yet",
                        style = MaterialTheme.typography.bodyMedium.copy(color = DarkPine, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Your completed Quran milestones will be preserved here.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(historyList) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceWhite
                        ),
                        border = BorderStroke(1.dp, BorderTealGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                                )
                                Text(
                                    text = "Completed in ${item.daysTaken} days • ${item.completionDateFormatted}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                                )
                                Text(
                                    text = "6,236 Ayahs • Full Quran",
                                    style = MaterialTheme.typography.labelSmall.copy(color = DeepVibrantTeal, fontWeight = FontWeight.Bold)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = DeepVibrantTeal,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Khatma Setup Sheet Content (for editing / creating)
 */
@Composable
fun KhatmaSetupSheetContent(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    KhatmaSetupView(
        viewModel = viewModel,
        isExistingKhatmaCompleted = false,
        onOpenHistory = {
            onDismiss()
            viewModel.isKhatmaHistorySheetOpen.value = true
        }
    )
}

/**
 * Serene Celebration Dialog with Dua Khatm Al-Quran
 */
@Composable
fun KhatmaCompletionCelebrationDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    viewModel.isKhatmaSetupSheetOpen.value = true
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal)
            ) {
                Text("Start a New Khatma", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = SlateTealMuted)
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "الحمد لله رب العالمين 🤍",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepVibrantTeal
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Khatma Completed!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "May Allah accept your recitation, make the Quran a guiding light for your heart, and elevate your rank in Jannah.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceElevated,
                        border = BorderStroke(1.dp, BorderTealGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "دعاء ختم القرآن الكريم",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepVibrantTeal
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = KhatmaEngine.DUA_KHATM_ARABIC,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Right,
                                    color = DarkPine
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = KhatmaEngine.DUA_KHATM_TRANSLATION,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateTealMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = SurfaceWhite
    )
}

/**
 * Quick Log Ayahs Dialog
 */
@Composable
fun QuickLogAyahsDialog(
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit
) {
    val options = listOf(5, 10, 20, 50)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Ayahs Read", fontWeight = FontWeight.Bold, color = DarkPine) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select the number of Ayahs read to advance your Khatma progress:", color = SlateTealMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEach { count ->
                        OutlinedButton(
                            onClick = { onAdd(count) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderTealGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+$count", color = DeepVibrantTeal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateTealMuted)
            }
        },
        containerColor = SurfaceWhite
    )
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

private fun Int.formatNumber(): String {
    return String.format(Locale.getDefault(), "%,d", this)
}
