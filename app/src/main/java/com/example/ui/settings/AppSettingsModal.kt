package com.example.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.MainViewModel

// Palette tokens matching the clean Islamic light canvas
private val NoorTealDark = Color(0xFF099382)
private val NoorTealVibrant = Color(0xFF13A795)
private val NoorDarkPine = Color(0xFF10261F)
private val NoorSageSlate = Color(0xFF5A756C)
private val NoorGoldAccent = Color(0xFFD4A340)
private val NoorGoldSoft = Color(0xFFFAF3E6)
private val NoorGoldBorder = Color(0xFFE8D4A8)
private val NoorCardBorder = Color(0xFFE2EBE6)
private val NoorSurfaceSoft = Color(0xFFF6FAF8)
private val NoorSoftGreenBg = Color(0xFFF2F8F5)
private val NoorSoftGreenBorder = Color(0xFFCCE4DC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsModal(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val showArabicSecondary by viewModel.showArabicSecondaryText.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val morningAzkarNotif by viewModel.morningEveningAzkarNotification.collectAsStateWithLifecycle()
    val dailyAyahNotif by viewModel.dailyAyahNotification.collectAsStateWithLifecycle()
    val qazaNotif by viewModel.qazaReminderNotification.collectAsStateWithLifecycle()
    val vibrateAdhan by viewModel.vibrationOnAdhan.collectAsStateWithLifecycle()
    val adhanVolume by viewModel.adhanSoundVolume.collectAsStateWithLifecycle()

    // Independent expansion state for each individual drop-down accordion
    var isThemeExpanded by remember { mutableStateOf(false) }
    var isLanguageExpanded by remember { mutableStateOf(false) }
    var isNotifExpanded by remember { mutableStateOf(false) }
    var isCalcExpanded by remember { mutableStateOf(false) }
    var isContactExpanded by remember { mutableStateOf(false) }
    var isPrivacyExpanded by remember { mutableStateOf(false) }
    var isAboutExpanded by remember { mutableStateOf(false) }

    var showLanguageSelector by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        tonalElevation = 0.dp,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            // Elegant Drag Handle Pill with generous breathing room
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp, bottom = 12.dp)
                    .size(width = 42.dp, height = 4.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFD4E0DA))
            )

            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                border = BorderStroke(0.5.dp, NoorCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
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
                                .background(Brush.linearGradient(listOf(NoorTealDark, NoorTealVibrant))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.settings_title),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NoorDarkPine,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = stringResource(R.string.settings_subtitle),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NoorSageSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NoorSurfaceSoft)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NoorDarkPine,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. THEME ACCORDION (Fixed to Clean Light Theme)
                item(key = "section_theme") {
                    SettingsAccordionCard(
                        icon = Icons.Default.LightMode,
                        title = stringResource(R.string.settings_section_theme),
                        subtitle = stringResource(R.string.settings_theme_fixed_sub),
                        isExpanded = isThemeExpanded,
                        onToggleExpand = { isThemeExpanded = !isThemeExpanded }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.settings_theme_clean_light),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NoorDarkPine,
                                        fontSize = 14.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = stringResource(R.string.settings_theme_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NoorSageSlate,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Palette Preview Swatches
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PaletteSwatch(color = Color(0xFFF4FAF7), name = stringResource(R.string.settings_swatch_mint), modifier = Modifier.weight(1f))
                            PaletteSwatch(color = Color(0xFF099382), name = stringResource(R.string.settings_swatch_teal), isDark = true, modifier = Modifier.weight(1f))
                            PaletteSwatch(color = Color(0xFFD4A340), name = stringResource(R.string.settings_swatch_gold), isDark = true, modifier = Modifier.weight(1f))
                            PaletteSwatch(color = Color(0xFF10261F), name = stringResource(R.string.settings_swatch_pine), isDark = true, modifier = Modifier.weight(1f))
                        }
                    }
                }

                // 2. LANGUAGE & LOCALIZATION ACCORDION (Primary + Secondary Arabic Toggle)
                item(key = "section_language") {
                    SettingsAccordionCard(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.settings_section_language),
                        subtitle = stringResource(R.string.settings_language_sub),
                        isExpanded = isLanguageExpanded,
                        onToggleExpand = { isLanguageExpanded = !isLanguageExpanded }
                    ) {
                        // Global App Language Selector
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NoorSurfaceSoft)
                                .clickable { showLanguageSelector = !showLanguageSelector }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.settings_primary_language),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NoorSageSlate,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "$appLanguage",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NoorDarkPine,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Icon(
                                imageVector = if (showLanguageSelector) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = NoorTealDark
                            )
                        }

                        AnimatedVisibility(
                            visible = showLanguageSelector,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val languages = listOf(
                                    "English" to "English (Default)",
                                    "Arabic" to "العربية (Arabic)",
                                    "French" to "Français (French)",
                                    "Urdu" to "اردو (Urdu)",
                                    "Indonesian" to "Bahasa Indonesia",
                                    "Turkish" to "Türkçe (Turkish)"
                                )

                                languages.forEach { (code, label) ->
                                    val isSelected = appLanguage == code
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.setAppLanguage(code)
                                                showLanguageSelector = false
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) NoorSoftGreenBg else Color.White,
                                        border = BorderStroke(1.dp, if (isSelected) NoorTealDark else NoorCardBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) NoorTealDark else NoorDarkPine,
                                                    fontSize = 13.sp
                                                )
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = NoorTealDark,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // GLOBAL ARABIC SECONDARY LAYER TOGGLE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.settings_show_arabic_secondary),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NoorDarkPine,
                                        fontSize = 14.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(R.string.settings_show_arabic_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NoorSageSlate,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }

                            Switch(
                                checked = showArabicSecondary,
                                onCheckedChange = { viewModel.toggleArabicSecondaryText(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NoorTealDark,
                                    uncheckedThumbColor = NoorSageSlate,
                                    uncheckedTrackColor = NoorSurfaceSoft
                                )
                            )
                        }
                    }
                }

                // 3. NOTIFICATIONS & REMINDERS ACCORDION
                item(key = "section_notifications") {
                    SettingsAccordionCard(
                        icon = Icons.Default.Notifications,
                        title = stringResource(R.string.settings_section_notifications),
                        subtitle = stringResource(R.string.settings_notif_sub),
                        isExpanded = isNotifExpanded,
                        onToggleExpand = { isNotifExpanded = !isNotifExpanded }
                    ) {
                        NotificationToggleRow(
                            title = stringResource(R.string.settings_notif_azkar),
                            description = stringResource(R.string.settings_notif_azkar_desc),
                            checked = morningAzkarNotif,
                            onCheckedChange = { viewModel.toggleMorningEveningAzkarNotification() }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NotificationToggleRow(
                            title = stringResource(R.string.settings_notif_daily_ayah),
                            description = stringResource(R.string.settings_notif_ayah_desc),
                            checked = dailyAyahNotif,
                            onCheckedChange = { viewModel.toggleDailyAyahNotification() }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NotificationToggleRow(
                            title = stringResource(R.string.settings_notif_qaza),
                            description = stringResource(R.string.settings_notif_qaza_desc),
                            checked = qazaNotif,
                            onCheckedChange = { viewModel.toggleQazaReminderNotification() }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NotificationToggleRow(
                            title = stringResource(R.string.settings_notif_vibrate_adhan),
                            description = stringResource(R.string.settings_notif_vibrate_desc),
                            checked = vibrateAdhan,
                            onCheckedChange = { viewModel.toggleVibrationOnAdhan() }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Adhan Volume Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = NoorTealDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.settings_adhan_volume),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = NoorDarkPine
                                        )
                                    )
                                }
                                Text(
                                    text = "$adhanVolume%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NoorTealDark
                                    )
                                )
                            }
                            Slider(
                                value = adhanVolume.toFloat(),
                                onValueChange = { viewModel.setAdhanSoundVolume(it.toInt()) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = NoorTealDark,
                                    activeTrackColor = NoorTealDark,
                                    inactiveTrackColor = NoorCardBorder
                                )
                            )
                        }
                    }
                }

                // 4. PRAYER CALCULATION ACCORDION
                item(key = "section_calculation") {
                    SettingsAccordionCard(
                        icon = Icons.Default.Calculate,
                        title = stringResource(R.string.settings_section_calc),
                        subtitle = stringResource(R.string.settings_calc_sub),
                        trailingBadge = "MWL",
                        isExpanded = isCalcExpanded,
                        onToggleExpand = { isCalcExpanded = !isCalcExpanded }
                    ) {
                        Text(
                            text = stringResource(R.string.settings_calc_details),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NoorSageSlate,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                // 5. INDEPENDENT ACCORDION: CONTACT US & SUPPORT
                item(key = "section_contact_us") {
                    SettingsAccordionCard(
                        icon = Icons.Default.Email,
                        title = stringResource(R.string.settings_section_contact),
                        subtitle = stringResource(R.string.settings_contact_sub),
                        isExpanded = isContactExpanded,
                        onToggleExpand = { isContactExpanded = !isContactExpanded }
                    ) {
                        Text(
                            text = stringResource(R.string.settings_contact_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NoorSageSlate,
                                fontSize = 12.sp,
                                lineHeight = 16.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:support@alnoorapp.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "Al-Noor App Feedback & Support")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", "support@alnoorapp.com"))
                                        viewModel.showToast("Copied support@alnoorapp.com to clipboard")
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NoorTealDark),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.settings_email_support), fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", "support@alnoorapp.com"))
                                    viewModel.showToast("Email address copied: support@alnoorapp.com")
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, NoorTealDark.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NoorTealDark),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.settings_copy_email), fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 6. INDEPENDENT ACCORDION: PRIVACY POLICY & SECURITY
                item(key = "section_privacy_policy") {
                    SettingsAccordionCard(
                        icon = Icons.Default.Security,
                        title = stringResource(R.string.settings_section_privacy),
                        subtitle = stringResource(R.string.settings_privacy_sub),
                        isExpanded = isPrivacyExpanded,
                        onToggleExpand = { isPrivacyExpanded = !isPrivacyExpanded }
                    ) {
                        Text(
                            text = stringResource(R.string.settings_privacy_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NoorSageSlate,
                                fontSize = 12.sp,
                                lineHeight = 16.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(NoorSoftGreenBg)
                                .border(1.dp, NoorSoftGreenBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PrivacyItem(title = stringResource(R.string.settings_privacy_item1_title), detail = stringResource(R.string.settings_privacy_item1_desc))
                            PrivacyItem(title = stringResource(R.string.settings_privacy_item2_title), detail = stringResource(R.string.settings_privacy_item2_desc))
                            PrivacyItem(title = stringResource(R.string.settings_privacy_item3_title), detail = stringResource(R.string.settings_privacy_item3_desc))
                        }
                    }
                }

                // 7. INDEPENDENT ACCORDION: ABOUT US & MISSION
                item(key = "section_about_us") {
                    SettingsAccordionCard(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.settings_section_about),
                        subtitle = stringResource(R.string.settings_about_sub),
                        isExpanded = isAboutExpanded,
                        onToggleExpand = { isAboutExpanded = !isAboutExpanded }
                    ) {
                        Text(
                            text = stringResource(R.string.settings_about_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NoorSageSlate,
                                fontSize = 12.sp,
                                lineHeight = 16.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(NoorSurfaceSoft)
                                .border(1.dp, NoorCardBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.settings_about_sources_title),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NoorDarkPine,
                                    fontSize = 11.5.sp
                                )
                            )
                            Text(
                                text = stringResource(R.string.settings_about_sources_list),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NoorDarkPine.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }

                // 8. SHARE APP UTILITIES (Clean Standalone Card)
                item(key = "section_share") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, NoorCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(NoorSoftGreenBg)
                                        .border(1.dp, NoorSoftGreenBorder, RoundedCornerShape(11.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = NoorTealDark,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Share Al-Noor",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NoorDarkPine,
                                            fontSize = 15.5.sp
                                        )
                                    )
                                    Text(
                                        text = "Spread beneficial knowledge with loved ones",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = NoorSageSlate,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "\"Whoever guides someone to goodness will have a reward like one who did it.\" (Sahih Muslim)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NoorDarkPine,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 12.sp,
                                    lineHeight = 16.5.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(com.example.ui.components.NoorTopBarGradient)
                                        .clickable {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Assalamu Alaikum! Check out Al-Noor – Your spiritual companion with verified Quran, prayer times, authentic Du'as, Khatma plans, and Ask Noor AI.\n\nhttps://alnoorapp.com"
                                                )
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share Al-Noor via"))
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share App", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF4FAF7),
                                    border = BorderStroke(1.dp, Color(0xFFCCE4DC)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Al-Noor Link", "https://alnoorapp.com"))
                                            viewModel.showToast("App share link copied to clipboard!")
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = null,
                                            tint = NoorTealDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Copy Link",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = NoorTealDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 9. FOLLOW US ON SOCIAL MEDIA (Clean Standalone Card)
                item(key = "section_social") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, NoorCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(NoorSoftGreenBg)
                                        .border(1.dp, NoorSoftGreenBorder, RoundedCornerShape(11.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = NoorTealDark,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Follow Us",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NoorDarkPine,
                                            fontSize = 15.5.sp
                                        )
                                    )
                                    Text(
                                        text = "Official social channels & updates",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = NoorSageSlate,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SocialChannelChip(
                                    label = "𝕏 Twitter",
                                    handle = "@AlNoorIslamic",
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    onClick = {
                                        viewModel.showToast("Opening @AlNoorIslamic on 𝕏")
                                    }
                                )
                                SocialChannelChip(
                                    label = "YouTube",
                                    handle = "@AlNoorApp",
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    onClick = {
                                        viewModel.showToast("Opening @AlNoorApp on YouTube")
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SocialChannelChip(
                                    label = "Telegram",
                                    handle = "t.me/AlNoorApp",
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    onClick = {
                                        viewModel.showToast("Opening Al-Noor Telegram channel")
                                    }
                                )
                                SocialChannelChip(
                                    label = "Instagram",
                                    handle = "@AlNoor.App",
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    onClick = {
                                        viewModel.showToast("Opening @AlNoor.App on Instagram")
                                    }
                                )
                            }
                        }
                    }
                }

                item(key = "bottom_space") {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsAccordionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    trailingBadge: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (isExpanded) NoorTealDark.copy(alpha = 0.45f) else NoorCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (isExpanded) NoorSoftGreenBg else NoorSurfaceSoft)
                            .border(1.dp, if (isExpanded) NoorSoftGreenBorder else NoorCardBorder, RoundedCornerShape(11.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = NoorTealDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NoorDarkPine,
                                    fontSize = 15.sp
                                )
                            )
                            if (trailingBadge != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NoorSoftGreenBg,
                                    border = BorderStroke(0.8.dp, NoorSoftGreenBorder)
                                ) {
                                    Text(
                                        text = trailingBadge,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NoorTealDark
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NoorSageSlate,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (isExpanded) NoorSoftGreenBg else NoorSurfaceSoft,
                    border = BorderStroke(1.dp, if (isExpanded) NoorTealDark.copy(alpha = 0.4f) else NoorCardBorder),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = if (isExpanded) NoorTealDark else NoorSageSlate,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(12.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = NoorDarkPine
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NoorSageSlate,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NoorTealDark,
                uncheckedThumbColor = NoorSageSlate,
                uncheckedTrackColor = NoorSurfaceSoft
            )
        )
    }
}

@Composable
private fun PaletteSwatch(
    color: Color,
    name: String,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color,
        border = BorderStroke(1.dp, NoorCardBorder),
        modifier = modifier.height(32.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else NoorDarkPine
                )
            )
        }
    }
}

@Composable
private fun PrivacyItem(
    title: String,
    detail: String
) {
    Column {
        Text(
            text = "• $title",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NoorTealDark,
                fontSize = 12.sp
            )
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.bodySmall.copy(
                color = NoorDarkPine.copy(alpha = 0.85f),
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        )
    }
}

@Composable
private fun SocialChannelChip(
    label: String,
    handle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = NoorSurfaceSoft,
        border = BorderStroke(1.dp, NoorCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NoorDarkPine,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = handle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NoorSageSlate,
                        fontSize = 10.5.sp
                    )
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = NoorTealDark,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}
