package com.example.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
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
        containerColor = Color(0xFFF6FAF8),
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. User Profile Header Card
            item(key = "user_header_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    UserProfileCard(
                        viewModel = viewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        userName = userName,
                        userEmail = userEmail,
                        onEditClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true },
                        onAvatarClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true }
                    )
                }
            }

            // 2. Account & Security Section
            item(key = "account_security_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AccountSecuritySection(
                        viewModel = viewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        isCloudSync = isCloudSync,
                        onEditProfileClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true },
                        onChangePasswordClick = { if (isUserLoggedIn) showChangePasswordSheet = true else showConnectSheet = true },
                        onCloudSyncToggle = { viewModel.toggleCloudSync() },
                        onConnectClick = { showConnectSheet = true }
                    )
                }
            }

            // 3. Account Actions Section (Log Out, Delete Account)
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

@Composable
private fun UserProfileCard(
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
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(0.5.dp, NoorCardBorder),
        shadowElevation = 1.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar with tap-to-edit badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(NoorGoldSoft)
                    .border(2.dp, NoorGoldAccent, CircleShape)
                    .clickable { onAvatarClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_user_avatar),
                    contentDescription = "Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Edit camera/tap overlay badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(NoorTealDark)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Tap to edit",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isUserLoggedIn) userName else "Guest Mode",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NoorDarkPine,
                            fontSize = 19.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (isUserLoggedIn && userEmail.isNotBlank()) userEmail else "Tap to connect account & sync data",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NoorSageSlate,
                        fontSize = 12.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isUserLoggedIn) NoorSoftGreenBg else NoorGoldSoft,
                    border = BorderStroke(1.dp, if (isUserLoggedIn) NoorSoftGreenBorder else NoorGoldBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = if (isUserLoggedIn) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isUserLoggedIn) NoorTealDark else NoorGoldAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isUserLoggedIn) "Connected (Google / Cloud)" else "Guest Account",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUserLoggedIn) NoorTealDark else NoorGoldAccent,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountSecuritySection(
    viewModel: MainViewModel,
    isUserLoggedIn: Boolean,
    isCloudSync: Boolean,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onCloudSyncToggle: () -> Unit,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "ACCOUNT & SECURITY",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NoorSageSlate,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, NoorCardBorder),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                // Edit Profile
                SettingsRowItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update display name, avatar or bio",
                    onClick = onEditProfileClick
                )

                HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                // Change Password
                SettingsRowItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Quick security update",
                    onClick = onChangePasswordClick
                )

                HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                // Cloud Sync Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!isUserLoggedIn) onConnectClick() else onCloudSyncToggle()
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
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
                                text = "Cloud Sync Status",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NoorDarkPine,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = if (isUserLoggedIn && isCloudSync) "Active (Google / Apple / Email)" else "Paused / Guest",
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "ACCOUNT ACTIONS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NoorSageSlate,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, NoorCardBorder),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
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

                    HorizontalDivider(color = NoorCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

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
    titleColor: Color = NoorDarkPine,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    .background(if (titleColor == NoorDarkPine) NoorSurfaceSoft else Color(0xFFFDEDEC)),
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                )
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
            tint = NoorSageSlate.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
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
