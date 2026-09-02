package com.example.ui.profile

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.localization.tr
import com.example.ui.MainViewModel
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar

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
fun UserProfileScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userBio by viewModel.userBio.collectAsStateWithLifecycle()
    val isCloudSync by viewModel.isCloudSyncEnabled.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage == "ar"

    var showEditProfileSheet by remember { mutableStateOf(false) }
    var showChangePasswordSheet by remember { mutableStateOf(false) }
    var showConnectSheet by remember { mutableStateOf(false) }
    var showBackupRestoreSheet by remember { mutableStateOf(false) }
    var showPrivacyPolicySheet by remember { mutableStateOf(false) }
    var showContactSupportDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = tr("profile_title", viewModel),
                eyebrow = if (isArabic) "الملف الشخصي" else "SPIRITUAL PROFILE",
                subtitle = if (isUserLoggedIn) userName else (if (isArabic) "الحساب والمزامنة السحابية" else "Account & Cloud Sync"),
                onBackClick = onNavigateBack,
                backContentDescription = stringResource(R.string.action_back),
                actions = {
                    NoorGlassIconButton(
                        onClick = { viewModel.openSettingsModal() },
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            )
        },
        containerColor = Color.White,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. User Profile Header Card (Guest mode & avatar WITHOUT border, completely white)
            item(key = "user_header_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    UserProfileHeader(
                        viewModel = viewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        userName = userName,
                        userEmail = userEmail,
                        onEditClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true },
                        onAvatarClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true }
                    )
                }
            }

            // 2. Data & Sync Section (Backup and Restore, Cloud Sync)
            item(key = "data_sync_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DataSyncSection(
                        isUserLoggedIn = isUserLoggedIn,
                        isCloudSync = isCloudSync,
                        onBackupRestoreClick = { showBackupRestoreSheet = true },
                        onCloudSyncToggle = { viewModel.toggleCloudSync() },
                        onConnectClick = { showConnectSheet = true }
                    )
                }
            }

            // 3. App Preferences & Security Section
            item(key = "app_preferences_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PreferencesSecuritySection(
                        isUserLoggedIn = isUserLoggedIn,
                        onEditProfileClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true },
                        onChangePasswordClick = { if (isUserLoggedIn) showChangePasswordSheet = true else showConnectSheet = true },
                        onAppSettingsClick = { viewModel.openSettingsModal() }
                    )
                }
            }

            // 4. Support & Legal Section (Privacy Policy & Contact Us)
            item(key = "support_legal_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SupportLegalSection(
                        onPrivacyPolicyClick = { showPrivacyPolicySheet = true },
                        onContactSupportClick = { showContactSupportDialog = true }
                    )
                }
            }

            // 5. Account Actions Section (Log Out, Delete Account)
            item(key = "account_actions_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AccountActionsSection(
                        viewModel = viewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        onConnectClick = { showConnectSheet = true },
                        onLogOutClick = { showSignOutDialog = true },
                        onDeleteAccountClick = { showDeleteAccountDialog = true }
                    )
                }
            }
        }

        // Backup & Restore Bottom Sheet
        if (showBackupRestoreSheet) {
            BackupRestoreSheet(
                viewModel = viewModel,
                onDismiss = { showBackupRestoreSheet = false }
            )
        }

        // Privacy Policy Bottom Sheet
        if (showPrivacyPolicySheet) {
            PrivacyPolicyBottomSheet(
                onDismiss = { showPrivacyPolicySheet = false }
            )
        }

        // Contact Support Dialog
        if (showContactSupportDialog) {
            ContactSupportDialog(
                viewModel = viewModel,
                onDismiss = { showContactSupportDialog = false }
            )
        }

        // Connect Account Bottom Sheet
        if (showConnectSheet) {
            ConnectAccountBottomSheet(
                viewModel = viewModel,
                onDismiss = { showConnectSheet = false },
                onConnectSuccess = { name, email, bio ->
                    viewModel.connectUser(name, email, bio)
                    showConnectSheet = false
                }
            )
        }

        // Edit Profile Bottom Sheet
        if (showEditProfileSheet) {
            EditProfileBottomSheet(
                viewModel = viewModel,
                initialName = userName,
                initialEmail = userEmail,
                initialBio = userBio,
                onDismiss = { showEditProfileSheet = false },
                onSave = { name, email, bio ->
                    viewModel.updateUserProfile(name, email, bio, "")
                    showEditProfileSheet = false
                }
            )
        }

        // Change Password Bottom Sheet / Dialog
        if (showChangePasswordSheet) {
            ChangePasswordBottomSheet(
                viewModel = viewModel,
                onDismiss = { showChangePasswordSheet = false },
                onSave = { newPass ->
                    viewModel.updatePassword(newPass)
                    showChangePasswordSheet = false
                }
            )
        }

        // Sign Out Confirmation Dialog
        if (showSignOutDialog) {
            AlertDialog(
                onDismissRequest = { showSignOutDialog = false },
                title = { Text(tr("profile_confirm_signout", viewModel), fontWeight = FontWeight.Bold, color = NoorDarkPine) },
                text = { Text(tr("profile_confirm_signout_sub", viewModel), color = NoorSageSlate) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.disconnectUser()
                            showSignOutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(tr("profile_sign_out", viewModel), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignOutDialog = false }) {
                        Text(stringResource(R.string.action_cancel), color = NoorSageSlate)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Delete Account Confirmation Dialog
        if (showDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAccountDialog = false },
                title = { Text("Delete Account?", fontWeight = FontWeight.Bold, color = Color(0xFFC0392B)) },
                text = { Text("This will permanently remove your account data and spiritual profile from this device. This action cannot be undone.", color = NoorSageSlate) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteAccount()
                            showDeleteAccountDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete Permanently", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccountDialog = false }) {
                        Text(stringResource(R.string.action_cancel), color = NoorSageSlate)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

/**
 * 1. User Header: clean seamless white, NO border around container or avatar image
 */
@Composable
private fun UserProfileHeader(
    viewModel: MainViewModel,
    isUserLoggedIn: Boolean,
    userName: String,
    userEmail: String,
    onEditClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = null,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar without border
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(NoorSoftGreenBg)
                    .clickable { onAvatarClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_user_avatar),
                    contentDescription = "Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle edit badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(NoorTealDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Tap to edit",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isUserLoggedIn) userName else "Guest Mode",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NoorDarkPine,
                        fontSize = 19.sp
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (isUserLoggedIn && userEmail.isNotBlank()) userEmail else "Tap to connect account & sync data",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NoorSageSlate,
                        fontSize = 12.5.sp
                    ),
                    modifier = Modifier.clickable { onEditClick() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isUserLoggedIn) NoorSoftGreenBg else Color(0xFFF6FAF8),
                    border = BorderStroke(0.8.dp, if (isUserLoggedIn) NoorSoftGreenBorder else NoorCardBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = if (isUserLoggedIn) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isUserLoggedIn) NoorTealDark else NoorSageSlate,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isUserLoggedIn) "Connected (Cloud Sync Active)" else "Guest Mode (Local Only)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isUserLoggedIn) NoorTealDark else NoorSageSlate,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2. Data & Sync Section with soft border
 */
@Composable
private fun DataSyncSection(
    isUserLoggedIn: Boolean,
    isCloudSync: Boolean,
    onBackupRestoreClick: () -> Unit,
    onCloudSyncToggle: () -> Unit,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "DATA & SYNCHRONIZATION",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NoorSageSlate,
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, NoorCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Backup & Restore
                SettingsRowItem(
                    icon = Icons.Default.Sync,
                    title = "Backup & Restore",
                    subtitle = "Export to Drive/Email or import Khatma & bookmarks",
                    badge = "New",
                    onClick = onBackupRestoreClick
                )

                HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                // Cloud Sync Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!isUserLoggedIn) onConnectClick() else onCloudSyncToggle()
                        }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NoorSoftGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = NoorTealDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Cloud Synchronization",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NoorDarkPine,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = if (isUserLoggedIn && isCloudSync) "Active (Automatic cloud backup)" else "Off (Local device only)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NoorSageSlate,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = isUserLoggedIn && isCloudSync,
                        onCheckedChange = {
                            if (!isUserLoggedIn) onConnectClick() else onCloudSyncToggle()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NoorTealDark
                        )
                    )
                }
            }
        }
    }
}

/**
 * 3. App Preferences & Security Section with soft border
 */
@Composable
private fun PreferencesSecuritySection(
    isUserLoggedIn: Boolean,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ACCOUNT & SETTINGS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NoorSageSlate,
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, NoorCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // App Settings
                SettingsRowItem(
                    icon = Icons.Default.Settings,
                    title = "App Settings",
                    subtitle = "Language, theme, notifications & prayer sound",
                    onClick = onAppSettingsClick
                )

                HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                // Edit Profile
                SettingsRowItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update display name, avatar or bio",
                    onClick = onEditProfileClick
                )

                HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                // Change Password
                SettingsRowItem(
                    icon = Icons.Default.Lock,
                    title = "Security & Password",
                    subtitle = "Quick password & security update",
                    onClick = onChangePasswordClick
                )
            }
        }
    }
}

/**
 * 4. Support & Legal Section with soft border
 */
@Composable
private fun SupportLegalSection(
    onPrivacyPolicyClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "SUPPORT & LEGAL",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NoorSageSlate,
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, NoorCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Privacy Policy
                SettingsRowItem(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy & Data Security",
                    subtitle = "100% offline, zero tracking, your data belongs to you",
                    onClick = onPrivacyPolicyClick
                )

                HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                // Contact Us
                SettingsRowItem(
                    icon = Icons.Default.Email,
                    title = "Contact Us & Feedback",
                    subtitle = "Reach our support team or request new features",
                    onClick = onContactSupportClick
                )
            }
        }
    }
}

/**
 * 5. Account Actions with soft border
 */
@Composable
private fun AccountActionsSection(
    viewModel: MainViewModel,
    isUserLoggedIn: Boolean,
    onConnectClick: () -> Unit,
    onLogOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ACCOUNT ACTIONS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NoorSageSlate,
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, NoorCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                if (!isUserLoggedIn) {
                    SettingsRowItem(
                        icon = Icons.Default.CloudSync,
                        title = "Connect Account",
                        subtitle = "Sign in with Google, Apple or Email",
                        onClick = onConnectClick
                    )
                } else {
                    SettingsRowItem(
                        icon = Icons.Default.Logout,
                        title = "Log Out",
                        subtitle = "Sign out of your Noor account",
                        titleColor = Color(0xFFC0392B),
                        onClick = onLogOutClick
                    )

                    HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 16.dp))

                    SettingsRowItem(
                        icon = Icons.Default.Warning,
                        title = "Delete Account",
                        subtitle = "Permanently remove account and data",
                        titleColor = Color(0xFFC0392B),
                        onClick = onDeleteAccountClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    titleColor: Color = NoorDarkPine,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
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
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (titleColor == NoorDarkPine) NoorSoftGreenBg else Color(0xFFFDEDEC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (titleColor == NoorDarkPine) NoorTealDark else titleColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            fontSize = 14.sp
                        )
                    )
                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NoorSoftGreenBg,
                            border = BorderStroke(0.8.dp, NoorSoftGreenBorder)
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
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

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = NoorSageSlate.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyPolicyBottomSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NoorSoftGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NoorTealDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Privacy Policy & Security",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NoorDarkPine,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Your spiritual journey is private and protected",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NoorSageSlate,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Text(
                text = "Al-Noor is engineered from the ground up to guarantee total data sovereignty and privacy. We do not sell, track, or share your personal spiritual habits.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NoorDarkPine.copy(alpha = 0.85f),
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NoorSoftGreenBg)
                    .border(1.dp, NoorSoftGreenBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrivacyHighlightItem(
                    title = "100% Offline by Default",
                    description = "Quran text, translations, prayer times, and tasbih counts are stored directly on your local device."
                )
                PrivacyHighlightItem(
                    title = "Zero Ad Tracking",
                    description = "No third-party trackers, advertisements, or data brokers are embedded in the app."
                )
                PrivacyHighlightItem(
                    title = "Transparent Backups",
                    description = "You can export and import your entire database at any time using open, readable JSON format."
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NoorTealDark)
            ) {
                Text("Got It", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PrivacyHighlightItem(title: String, description: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = NoorTealDark,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NoorDarkPine,
                    fontSize = 13.sp
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
    }
}

@Composable
private fun ContactSupportDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val emailAddress = "support@alnoorapp.com"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoorSoftGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = NoorTealDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "Contact Al-Noor Team",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NoorDarkPine,
                        fontSize = 16.sp
                    )
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Have questions, feedback, or need help with your Quran Khatma and app settings? Reach out to our dedicated team anytime.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NoorSageSlate,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NoorSoftGreenBg,
                    border = BorderStroke(1.dp, NoorSoftGreenBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", emailAddress))
                            viewModel.showToast("Email address copied to clipboard!")
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = emailAddress,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NoorTealDark,
                                fontSize = 13.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Email",
                            tint = NoorTealDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$emailAddress")
                        putExtra(Intent.EXTRA_SUBJECT, "Al-Noor App Feedback / Support")
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, "Send Email via"))
                    } catch (e: Exception) {
                        viewModel.showToast("No email client installed. Email copied to clipboard.")
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NoorTealDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Open Email", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NoorSageSlate)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectAccountBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onConnectSuccess: (String, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var inputName by remember { mutableStateOf("Zaid Ibrahim") }
    var inputEmail by remember { mutableStateOf("zaid.ibrahim@example.com") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Connect Noor Account",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = NoorDarkPine,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sign in via Google, Apple or Email for secure cloud sync",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NoorSageSlate,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = inputName,
                onValueChange = { inputName = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NoorTealDark,
                    unfocusedBorderColor = NoorCardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = inputEmail,
                onValueChange = { inputEmail = it },
                label = { Text("Email / Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NoorTealDark,
                    unfocusedBorderColor = NoorCardBorder
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onConnectSuccess(inputName, inputEmail, "") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NoorTealDark)
            ) {
                Text(
                    text = "Connect & Sync",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileBottomSheet(
    viewModel: MainViewModel,
    initialName: String,
    initialEmail: String,
    initialBio: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var bio by remember { mutableStateOf(initialBio) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = NoorDarkPine,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email / Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio / Intention") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(name, email, bio) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NoorTealDark)
            ) {
                Text(
                    text = stringResource(R.string.action_save),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = NoorDarkPine,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Secure your account with a strong password",
                style = MaterialTheme.typography.bodySmall.copy(color = NoorSageSlate)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = oldPass,
                onValueChange = { oldPass = it },
                label = { Text("Current Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("New Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(newPass) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NoorTealDark)
            ) {
                Text(
                    text = "Update Password",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                )
            }
        }
    }
}
