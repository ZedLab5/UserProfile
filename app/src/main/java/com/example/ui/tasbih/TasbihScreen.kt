package com.example.ui.tasbih

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DhikrItem
import com.example.ui.MainViewModel
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
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
import kotlinx.coroutines.launch

val standardDhikrPresets = listOf(
    DhikrItem(
        id = "subhanallah",
        arabicText = "سُبْحَانَ اللَّهِ",
        transliteration = "SubhanAllah",
        translation = "Glory be to Allah in His infinite perfection",
        defaultTarget = 33,
        virtue = "Fills the scale with good deeds"
    ),
    DhikrItem(
        id = "alhamdulillah",
        arabicText = "الْحَمْدُ لِلَّهِ",
        transliteration = "Alhamdulillah",
        translation = "All praise and gratitude belong to Allah",
        defaultTarget = 33,
        virtue = "Fills what is between the heavens and the earth"
    ),
    DhikrItem(
        id = "allahu_akbar",
        arabicText = "اللَّهُ أَكْبَرُ",
        transliteration = "Allahu Akbar",
        translation = "Allah is Greater than everything",
        defaultTarget = 34,
        virtue = "The most beloved words to Allah"
    ),
    DhikrItem(
        id = "astaghfirullah",
        arabicText = "أَسْتَغْفِرُ اللَّهَ",
        transliteration = "Astaghfirullah",
        translation = "I seek forgiveness from Allah",
        defaultTarget = 100,
        virtue = "Opens doors of sustenance and relieves distress"
    ),
    DhikrItem(
        id = "la_ilaha_illa_allah",
        arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ",
        transliteration = "La ilaha illa Allah",
        translation = "None has the right to be worshipped except Allah",
        defaultTarget = 100,
        virtue = "The finest statement of faith and key to Paradise"
    ),
    DhikrItem(
        id = "salawat",
        arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
        transliteration = "Allahumma Salli 'ala Muhammad",
        translation = "O Allah, send peace and blessings upon Muhammad",
        defaultTarget = 100,
        virtue = "Allah sends 10 blessings for every salawat"
    ),
    DhikrItem(
        id = "la_hawla",
        arabicText = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        transliteration = "La hawla wa la quwwata illa billah",
        translation = "There is no power nor might except with Allah",
        defaultTarget = 33,
        virtue = "A treasure from the treasures of Paradise"
    ),
    DhikrItem(
        id = "subhanallahi_bihamdihi",
        arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        transliteration = "SubhanAllahi wa bihamdihi",
        translation = "Glory and praise be to Allah",
        defaultTarget = 100,
        virtue = "Sins forgiven even if like the foam of the sea"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val count by viewModel.tasbihCount.collectAsStateWithLifecycle()
    val target by viewModel.tasbihTarget.collectAsStateWithLifecycle()
    val selectedDhikrTitle by viewModel.selectedDhikr.collectAsStateWithLifecycle()
    val selectedDhikrArabic by viewModel.selectedDhikrArabic.collectAsStateWithLifecycle()
    val selectedDhikrMeaning by viewModel.selectedDhikrMeaning.collectAsStateWithLifecycle()
    val totalAllTime by viewModel.tasbihTotalAllTime.collectAsStateWithLifecycle()
    val lapsCompleted by viewModel.tasbihLapsCompleted.collectAsStateWithLifecycle()
    val currentTheme by viewModel.tasbihVisualTheme.collectAsStateWithLifecycle()

    var isTasbihBeadsVisible by remember { mutableStateOf(true) }
    var showCustomDhikrDialog by remember { mutableStateOf(false) }
    var showCustomTargetDialog by remember { mutableStateOf(false) }
    var showThemesBottomSheet by remember { mutableStateOf(false) }
    var showDhikrSelectionSheet by remember { mutableStateOf(false) }

    var customDhikrArabicInput by remember { mutableStateOf("") }
    var customDhikrTransInput by remember { mutableStateOf("") }
    var customDhikrMeaningInput by remember { mutableStateOf("") }
    var customTargetInput by remember { mutableStateOf("33") }

    val themesSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dhikrSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            NoorTopBar(
                title = stringResource(R.string.tasbih_screen_title),
                eyebrow = "NOOR",
                subtitle = stringResource(R.string.tasbih_screen_subtitle),
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = stringResource(R.string.action_back),
                actions = {
                    NoorGlassIconButton(
                        onClick = { showThemesBottomSheet = true },
                        icon = Icons.Default.FormatPaint,
                        contentDescription = stringResource(R.string.tasbih_cd_change_theme)
                    )
                    NoorGlassIconButton(
                        onClick = { viewModel.openSettingsModal() },
                        icon = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.nav_settings)
                    )
                }
            )
        },
        containerColor = CanvasMint,
        modifier = modifier
    ) { paddingValues ->
        // Static layout with zero vertical scrolling
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Card: Flat design, stacked Arabic / Clean Transliteration / Meaning, Live Counter
            ActiveDhikrInfoCard(
                arabicText = selectedDhikrArabic,
                transliteration = selectedDhikrTitle,
                meaning = selectedDhikrMeaning,
                count = count,
                target = target
            )

            // 2. Main Interactive Area: Bead String or Hidden Minimalist Tap Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isTasbihBeadsVisible) {
                    when (currentTheme) {
                        "Minimal Circle" -> MinimalCircleCounterTheme(
                            count = count,
                            target = target,
                            laps = lapsCompleted,
                            onIncrement = { viewModel.incrementTasbih() }
                        )
                        else -> TraditionalMarbleBeadsTheme(
                            count = count,
                            target = target,
                            laps = lapsCompleted,
                            onIncrement = { viewModel.incrementTasbih() }
                        )
                    }
                } else {
                    HiddenBeadsTapSurface(
                        onIncrement = { viewModel.incrementTasbih() }
                    )
                }
            }

            // 3. Flat Merged Control Bar (Header, Target Dropdown + Choose Zikr + Balanced Metrics & Reset)
            MergedTasbihControlBottomBar(
                target = target,
                laps = lapsCompleted,
                totalAllTime = totalAllTime,
                onSelectTarget = { newTarget ->
                    viewModel.setTasbihTarget(newTarget)
                },
                onCustomTargetClick = { showCustomTargetDialog = true },
                onOpenDhikrSelector = { showDhikrSelectionSheet = true },
                onResetCount = { viewModel.resetTasbih() }
            )
        }
    }

    // Modal Bottom Sheet: Dhikr Selection
    if (showDhikrSelectionSheet) {
        DhikrSelectionBottomSheet(
            selectedTitle = selectedDhikrTitle,
            sheetState = dhikrSheetState,
            onSelectDhikr = { preset ->
                viewModel.setDhikr(
                    dhikrTitle = preset.transliteration,
                    arabic = preset.arabicText,
                    meaning = preset.translation,
                    target = preset.defaultTarget,
                    virtue = preset.virtue
                )
                scope.launch { dhikrSheetState.hide() }.invokeOnCompletion {
                    showDhikrSelectionSheet = false
                }
            },
            onAddCustom = {
                scope.launch { dhikrSheetState.hide() }.invokeOnCompletion {
                    showDhikrSelectionSheet = false
                    showCustomDhikrDialog = true
                }
            },
            onDismiss = {
                scope.launch { dhikrSheetState.hide() }.invokeOnCompletion {
                    showDhikrSelectionSheet = false
                }
            }
        )
    }

    // Dialog: Add Custom Dhikr
    if (showCustomDhikrDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDhikrDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.tasbih_dialog_add_dhikr_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = customDhikrArabicInput,
                        onValueChange = { customDhikrArabicInput = it },
                        label = { Text(stringResource(R.string.tasbih_dialog_arabic_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customDhikrTransInput,
                        onValueChange = { customDhikrTransInput = it },
                        label = { Text(stringResource(R.string.tasbih_dialog_trans_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customDhikrMeaningInput,
                        onValueChange = { customDhikrMeaningInput = it },
                        label = { Text(stringResource(R.string.tasbih_dialog_meaning_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                val defaultMeaning = stringResource(R.string.tasbih_default_custom_meaning)
                val toastMsg = stringResource(R.string.tasbih_toast_custom_dhikr_set)
                Button(
                    onClick = {
                        if (customDhikrTransInput.isNotBlank()) {
                            viewModel.setDhikr(
                                dhikrTitle = customDhikrTransInput,
                                arabic = customDhikrArabicInput.ifBlank { customDhikrTransInput },
                                meaning = customDhikrMeaningInput.ifBlank { defaultMeaning },
                                target = 33
                            )
                            showCustomDhikrDialog = false
                            viewModel.showToast(toastMsg)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal)
                ) {
                    Text(stringResource(R.string.tasbih_dialog_use_dhikr_btn))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDhikrDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = SlateTealMuted)
                }
            }
        )
    }

    // Dialog: Custom Target
    if (showCustomTargetDialog) {
        AlertDialog(
            onDismissRequest = { showCustomTargetDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.tasbih_dialog_set_target_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPine)
                )
            },
            text = {
                OutlinedTextField(
                    value = customTargetInput,
                    onValueChange = { customTargetInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(R.string.tasbih_dialog_target_input_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = customTargetInput.toIntOrNull() ?: 33
                        if (num > 0) {
                            viewModel.setTasbihTarget(num)
                            showCustomTargetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal)
                ) {
                    Text(stringResource(R.string.tasbih_dialog_set_target_btn))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomTargetDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = SlateTealMuted)
                }
            }
        )
    }

    // Modal Bottom Sheet: Themes
    if (showThemesBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showThemesBottomSheet = false },
            sheetState = themesSheetState,
            containerColor = SurfaceWhite
        ) {
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
                        text = stringResource(R.string.tasbih_sheet_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine,
                            fontSize = 19.sp
                        )
                    )
                    IconButton(onClick = {
                        scope.launch { themesSheetState.hide() }.invokeOnCompletion {
                            showThemesBottomSheet = false
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(R.string.action_cancel), tint = DarkPine)
                    }
                }

                val themesList = listOf(
                    Triple(
                        "Marble",
                        stringResource(R.string.tasbih_theme_marble_title),
                        stringResource(R.string.tasbih_theme_marble_desc)
                    ),
                    Triple(
                        "Minimal Circle",
                        stringResource(R.string.tasbih_theme_minimal_title),
                        stringResource(R.string.tasbih_theme_minimal_desc)
                    )
                )

                themesList.forEach { (themeId, themeName, themeDesc) ->
                    val isSelected = currentTheme == themeId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setTasbihTheme(themeId)
                                scope.launch { themesSheetState.hide() }.invokeOnCompletion {
                                    showThemesBottomSheet = false
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) SoftTealTint else CanvasMint,
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) DeepVibrantTeal else BorderTealGray
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = themeName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkPine,
                                        fontSize = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = themeDesc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateTealMuted,
                                        fontSize = 12.5.sp
                                    )
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(DeepVibrantTeal),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = stringResource(R.string.action_selected),
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// 1. THEME 1: PHYSICAL BEAD STRING UI (TRADITIONAL)
// -------------------------------------------------------------
@Composable
fun TraditionalMarbleBeadsTheme(
    count: Int,
    target: Int,
    laps: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "beadPressAnim"
    )

    // Smooth animated count for infinite sliding bead transition
    val animatedCount by animateFloatAsState(
        targetValue = count.toFloat(),
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "animatedTasbihBeadCount"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onIncrement
            ),
        contentAlignment = Alignment.Center
    ) {
        // Canvas for Infinite Sliding Golden Marbles spanning edge-to-edge
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .layout { measurable, constraints ->
                    val horizontalExtra = 32.dp.roundToPx() // Expand to fill entire screen edge-to-edge
                    val placeable = measurable.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth + horizontalExtra,
                            minWidth = constraints.maxWidth + horizontalExtra
                        )
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(-horizontalExtra / 2, 0)
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Bezier Curve Definition shifted slightly higher to optimize vertical spacing
            val p0x = -40f
            val p0y = h * 0.70f
            val p1x = w * 0.44f
            val p1y = h * 0.12f
            val p2x = w + 40f
            val p2y = h * 0.18f

            // Parametric quadratic bezier evaluation function
            fun getPointOnCurve(u: Float): Offset {
                val inv = 1f - u
                val x = inv * inv * p0x + 2f * inv * u * p1x + u * u * p2x
                val y = inv * inv * p0y + 2f * inv * u * p1y + u * u * p2y
                return Offset(x, y)
            }

            // 1. Draw the subtle connecting string thread
            val stringPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(p0x, p0y)
                quadraticTo(p1x, p1y, p2x, p2y)
            }

            drawPath(
                path = stringPath,
                color = Color(0xFF0F3820).copy(alpha = 0.65f),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Bead geometry & infinite loop parameters
            val beadRadius = 24.dp.toPx()
            val beadSpacing = 0.088f // Spacing in curve parameter u
            val gapCenter = 0.63f
            val gapWidth = 0.24f

            val uLeftBase = gapCenter - gapWidth / 2f // Where counted beads accumulate
            val uRightBase = gapCenter + gapWidth / 2f // Where waiting beads arrive

            // Animated step progression
            val progress = animatedCount
            val s = (progress - kotlin.math.floor(progress.toDouble()).toFloat()).coerceIn(0f, 1f)

            // Helper to render a high-detail 3D Golden Marble Bead
            fun drawGoldenMarble(center: Offset) {
                val bx = center.x
                val by = center.y

                // Soft ambient drop shadow underneath
                drawCircle(
                    color = Color(0x33000000),
                    radius = beadRadius * 1.05f,
                    center = Offset(bx + 2.dp.toPx(), by + 4.dp.toPx())
                )

                // 3D Spherical Radial Golden Gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF59D), // Bright highlight
                            Color(0xFFFFD54F), // Luminous gold
                            Color(0xFFE5A823), // Deep rich gold
                            Color(0xFFC47F17), // Warm amber tone
                            Color(0xFF6D3F04)  // Dark spherical rim
                        ),
                        center = Offset(bx - beadRadius * 0.35f, by - beadRadius * 0.35f),
                        radius = beadRadius * 1.25f
                    ),
                    radius = beadRadius,
                    center = Offset(bx, by)
                )

                // Specular Highlight reflection point
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = beadRadius * 0.22f,
                    center = Offset(bx - beadRadius * 0.35f, by - beadRadius * 0.35f)
                )
            }

            // Render Left Beads (Counted queue moving down/left)
            for (k in 0..6) {
                val u = uLeftBase - (k + s) * beadSpacing
                if (u in -0.2f..1.2f) {
                    drawGoldenMarble(getPointOnCurve(u))
                }
            }

            // Render Crossing Bead (Moving seamlessly across the gap)
            val crossingU = (1f - s) * uRightBase + s * uLeftBase
            if (crossingU in -0.2f..1.2f) {
                drawGoldenMarble(getPointOnCurve(crossingU))
            }

            // Render Right Beads (Waiting queue moving towards the gap)
            for (m in 1..5) {
                val u = uRightBase + (m - s) * beadSpacing
                if (u in -0.2f..1.2f) {
                    drawGoldenMarble(getPointOnCurve(u))
                }
            }
        }

        // Perfectly Horizontally Centered, Premium Double-Stacked Counter Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .scale(buttonScale),
            contentAlignment = Alignment.Center
        ) {
            // Layer 1: Outer Pedestal Ring (White with centered soft shadow)
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.1f),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .clip(CircleShape)
                    .background(Color.White)
            )

            // Layer 2: Inner Primary Circular Button Stacked Directly On Top (Smaller top circle for side spacing)
            Surface(
                modifier = Modifier
                    .size(108.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.15f),
                        spotColor = Color.Black.copy(alpha = 0.15f)
                    ),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = stringResource(R.string.tasbih_tap_to_count),
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.tasbih_tap_button_cue),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DarkPine,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }
            }
        }
    }
}



// -------------------------------------------------------------
// 3. THEME 3: MINIMAL CIRCLE (MODERN RADIAL PROGRESS)
// -------------------------------------------------------------
@Composable
fun MinimalCircleCounterTheme(
    count: Int,
    target: Int,
    laps: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "circlePressAnim"
    )

    val progress = (count.toFloat() / target.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .scale(scale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onIncrement
                ),
            contentAlignment = Alignment.Center
        ) {
            // Modern Radial Progress Ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val pad = strokeWidth / 2f
                val diameter = size.width - strokeWidth
                val radius = diameter / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background Ring Track
                drawCircle(
                    color = Color(0xFFE2E8F0),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Foreground Gradient Progress Sweep
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            DeepVibrantTeal,
                            Color(0xFF22C55E),
                            MetallicGold,
                            DeepVibrantTeal
                        ),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Inner Card
            Surface(
                shape = CircleShape,
                color = SurfaceWhite,
                border = BorderStroke(1.dp, BorderTealGray),
                shadowElevation = 6.dp,
                modifier = Modifier.size(210.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine,
                            fontSize = 54.sp
                        )
                    )
                    Text(
                        text = stringResource(R.string.tasbih_minimal_of_target, target),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepVibrantTeal,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.tasbih_minimal_percent_done, (progress * 100).toInt()),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER SUB-COMPONENTS
// -------------------------------------------------------------

@Composable
fun ActiveDhikrInfoCard(
    arabicText: String,
    transliteration: String,
    meaning: String,
    count: Int,
    target: Int,
    modifier: Modifier = Modifier
) {
    // Remove any Arabic characters from the phonetic transliteration string
    val cleanTransliteration = remember(transliteration) {
        transliteration.replace(Regex("[\\p{InArabic}\\u0600-\\u06FF]"), "").trim()
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        border = null, // Completely flat design with zero borders and zero shadows
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 1. Arabic Calligraphy Text
            Text(
                text = arabicText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepVibrantTeal,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )

            // 2. Phonetic Transliteration (Stacked neatly between Arabic & English, no Arabic characters)
            if (cleanTransliteration.isNotBlank()) {
                Text(
                    text = cleanTransliteration,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkPine,
                        fontSize = 15.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // 3. English Meaning / Translation
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SlateTealMuted,
                    fontSize = 12.5.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 4. Live Counter Readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkPine,
                        fontSize = 38.sp,
                        letterSpacing = (-1).sp
                    )
                )
                Text(
                    text = " / $target",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SlateTealMuted,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.padding(bottom = 5.dp, start = 4.dp)
                )
            }

            // Slim Live Progress Line
            val progress = (count.toFloat() / target.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderTealGray.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(DeepVibrantTeal, Color(0xFF22C55E))
                            )
                        )
                )
            }
        }
    }
}

/**
 * Minimalist Tap Surface when Tasbih Beads are Hidden
 */
@Composable
fun HiddenBeadsTapSurface(
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onIncrement
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = SoftTealTint.copy(alpha = 0.85f),
            modifier = Modifier
                .size(190.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onIncrement
                )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = DeepVibrantTeal,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.tasbih_tap_button_cue),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = DeepVibrantTeal,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.tasbih_tap_to_count_prompt),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrSelectionBottomSheet(
    selectedTitle: String,
    sheetState: androidx.compose.material3.SheetState,
    onSelectDhikr: (DhikrItem) -> Unit,
    onAddCustom: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.tasbih_select_dhikr_sheet_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkPine,
                        fontSize = 19.sp
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_cancel),
                        tint = DarkPine
                    )
                }
            }

            // Add Custom Dhikr Option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddCustom),
                shape = RoundedCornerShape(14.dp),
                color = SoftTealTint,
                border = BorderStroke(1.dp, DeepVibrantTeal.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = DeepVibrantTeal,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.tasbih_custom_chip),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepVibrantTeal,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = stringResource(R.string.tasbih_dialog_add_dhikr_title),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Dhikr Presets List with proper vertical spacing and bottom clearance
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 28.dp)
            ) {
                items(standardDhikrPresets, key = { it.id }) { preset ->
                    val isSelected = selectedTitle.contains(preset.transliteration, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDhikr(preset) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SoftTealTint else CanvasMint,
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) DeepVibrantTeal else BorderTealGray
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = preset.transliteration,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkPine,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = preset.translation,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateTealMuted,
                                        fontSize = 11.5.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = preset.arabicText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepVibrantTeal,
                                    fontSize = 18.sp
                                )
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = DeepVibrantTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Merged Control Bar: Completely flat, header title at top-left, light-themed number dropdown,
 * "Choose Zikr" button, Laps & Total metrics aligned on the left, and Reset Count on the right.
 */
@Composable
fun MergedTasbihControlBottomBar(
    target: Int,
    laps: Int,
    totalAllTime: Int,
    onSelectTarget: (Int) -> Unit,
    onCustomTargetClick: () -> Unit,
    onOpenDhikrSelector: () -> Unit,
    onResetCount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isNumberMenuExpanded by remember { mutableStateOf(false) }
    val standardNumbers = listOf(33, 99, 100, 1000)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        border = null, // Completely flat design with zero borders and zero shadows
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Text Label at the top-left of the card
            Text(
                text = stringResource(R.string.tasbih_card_header_title),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkPine,
                    fontSize = 14.sp
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            // Middle Row: "Choose Number - XX" Dropdown Button | "Choose Zikr" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dropdown Button: "Choose Number - XX"
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isNumberMenuExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        color = SoftTealTint,
                        border = BorderStroke(1.dp, BorderTealGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.tasbih_choose_number_format, target),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPine,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = DeepVibrantTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Dropdown Menu with Light Theme background
                    DropdownMenu(
                        expanded = isNumberMenuExpanded,
                        onDismissRequest = { isNumberMenuExpanded = false },
                        modifier = Modifier.background(SurfaceWhite)
                    ) {
                        standardNumbers.forEach { num ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "$num",
                                        fontWeight = if (target == num) FontWeight.Bold else FontWeight.Normal,
                                        color = if (target == num) DeepVibrantTeal else DarkPine
                                    )
                                },
                                onClick = {
                                    onSelectTarget(num)
                                    isNumberMenuExpanded = false
                                },
                                leadingIcon = if (target == num) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DeepVibrantTeal,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null
                            )
                        }

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.tasbih_dropdown_custom_option),
                                    fontWeight = FontWeight.Bold,
                                    color = MetallicGold
                                )
                            },
                            onClick = {
                                isNumberMenuExpanded = false
                                onCustomTargetClick()
                            }
                        )
                    }
                }

                // "Choose Zikr" Button
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenDhikrSelector),
                    shape = RoundedCornerShape(12.dp),
                    color = SoftTealTint,
                    border = BorderStroke(1.dp, BorderTealGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.tasbih_choose_zikr_btn),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 13.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.LibraryBooks,
                            contentDescription = null,
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bottom Row: "Laps" and "Total" shifted to the left, "Reset Count" on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side metrics
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Session Laps Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CanvasMint,
                        border = BorderStroke(1.dp, BorderTealGray)
                    ) {
                        Text(
                            text = stringResource(R.string.tasbih_laps_badge, laps),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepVibrantTeal,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // All-Time Total Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CanvasMint,
                        border = BorderStroke(1.dp, BorderTealGray)
                    ) {
                        Text(
                            text = stringResource(R.string.tasbih_total_badge, totalAllTime),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Right side: Reset Count Action Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEE2E2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.clickable(onClick = onResetCount)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.tasbih_reset_count_action),
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = stringResource(R.string.tasbih_reset_count_action),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE11D48),
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
