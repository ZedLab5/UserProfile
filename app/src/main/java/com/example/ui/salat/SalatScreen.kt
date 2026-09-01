package com.example.ui.salat

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CalculationAuthority
import com.example.data.model.PrayerTime
import com.example.data.model.PrayerZone
import com.example.ui.MainViewModel
import com.example.ui.components.BentoCard
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.BorderTealLight
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalatScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var isSettingsModalOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryTealGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = stringResource(R.string.salat_screen_title),
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.salat_screen_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPine
                                )
                            )
                            Text(
                                text = stringResource(R.string.salat_screen_subtitle),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateTealMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = DarkPine
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSettingsModalOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = stringResource(R.string.salat_settings_sheet_title),
                            tint = DeepVibrantTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CanvasMint)
            )
        },
        containerColor = CanvasMint,
        modifier = modifier
    ) { paddingValues ->
        SalatTimesContent(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )

        // Standard Modal Bottom Sheet for Salat Settings (Opens bottom to top)
        if (isSettingsModalOpen) {
            SalatSettingsModalSheet(
                onDismiss = { isSettingsModalOpen = false },
                viewModel = viewModel
            )
        }
    }
}

// =========================================================================
// SALAT SETTINGS MODAL BOTTOM SHEET (BOTTOM-TO-TOP TRANSITION & DROPDOWN SELECTORS)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalatSettingsModalSheet(
    onDismiss: () -> Unit,
    viewModel: MainViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val autoSilent by viewModel.autoSilentDuringSalat.collectAsStateWithLifecycle()
    val silentDuration by viewModel.silentDurationMinutes.collectAsStateWithLifecycle()
    val isHanafi by viewModel.isHanafiAsr.collectAsStateWithLifecycle()
    val selectedZone by viewModel.selectedPrayerZone.collectAsStateWithLifecycle()
    val selectedAuthority by viewModel.selectedAuthority.collectAsStateWithLifecycle()
    val manualOffsets by viewModel.prayerManualMinuteOffsets.collectAsStateWithLifecycle()
    val athanSound by viewModel.athanSoundName.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAthanAudioPreviewPlaying.collectAsStateWithLifecycle()
    val hijriOffset by viewModel.hijriAdjustmentDays.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderTealLight)
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.salat_settings_sheet_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine
                            )
                        )
                        Text(
                            text = stringResource(R.string.salat_settings_sheet_sub),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = SlateTealMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = BorderTealLight)

            // 1. MOSQUE MODE (AUTO-SILENT DND)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevated,
                border = BorderStroke(1.dp, BorderTealLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.salat_mosque_mode_title),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPine
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.salat_mosque_mode_sub, silentDuration),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateTealMuted,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Switch(
                            checked = autoSilent,
                            onCheckedChange = { viewModel.toggleAutoSilent() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DeepVibrantTeal,
                                uncheckedTrackColor = BorderTealGray,
                                uncheckedThumbColor = SlateTealMuted
                            )
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.salat_silent_duration, silentDuration),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (autoSilent) DeepVibrantTeal else SlateTealMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        Slider(
                            value = silentDuration.toFloat(),
                            onValueChange = { viewModel.setSilentDuration(it.toInt()) },
                            valueRange = 10f..45f,
                            steps = 6,
                            colors = SliderDefaults.colors(
                                thumbColor = if (autoSilent) DeepVibrantTeal else SlateTealMuted,
                                activeTrackColor = if (autoSilent) DeepVibrantTeal else SlateTealMuted,
                                inactiveTrackColor = BorderTealLight
                            )
                        )
                    }
                }
            }

            // 2. OPTION INPUT DROPDOWN: PRAYER TIME ZONE
            SalatZoneDropdownSelector(
                zones = viewModel.prayerZones,
                selectedZone = selectedZone,
                isArabic = isArabic,
                onSelectZone = { viewModel.selectPrayerZone(it) }
            )

            // 3. OPTION INPUT DROPDOWN: CALCULATION AUTHORITY
            SalatAuthorityDropdownSelector(
                authorities = viewModel.calculationAuthorities,
                selectedAuthority = selectedAuthority,
                onSelectAuthority = { viewModel.selectCalculationAuthority(it) }
            )

            // 4. MANUAL PRAYER TIME MINUTE ADJUSTMENTS
            SalatManualOffsetsCard(
                manualOffsets = manualOffsets,
                isArabic = isArabic,
                onUpdateOffset = { prayerName, delta ->
                    viewModel.updatePrayerManualOffset(prayerName, delta)
                },
                onResetOffsets = {
                    viewModel.resetPrayerManualOffsets()
                }
            )

            // 5. OPTION INPUT DROPDOWN: ADHAN VOICE RECITATION
            SalatAdhanVoiceDropdownSelector(
                selectedVoice = athanSound,
                isPlaying = isAudioPlaying,
                onSelectVoice = { viewModel.athanSoundName.value = it },
                onTogglePreview = { viewModel.toggleAthanAudioPreview() }
            )

            // 6. HANAFI ASR JURISTIC CALCULATION
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevated,
                border = BorderStroke(1.dp, BorderTealLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.salat_hanafi_asr_title),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isHanafi) stringResource(R.string.salat_hanafi_asr_desc)
                            else stringResource(R.string.salat_standard_asr_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Switch(
                        checked = isHanafi,
                        onCheckedChange = { viewModel.toggleHanafiAsr(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DeepVibrantTeal,
                            uncheckedTrackColor = BorderTealGray,
                            uncheckedThumbColor = SlateTealMuted
                        )
                    )
                }
            }

            // 6. HIJRI CALENDAR ADJUSTMENT
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevated,
                border = BorderStroke(1.dp, BorderTealLight)
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
                            text = stringResource(R.string.salat_hijri_calib_title),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine
                            )
                        )
                        Text(
                            text = stringResource(R.string.salat_hijri_calib_sub, if (hijriOffset > 0) "+$hijriOffset" else "$hijriOffset"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, BorderTealLight),
                            modifier = Modifier.clickable { viewModel.updateHijriAdjustment(-1) }
                        ) {
                            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Minus Day",
                                    tint = SlateTealMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = "$hijriOffset",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, BorderTealLight),
                            modifier = Modifier.clickable { viewModel.updateHijriAdjustment(1) }
                        ) {
                            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Plus Day",
                                    tint = DeepVibrantTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Done Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.action_done),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// =========================================================================
// DROPDOWN SELECTORS FOR SETTINGS
// =========================================================================

@Composable
private fun SalatZoneDropdownSelector(
    zones: List<PrayerZone>,
    selectedZone: PrayerZone,
    isArabic: Boolean,
    onSelectZone: (PrayerZone) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.salat_zones_title),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DarkPine
            )
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = true },
                shape = RoundedCornerShape(14.dp),
                color = SurfaceElevated,
                border = BorderStroke(1.dp, if (isExpanded) DeepVibrantTeal else BorderTealLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (isArabic) selectedZone.arabicName else "${selectedZone.name}, ${selectedZone.country}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPine
                                )
                            )
                            Text(
                                text = selectedZone.zoneLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SlateTealMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Zone",
                        tint = DeepVibrantTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 360.dp)
                    .background(SurfaceWhite)
                    .border(BorderStroke(1.dp, BorderTealLight), RoundedCornerShape(14.dp))
            ) {
                zones.forEach { zone ->
                    val isSelected = zone.id == selectedZone.id
                    val displayName = if (isArabic) zone.arabicName else "${zone.name}, ${zone.country}"

                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = displayName,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) DeepVibrantTeal else DarkPine
                                            )
                                        )
                                        Text(
                                            text = zone.zoneLabel,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SlateTealMuted,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DeepVibrantTeal,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            onSelectZone(zone)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SalatAuthorityDropdownSelector(
    authorities: List<CalculationAuthority>,
    selectedAuthority: CalculationAuthority,
    onSelectAuthority: (CalculationAuthority) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.salat_calc_authority_title),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DarkPine
            )
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = true },
                shape = RoundedCornerShape(14.dp),
                color = SurfaceElevated,
                border = BorderStroke(1.dp, if (isExpanded) DeepVibrantTeal else BorderTealLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = selectedAuthority.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPine
                                )
                            )
                            val ishaLabel = if (selectedAuthority.ishaIntervalMinutes != null) "${selectedAuthority.ishaIntervalMinutes}m" else "${selectedAuthority.ishaAngle}°"
                            Text(
                                text = "Fajr: ${selectedAuthority.fajrAngle}° • Isha: $ishaLabel",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SlateTealMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Authority",
                        tint = DeepVibrantTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 360.dp)
                    .background(SurfaceWhite)
                    .border(BorderStroke(1.dp, BorderTealLight), RoundedCornerShape(14.dp))
            ) {
                authorities.forEach { auth ->
                    val isSelected = auth.id == selectedAuthority.id
                    val authIshaLabel = if (auth.ishaIntervalMinutes != null) "${auth.ishaIntervalMinutes}m" else "${auth.ishaAngle}°"

                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = auth.name,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) DeepVibrantTeal else DarkPine
                                            )
                                        )
                                        Text(
                                            text = "Fajr: ${auth.fajrAngle}° • Isha: $authIshaLabel",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SlateTealMuted,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DeepVibrantTeal,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            onSelectAuthority(auth)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SalatManualOffsetsCard(
    manualOffsets: Map<String, Int>,
    isArabic: Boolean,
    onUpdateOffset: (String, Int) -> Unit,
    onResetOffsets: () -> Unit
) {
    val prayers = listOf(
        "Fajr" to (if (isArabic) "الفجر" else "Fajr"),
        "Sunrise" to (if (isArabic) "الشروق" else "Sunrise"),
        "Dhuhr" to (if (isArabic) "الظهر" else "Dhuhr"),
        "Asr" to (if (isArabic) "العصر" else "Asr"),
        "Maghrib" to (if (isArabic) "المغرب" else "Maghrib"),
        "Isha" to (if (isArabic) "العشاء" else "Isha")
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceElevated,
        border = BorderStroke(1.dp, BorderTealLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.salat_manual_offsets_title),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine
                        )
                    )
                    Text(
                        text = stringResource(R.string.salat_manual_offsets_sub),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = stringResource(R.string.salat_reset_offsets),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DeepVibrantTeal,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onResetOffsets() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            HorizontalDivider(color = BorderTealLight.copy(alpha = 0.5f), thickness = 0.8.dp)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                prayers.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { (pKey, pLabel) ->
                            val currentOffset = manualOffsets[pKey] ?: 0
                            val offsetText = if (currentOffset > 0) "+$currentOffset min" else if (currentOffset < 0) "$currentOffset min" else "0 min"

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceWhite,
                                border = BorderStroke(1.dp, BorderTealLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = pLabel,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DarkPine,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Text(
                                            text = offsetText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (currentOffset != 0) DeepVibrantTeal else SlateTealMuted,
                                                fontWeight = if (currentOffset != 0) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = CanvasMint,
                                            border = BorderStroke(1.dp, BorderTealLight),
                                            modifier = Modifier.clickable { onUpdateOffset(pKey, -1) }
                                        ) {
                                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Remove,
                                                    contentDescription = "Minus 1 min",
                                                    tint = SlateTealMuted,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = CircleShape,
                                            color = SoftTealTint,
                                            border = BorderStroke(1.dp, BorderTealLight),
                                            modifier = Modifier.clickable { onUpdateOffset(pKey, 1) }
                                        ) {
                                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Plus 1 min",
                                                    tint = DeepVibrantTeal,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalatAdhanVoiceDropdownSelector(
    selectedVoice: String,
    isPlaying: Boolean,
    onSelectVoice: (String) -> Unit,
    onTogglePreview: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val adhanVoices = listOf(
        "Makkah Al-Mukarramah Adhan",
        "Madinah Al-Munawwarah Adhan",
        "Al-Aqsa Sanctuary Adhan",
        "Istanbul Blue Mosque Adhan",
        "Cairo Al-Azhar Adhan"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.salat_adhan_voice_title),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DarkPine
            )
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = true },
                shape = RoundedCornerShape(14.dp),
                color = SurfaceElevated,
                border = BorderStroke(1.dp, if (isExpanded) DeepVibrantTeal else BorderTealLight)
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = selectedVoice,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPine,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(R.string.salat_adhan_switch_sub),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SlateTealMuted,
                                    fontSize = 10.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onTogglePreview,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isPlaying) "Stop" else "Preview",
                                tint = DeepVibrantTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Voice",
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 360.dp)
                    .background(SurfaceWhite)
                    .border(BorderStroke(1.dp, BorderTealLight), RoundedCornerShape(14.dp))
            ) {
                adhanVoices.forEach { voice ->
                    val isSelected = voice == selectedVoice

                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = voice,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) DeepVibrantTeal else DarkPine,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DeepVibrantTeal,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            onSelectVoice(voice)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================
// TIMES CONTENT (UNIFIED CARD STYLING, CLEAN DIVIDERS & ALIGNED COLUMNS)
// =========================================================================

@Composable
private fun SalatTimesContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val completedPrayers by viewModel.completedPrayers.collectAsStateWithLifecycle()
    val selectedZone by viewModel.selectedPrayerZone.collectAsStateWithLifecycle()
    val selectedAuthority by viewModel.selectedAuthority.collectAsStateWithLifecycle()
    val isHanafi by viewModel.isHanafiAsr.collectAsStateWithLifecycle()
    val timersMap by viewModel.prayerNotificationTimers.collectAsStateWithLifecycle()
    val enabledMap by viewModel.prayerNotificationEnabled.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Zone Card (Summary)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SurfaceWhite,
            border = BorderStroke(1.dp, BorderTealLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SoftTealTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Zone",
                            tint = DeepVibrantTeal,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) selectedZone.arabicName else "${selectedZone.name}, ${selectedZone.country}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine,
                                fontSize = 16.5.sp
                            )
                        )
                        Text(
                            text = "${selectedAuthority.name} • ${if (isHanafi) stringResource(R.string.salat_hanafi_asr) else stringResource(R.string.salat_standard_asr)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Daily Progress Bar & Date
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CanvasMint,
                    border = BorderStroke(1.dp, BorderTealGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = stringResource(R.string.salat_date_cd),
                                tint = SlateTealMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = stringResource(R.string.salat_sample_hijri_date),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DarkPine,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.5.sp
                                )
                            )
                        }

                        Text(
                            text = stringResource(R.string.salat_completed_count, completedPrayers.size),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepVibrantTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Daily Prayer Timetable - Expanded High-Craft Single Card Style
        BentoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = SurfaceWhite,
            borderColor = BorderTealGray,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Column(
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.salat_daily_schedule_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkPine,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.salat_daily_schedule_sub),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateTealMuted,
                            fontSize = 12.5.sp
                        )
                    )
                }

                HorizontalDivider(color = BorderTealLight.copy(alpha = 0.6f), thickness = 1.dp)

                prayerTimes.forEachIndexed { index, prayer ->
                    val isChecked = completedPrayers.contains(prayer.name)
                    val isActionable = prayer.isPast || prayer.isCurrent
                    val offsetMin = timersMap[prayer.name] ?: 0
                    val isNotificationEnabled = enabledMap[prayer.name] ?: true
                    val isSunrise = prayer.name == "Sunrise"

                    val prayerDisplayName = when (prayer.name) {
                        "Fajr" -> stringResource(R.string.prayer_fajr)
                        "Sunrise" -> stringResource(R.string.prayer_sunrise)
                        "Dhuhr" -> stringResource(R.string.prayer_dhuhr)
                        "Asr" -> stringResource(R.string.prayer_asr)
                        "Maghrib" -> stringResource(R.string.prayer_maghrib)
                        "Isha" -> stringResource(R.string.prayer_isha)
                        else -> prayer.name
                    }

                    val renderedPrayerName = if (isArabic) prayer.arabicName else prayerDisplayName

                    PrayerRowItem(
                        prayer = prayer,
                        renderedPrayerName = renderedPrayerName,
                        isChecked = isChecked,
                        isActionable = isActionable,
                        isSunrise = isSunrise,
                        offsetMin = offsetMin,
                        isNotificationEnabled = isNotificationEnabled,
                        isArabic = isArabic,
                        onTogglePrayer = {
                            if (isActionable && !isSunrise) {
                                viewModel.togglePrayerCompleted(prayer)
                            }
                        },
                        onSetOffset = { offset ->
                            viewModel.setPrayerNotificationTimer(prayer.name, offset)
                        },
                        onToggleNotification = {
                            viewModel.togglePrayerNotification(prayer.name)
                        }
                    )

                    if (index < prayerTimes.size - 1) {
                        HorizontalDivider(
                            color = BorderTealLight.copy(alpha = 0.4f),
                            thickness = 0.8.dp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Forbidden Prayer Times Notice Card
        BentoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFFEF2F2),
            borderColor = Color(0xFFFECACA)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = if (isArabic) "أوقات الكراهة" else "Forbidden Times",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isArabic) "أوقات الكراهة وصلاة المحظورة" else "Forbidden Prayer Times",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                    )
                }
                Text(
                    text = if (isArabic) "الأوقات التي يكره أو يحرم فيها أداء صلاة النافلة المطلقة:" else "Times when performing voluntary prayers is forbidden or disliked:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB91C1C), fontSize = 11.5.sp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                val forbiddenList = if (isArabic) listOf(
                    "• وقت الشروق: من طلوع الشمس حتى ترتفع قيد رمح (نحو 15 دقيقة).",
                    "• وقت الاستواء: عندما تكون الشمس في كبد السماء قبيل الظهر بقليل.",
                    "• وقت الغروب: من بدء اصفرار الشمس حتى تمام غروبها."
                ) else listOf(
                    "• Sunrise: From dawn/sunrise until the sun is fully risen (~15 mins).",
                    "• Zenith: Exact midday when the sun is at its highest point.",
                    "• Sunset: From when the sun begins to turn yellow until fully set."
                )
                forbiddenList.forEach { item ->
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7F1D1D),
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }
    }
}

// =========================================================================
// PRAYER ROW ITEM (INLINE COMPACT GREEN NOTIFICATION PILL & SPACIOUS LAYOUT)
// =========================================================================

@Composable
private fun PrayerRowItem(
    prayer: PrayerTime,
    renderedPrayerName: String,
    isChecked: Boolean,
    isActionable: Boolean,
    isSunrise: Boolean,
    offsetMin: Int,
    isNotificationEnabled: Boolean,
    isArabic: Boolean,
    onTogglePrayer: () -> Unit,
    onSetOffset: (Int) -> Unit,
    onToggleNotification: () -> Unit
) {
    var isDropdownOpen by remember { mutableStateOf(false) }

    val shortOffsetLabel = if (!isNotificationEnabled) {
        if (isArabic) "صامت" else "Off"
    } else {
        when (offsetMin) {
            -30 -> if (isArabic) "30 د" else "30min"
            -20 -> if (isArabic) "20 د" else "20min"
            -15 -> if (isArabic) "15 د" else "15min"
            -10 -> if (isArabic) "10 د" else "10min"
            -5 -> if (isArabic) "5 د" else "5min"
            0 -> if (isArabic) "في الوقت" else "Exact"
            5 -> if (isArabic) "+5 د" else "+5min"
            10 -> if (isArabic) "+10 د" else "+10min"
            else -> if (offsetMin < 0) "${-offsetMin}min" else "+${offsetMin}min"
        }
    }

    // Row Background: Soft highlight for Active Prayer, clean/transparent for others
    val rowBackgroundModifier = when {
        prayer.isCurrent && !isSunrise -> Modifier
            .background(SoftTealTint.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, DeepVibrantTeal.copy(alpha = 0.35f)), RoundedCornerShape(12.dp))
        isSunrise -> Modifier
            .background(Color(0xFFFFFBEB).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        else -> Modifier
    }

    val iconContainerColor = when {
        isChecked && !isSunrise -> Color(0xFF0D6E58)
        prayer.isCurrent && !isSunrise -> Color.White
        isSunrise -> Color(0xFFD97706)
        else -> CanvasMint
    }

    val prayerTitleColor = when {
        isChecked && !isSunrise -> Color(0xFF0D6E58)
        prayer.isCurrent && !isSunrise -> DeepVibrantTeal
        isSunrise -> Color(0xFF92400E)
        else -> DarkPine
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(rowBackgroundModifier)
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Checkbox/Icon + Name + Status Badge (Expanded Horizontal Spacing)
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = isActionable && !isSunrise) {
                    onTogglePrayer()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor)
                    .then(
                        if (prayer.isCurrent && !isChecked)
                            Modifier.border(BorderStroke(1.dp, BorderTealGray), CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isChecked -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.salat_status_completed),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    isSunrise -> {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = stringResource(R.string.prayer_sunrise),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    prayer.isCurrent -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.salat_status_active),
                            tint = SlateTealMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    !isActionable -> {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SlateTealMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = SlateTealMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Prayer Name + Status Badge with Expanded Horizontal Spacing (Fixes squeezed text)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = renderedPrayerName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (prayer.isCurrent || isChecked) FontWeight.Bold else FontWeight.SemiBold,
                        color = prayerTitleColor,
                        fontSize = 15.5.sp
                    )
                )

                // Status Badge Logic Fix: isChecked takes precedence over isCurrent!
                if (isChecked) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0D6E58).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isArabic) "تم" else "Done",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF0D6E58),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                } else if (prayer.isCurrent && !isSunrise) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = if (isArabic) "الآن" else "Active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFB45309),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                }
            }
        }

        // Right: Compact Inline Green Notification Pill + Right-Aligned Time (Shifted Right)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!isSunrise) {
                // Inline Compact Notification Pill with Embedded Bell Icon
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isNotificationEnabled) SoftTealTint else Color(0xFFF1F5F9),
                        border = BorderStroke(
                            1.dp,
                            if (isNotificationEnabled) DeepVibrantTeal.copy(alpha = 0.35f) else BorderTealLight
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isDropdownOpen = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isNotificationEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                                contentDescription = if (isNotificationEnabled) stringResource(R.string.salat_notification_active) else stringResource(R.string.salat_notification_muted),
                                tint = if (isNotificationEnabled) DeepVibrantTeal else SlateTealMuted,
                                modifier = Modifier.size(13.5.dp)
                            )
                            Text(
                                text = shortOffsetLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isNotificationEnabled) DeepVibrantTeal else SlateTealMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Timing dropdown",
                                tint = if (isNotificationEnabled) DeepVibrantTeal else SlateTealMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isDropdownOpen,
                        onDismissRequest = { isDropdownOpen = false },
                        modifier = Modifier
                            .background(SurfaceWhite)
                            .border(BorderStroke(1.dp, BorderTealLight), RoundedCornerShape(12.dp))
                    ) {
                        // Toggle Mute / Turn Off
                        DropdownMenuItem(
                            text = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isNotificationEnabled) {
                                            if (isArabic) "إيقاف التنبيه (صامت)" else "Turn Off / Mute"
                                        } else {
                                            if (isArabic) "تفعيل التنبيه" else "Enable Notification"
                                        },
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isNotificationEnabled) Color(0xFFDC2626) else DeepVibrantTeal
                                        )
                                    )
                                }
                            },
                            onClick = {
                                onToggleNotification()
                                isDropdownOpen = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isNotificationEnabled) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (isNotificationEnabled) Color(0xFFDC2626) else DeepVibrantTeal,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        )

                        HorizontalDivider(color = BorderTealLight.copy(alpha = 0.5f), thickness = 0.8.dp)

                        val timingOptions = listOf(
                            -30 to R.string.salat_dropdown_30_before,
                            -20 to R.string.salat_dropdown_20_before,
                            -15 to R.string.salat_dropdown_15_before,
                            -10 to R.string.salat_dropdown_10_before,
                            -5 to R.string.salat_dropdown_5_before,
                            0 to R.string.salat_dropdown_exact,
                            5 to R.string.salat_dropdown_5_after,
                            10 to R.string.salat_dropdown_10_after
                        )

                        timingOptions.forEach { (offset, stringRes) ->
                            val isSelected = isNotificationEnabled && (offset == offsetMin)
                            DropdownMenuItem(
                                text = {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(stringRes),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) DeepVibrantTeal else DarkPine
                                            )
                                        )
                                    }
                                },
                                onClick = {
                                    onSetOffset(offset)
                                    isDropdownOpen = false
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DeepVibrantTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }

            // Fixed-Width Right-Aligned Time Container
            Text(
                text = prayer.timeString,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) Color(0xFF0D6E58) else if (prayer.isCurrent) DeepVibrantTeal else if (isSunrise) Color(0xFF92400E) else DarkPine,
                    fontSize = 15.5.sp
                ),
                modifier = Modifier.width(52.dp)
            )
        }
    }
}
