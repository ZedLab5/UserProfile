package com.example.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.localization.tr
import com.example.data.model.PrayerTime
import com.example.data.quran.DuaData
import com.example.data.quran.KhatmaEngine
import com.example.data.quran.KhatmaPaceStatus
import com.example.data.quran.QuranData
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.SalatTab
import com.example.ui.components.AudioWaveformIndicator
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceWhite

// Accent Colors: Exact Gradient between 099382 and 13A795 & Tasteful Gold Highlights
private val NoorTealStart = Color(0xFF099382)
private val NoorTealEnd = Color(0xFF13A795)
val NoorAccentGradient = Brush.linearGradient(listOf(NoorTealStart, NoorTealEnd))

// Prayer Tracker Diagonal Gradient (Top-Left to Bottom-Right, 3 color stops)
val PrayerTrackerDiagonalGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF0FA895), // Bright teal-green start
        Color(0xFF14C4A8), // Lighter teal-green middle
        Color(0xFF0FA895)  // Bright teal-green end (subtle sheen effect)
    ),
    start = Offset.Zero,
    end = Offset.Infinite
)

// Subtle Warm Islamic Gold / Yellow Accents (Tasteful touches)
val NoorGoldAccent = Color(0xFFD4A340)
val NoorGoldLight = Color(0xFFE8BA5A)
val NoorGoldSoft = Color(0xFFFAF3E6)
val NoorGoldBorder = Color(0xFFE8D4A8)
val NoorGoldGradient = Brush.linearGradient(listOf(Color(0xFFE5B958), Color(0xFFC8932A)))
val NoorBorderGradient = Brush.linearGradient(listOf(NoorTealStart, NoorGoldAccent, NoorTealEnd))

private val NoorDarkPine = Color(0xFF10261F)
private val NoorSageSlate = Color(0xFF5A756C)
private val NoorCardBorder = Color(0xFFE2EBE6)
private val NoorSurfaceSoft = Color(0xFFF6FAF8)
private val NoorNightCanopy = Color(0xFF091F19)
private val NoorNightCanopyMid = Color(0xFF103328)

// Soft green palette for refined, consistent spiritual cards
val NoorSoftGreenBorder = Color(0xFFCCE4DC)
val NoorSoftGreenBg = Color(0xFFF2F8F5)
val NoorSoftGreenBadgeBg = Color(0xFFE0F3ED)

// ============================================================
// REUSABLE UNIFIED CARD STRUCTURE: (ICON + HEADING + SUBHEADING)
// ============================================================

@Composable
fun NoorSectionContainer(
    icon: ImageVector? = null,
    customIcon: (@Composable () -> Unit)? = null,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    containerShape: RoundedCornerShape = RoundedCornerShape(24.dp),
    backgroundBrush: Brush? = null,
    backgroundColor: Color = Color.White,
    borderBrush: Brush? = null,
    borderWidth: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = containerShape,
                spotColor = Color.Black.copy(alpha = 0.12f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(containerShape)
            .then(
                if (backgroundBrush != null) {
                    Modifier.background(backgroundBrush)
                } else {
                    Modifier.background(backgroundColor)
                }
            )
            .then(
                if (borderBrush != null && borderWidth.value > 0f) {
                    Modifier.border(borderWidth, borderBrush, containerShape)
                } else {
                    Modifier
                }
            )
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Standard Header: Leading Badge + Title & Subtitle + Optional Trailing Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Leading Icon Badge with clean minimalist styling matching settings
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(NoorSoftGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            if (customIcon != null) {
                                customIcon()
                            } else if (icon != null) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = NoorTealStart,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NoorDarkPine,
                                    letterSpacing = (-0.2).sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NoorSageSlate
                                )
                            )
                        }
                    }

                    if (actionLabel != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NoorGoldSoft,
                            modifier = Modifier.clickable { onActionClick?.invoke() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = actionLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NoorGoldAccent
                                    )
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = NoorGoldAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Body Content
                content()
            }
    }
}

// ============================================================
// CUSTOM MINIMAL ISLAMIC VECTOR ICONS (Clean & Refined)
// ============================================================

@Composable
fun IslamicIconAzkar(
    modifier: Modifier = Modifier,
    tint: Color = NoorTealStart
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.42f
        val r = w * 0.30f
        val strokeW = 1.6.dp.toPx()

        // Rosary Bead Loop
        val beadCount = 8
        for (i in 0 until beadCount) {
            val angle = Math.toRadians((i * 360.0 / beadCount) - 90.0)
            val bx = (cx + r * Math.cos(angle)).toFloat()
            val by = (cy + r * Math.sin(angle)).toFloat()
            drawCircle(color = tint, radius = 2.0.dp.toPx(), center = Offset(bx, by))
        }

        // Hanging Minaret Tassel
        drawLine(tint, Offset(cx, cy + r), Offset(cx, cy + r + h * 0.22f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawCircle(color = NoorGoldAccent, radius = 1.8.dp.toPx(), center = Offset(cx, cy + r + h * 0.26f))
    }
}

@Composable
fun IslamicIconQuranAudio(
    modifier: Modifier = Modifier,
    tint: Color = NoorTealStart
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.6.dp.toPx()

        // Headphone Arc
        val arcTop = h * 0.18f
        val arcBottom = h * 0.62f
        val leftX = w * 0.22f
        val rightX = w * 0.78f

        val path = Path().apply {
            moveTo(leftX, arcBottom)
            cubicTo(leftX, arcTop, rightX, arcTop, rightX, arcBottom)
        }
        drawPath(path, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Left & Right Minimal Ear Cushions
        drawRoundRect(
            color = tint,
            topLeft = Offset(leftX - 2.5.dp.toPx(), arcBottom - 2.dp.toPx()),
            size = Size(5.dp.toPx(), 9.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(rightX - 2.5.dp.toPx(), arcBottom - 2.dp.toPx()),
            size = Size(5.dp.toPx(), 9.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )

        // Center Minimal Wave Bars
        val cx = w / 2f
        drawLine(tint, Offset(cx - 3.dp.toPx(), h * 0.52f), Offset(cx - 3.dp.toPx(), h * 0.76f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(NoorGoldAccent, Offset(cx, h * 0.44f), Offset(cx, h * 0.84f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(tint, Offset(cx + 3.dp.toPx(), h * 0.52f), Offset(cx + 3.dp.toPx(), h * 0.76f), strokeWidth = strokeW, cap = StrokeCap.Round)
    }
}

@Composable
fun IslamicIconTasbeeh(
    modifier: Modifier = Modifier,
    tint: Color = NoorTealStart
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeW = 1.6.dp.toPx()

        // Outer Ring
        drawCircle(
            color = tint,
            radius = w * 0.34f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeW)
        )

        // Inner Counter Dial & Notch
        drawCircle(
            color = tint.copy(alpha = 0.15f),
            radius = w * 0.18f,
            center = Offset(cx, cy),
            style = Fill
        )
        drawCircle(
            color = NoorGoldAccent,
            radius = 2.dp.toPx(),
            center = Offset(cx, cy)
        )

        // Top Clicker Button
        drawLine(
            color = NoorGoldAccent,
            start = Offset(cx, h * 0.08f),
            end = Offset(cx, h * 0.16f),
            strokeWidth = strokeW * 1.2f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun IslamicIconDua(
    modifier: Modifier = Modifier,
    tint: Color = NoorTealStart
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.6.dp.toPx()

        // Left Hand
        val leftHand = Path().apply {
            moveTo(w * 0.46f, h * 0.80f)
            lineTo(w * 0.22f, h * 0.65f)
            cubicTo(w * 0.16f, h * 0.46f, w * 0.26f, h * 0.26f, w * 0.44f, h * 0.24f)
            lineTo(w * 0.46f, h * 0.80f)
        }
        drawPath(leftHand, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Right Hand
        val rightHand = Path().apply {
            moveTo(w * 0.54f, h * 0.80f)
            lineTo(w * 0.78f, h * 0.65f)
            cubicTo(w * 0.84f, h * 0.46f, w * 0.74f, h * 0.26f, w * 0.56f, h * 0.24f)
            lineTo(w * 0.54f, h * 0.80f)
        }
        drawPath(rightHand, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))
    }
}

@Composable
fun IslamicIconTask(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = w * 0.36f
        val path = Path()

        // 8-Point Islamic Star
        val points = 16
        for (i in 0 until points) {
            val r = if (i % 2 == 0) radius else radius * 0.65f
            val angle = Math.toRadians((i * 360.0 / points) - 90.0)
            val x = (cx + r * Math.cos(angle)).toFloat()
            val y = (cy + r * Math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color = tint, style = Stroke(width = 1.6.dp.toPx()))

        // Inner Check Mark
        val checkPath = Path().apply {
            moveTo(w * 0.36f, cy)
            lineTo(w * 0.47f, cy + 3.dp.toPx())
            lineTo(w * 0.64f, cy - 3.5.dp.toPx())
        }
        drawPath(checkPath, color = tint, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun IslamicIconSalat(
    modifier: Modifier = Modifier,
    tint: Color = NoorTealStart
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.6.dp.toPx()

        // Mihrab Arch
        val path = Path().apply {
            moveTo(w * 0.24f, h * 0.84f)
            lineTo(w * 0.24f, h * 0.46f)
            cubicTo(w * 0.24f, h * 0.24f, w * 0.5f, h * 0.16f, w * 0.5f, h * 0.14f)
            cubicTo(w * 0.5f, h * 0.16f, w * 0.76f, h * 0.24f, w * 0.76f, h * 0.46f)
            lineTo(w * 0.76f, h * 0.84f)
        }
        drawPath(path, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Floor Base & Mihrab Lamp
        drawLine(tint, Offset(w * 0.16f, h * 0.84f), Offset(w * 0.84f, h * 0.84f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawCircle(color = NoorGoldAccent, radius = 2.dp.toPx(), center = Offset(w * 0.5f, h * 0.44f))
        drawLine(tint, Offset(w * 0.5f, h * 0.24f), Offset(w * 0.5f, h * 0.42f), strokeWidth = 1.2.dp.toPx())
    }
}

@Composable
fun IslamicIconQibla(
    modifier: Modifier = Modifier,
    tint: Color = NoorTealStart
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeW = 1.6.dp.toPx()

        // Outer Compass Ring
        drawCircle(
            color = tint,
            radius = w * 0.34f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeW)
        )

        // North Pointer with Gold Tip
        val needlePath = Path().apply {
            moveTo(cx, h * 0.20f)
            lineTo(cx - 3.5.dp.toPx(), cy + 2.dp.toPx())
            lineTo(cx, cy)
            lineTo(cx + 3.5.dp.toPx(), cy + 2.dp.toPx())
            close()
        }
        drawPath(needlePath, color = NoorGoldAccent)

        // Kaaba Cube Base
        drawRect(
            color = tint,
            topLeft = Offset(cx - 3.5.dp.toPx(), cy + 3.dp.toPx()),
            size = Size(7.dp.toPx(), 6.5.dp.toPx())
        )
    }
}

@Composable
fun IslamicIconMushaf(
    modifier: Modifier = Modifier,
    tint: Color = NoorTealStart
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val strokeW = 1.6.dp.toPx()

        // Open Holy Book (Mushaf)
        val leftPage = Path().apply {
            moveTo(cx, h * 0.50f)
            cubicTo(w * 0.36f, h * 0.46f, w * 0.22f, h * 0.36f, w * 0.18f, h * 0.32f)
            lineTo(w * 0.18f, h * 0.66f)
            cubicTo(w * 0.22f, h * 0.70f, w * 0.36f, h * 0.80f, cx, h * 0.84f)
        }
        drawPath(leftPage, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        val rightPage = Path().apply {
            moveTo(cx, h * 0.50f)
            cubicTo(w * 0.64f, h * 0.46f, w * 0.78f, h * 0.36f, w * 0.82f, h * 0.32f)
            lineTo(w * 0.82f, h * 0.66f)
            cubicTo(w * 0.78f, h * 0.70f, w * 0.64f, h * 0.80f, cx, h * 0.84f)
        }
        drawPath(rightPage, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Center Spine & Gold Ribbon
        drawLine(tint, Offset(cx, h * 0.50f), Offset(cx, h * 0.84f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(NoorGoldAccent, Offset(cx, h * 0.50f), Offset(cx + 2.dp.toPx(), h * 0.92f), strokeWidth = 1.4.dp.toPx(), cap = StrokeCap.Round)
    }
}

// ============================================================
// 1. STANDALONE ATMOSPHERIC MOSQUE HERO CARD (Luxury Islamic Aesthetic)
// ============================================================

@Composable
fun HomeHeaderSection(
    viewModel: MainViewModel,
    userName: String,
    location: String,
    nextPrayerName: String,
    nextPrayerTime: String,
    countdown: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val heroTrayShape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(heroTrayShape)
            .background(Color(0xFF0D1B1E), shape = heroTrayShape)
    ) {
        // Background Mosque Image centered
        Image(
            painter = painterResource(id = R.drawable.img_pinterest_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(0f, -0.15f),
            alpha = 1.0f,
            modifier = Modifier.matchParentSize()
        )

        // Soft black overlay on top of the image so it feels darker and less raw blue
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        // Deep rich vertical black gradient overlay for crisp text readability
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66000000),
                            Color(0x99000000),
                            Color(0xEE000000)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 22.dp, end = 22.dp, top = 48.dp, bottom = 80.dp)
                .offset(y = (-10).dp)
        ) {
            // Top User Profile Bar with Top-Right Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(NoorDestination.PROFILE) }
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(1.5.dp, NoorGoldAccent, CircleShape)
                            .padding(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_user_avatar),
                            contentDescription = tr("home_user_avatar", viewModel),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Column {
                        Text(
                            text = tr("home_header_greeting", viewModel),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = NoorGoldAccent
                            )
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (userName.isBlank() || userName == "Guest Mode") tr("home_header_guest", viewModel) else userName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = NoorGoldAccent.copy(alpha = 0.85f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = NoorGoldAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = location.ifBlank { tr("home_header_default_location", viewModel) },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }

                // Top Right Action Buttons: Customize & Settings (Clean translucent with subtle glass border)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Customize Button
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { viewModel.openCustomizeHomeSheet() },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DashboardCustomize,
                                contentDescription = if (isArabic) "تخصيص" else "Customize",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    // 2. Settings Button
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { viewModel.openSettingsModal() },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = if (isArabic) "الإعدادات" else "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Editorial Immersive Next Prayer Showcase Banner with Gold Accents
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, NoorGoldAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 13.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(NoorGoldAccent)
                            )
                            Text(
                                text = tr("home_header_upcoming_salat", viewModel),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = NoorGoldAccent
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = nextPrayerName.ifBlank { tr("prayer_isha", viewModel) },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                color = Color.White
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = nextPrayerTime.ifBlank { "05:36 pm" },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                letterSpacing = (-0.3).sp,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val displayCountdown = if (countdown.contains("left", ignoreCase = true) || countdown.contains("متبقية", ignoreCase = true)) {
                            countdown
                        } else {
                            String.format(tr("home_header_time_left", viewModel), countdown)
                        }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0x33099382),
                            border = BorderStroke(1.dp, NoorGoldAccent.copy(alpha = 0.6f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(NoorGoldAccent)
                                )
                                Text(
                                    text = displayCountdown,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NoorGoldAccent
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// 2. MERGED SALAT TIMES & TRACKER CARD (Overlapping Hero Tray Layer)
// ============================================================

@Composable
fun ChronologicalPrayerTracker(
    viewModel: MainViewModel,
    prayers: List<PrayerTime>,
    modifier: Modifier = Modifier
) {
    val completedPrayers by viewModel.completedPrayers.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val infiniteTransition = rememberInfiniteTransition(label = "salat_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Outer Green Container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.12f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF074E44),
                        Color(0xFF0C8A79),
                        Color(0xFF074E44)
                    )
                )
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row with white text matching green background
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IslamicIconSalat(modifier = Modifier.size(20.dp), tint = Color.White)
                    }

                    Column {
                        Text(
                            text = if (isArabic) "مواقيت وتتبع الصلاة" else "Prayer Times & Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "سجّل صلواتك اليومية" else "Tap to record prayers",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NoorGoldSoft,
                    modifier = Modifier.clickable { viewModel.navigateToSalat(SalatTab.TIMES) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isArabic) "تذكيرات" else "Reminders",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NoorGoldAccent
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = NoorGoldAccent,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inner White Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFF8FAFC)
                                )
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 12.dp)
                ) {
                    // Interactive Prayer Timeline: Connected line with circular checkmark nodes
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Connecting timeline rail spanning across the node centers, perfectly centered
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .padding(top = 8.dp)
                        ) {
                            val itemWidth = size.width / 5f
                            val startX = itemWidth / 2f
                            val endX = size.width - (itemWidth / 2f)
                            val centerY = 17.dp.toPx()
                            drawLine(
                                color = NoorSoftGreenBorder,
                                start = Offset(startX, centerY),
                                end = Offset(endX, centerY),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // 5 Daily Prayer Tracker Nodes Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            val prayerItems = listOf(
                                Pair(tr("prayer_fajr", viewModel), "Fajr"),
                                Pair(tr("prayer_dhuhr", viewModel), "Dhuhr"),
                                Pair(tr("prayer_asr", viewModel), "Asr"),
                                Pair(tr("prayer_maghrib", viewModel), "Maghrib"),
                                Pair(tr("prayer_isha", viewModel), "Isha")
                            )

                            prayerItems.forEachIndexed { index, (localizedName, keyName) ->
                                val matchingPrayer = prayers.find { it.name.equals(keyName, ignoreCase = true) }
                                val isChecked = matchingPrayer != null && completedPrayers.contains(matchingPrayer.name)
                                val isCurrent = matchingPrayer?.isCurrent == true
                                val isActionable = matchingPrayer != null && (matchingPrayer.isPast || matchingPrayer.isCurrent)

                                val timeText = matchingPrayer?.timeString ?: when (index) {
                                    0 -> "05:43"
                                    1 -> "12:45"
                                    2 -> "16:39"
                                    3 -> "19:15"
                                    4 -> "21:07"
                                    else -> "--:--"
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            matchingPrayer?.let { viewModel.togglePrayerCompleted(it) }
                                        }
                                        .padding(vertical = 8.dp, horizontal = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .scale(if (isCurrent && !isChecked) pulseScale else 1f)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isChecked -> NoorTealStart
                                                    else -> Color.White
                                                }
                                            )
                                            .border(
                                                width = if (isChecked) 2.dp else if (isCurrent) 2.5.dp else 1.2.dp,
                                                color = when {
                                                    isChecked -> Color.White
                                                    isCurrent -> NoorTealStart
                                                    else -> NoorSoftGreenBorder
                                                },
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            isChecked -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = tr("home_completed", viewModel),
                                                    tint = Color.White,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                            isCurrent -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Active Salat",
                                                    tint = NoorTealStart,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                            isActionable -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Not Yet Completed",
                                                    tint = NoorSageSlate.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            else -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Upcoming",
                                                    tint = NoorSageSlate.copy(alpha = 0.25f),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = localizedName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isCurrent || isChecked) FontWeight.Bold else FontWeight.SemiBold,
                                            color = when {
                                                isCurrent -> NoorTealStart
                                                isChecked -> NoorTealStart
                                                else -> NoorDarkPine
                                            }
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = timeText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCurrent) NoorDarkPine else NoorSageSlate
                                        )
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

@Composable
fun SpiritualEssentialCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, NoorSoftGreenBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoorSoftGreenBg)
                        .border(1.dp, NoorSoftGreenBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = NoorSageSlate,
                    modifier = Modifier.size(14.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NoorDarkPine
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.5.sp,
                        color = NoorSageSlate
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ============================================================
// 2. SPIRITUAL ESSENTIALS (6 Clean Cards Wrapped in Uniform Standard Section Container)
// ============================================================

@Composable
fun SpiritualEssentialsGrid(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NoorSectionContainer(
        icon = Icons.AutoMirrored.Filled.MenuBook,
        title = stringResource(R.string.home_spiritual_essentials_title),
        subtitle = stringResource(R.string.home_spiritual_essentials_subtitle),
        actionLabel = stringResource(R.string.home_all_tools),
        onActionClick = { viewModel.navigateTo(NoorDestination.ALL_TOOLS) },
        modifier = modifier
    ) {
        // 6 Cards (3 Stacked Over 3) housed comfortably inside the uniform card container
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1 (3 Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Holy Qur'an
                SpiritualEssentialCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_holy_quran),
                    subtitle = stringResource(R.string.home_surahs_count),
                    icon = { IslamicIconMushaf(modifier = Modifier.size(20.dp), tint = NoorTealStart) },
                    onClick = { viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
                )

                // Card 2: Qibla Finder
                SpiritualEssentialCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_qibla_finder),
                    subtitle = stringResource(R.string.home_live_kaaba),
                    icon = { IslamicIconQibla(modifier = Modifier.size(20.dp), tint = NoorTealStart) },
                    onClick = { viewModel.navigateTo(NoorDestination.QIBLA) }
                )

                // Card 3: Smart Tasbih
                SpiritualEssentialCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_tasbih),
                    subtitle = stringResource(R.string.home_daily_dhikr),
                    icon = { IslamicIconTasbeeh(modifier = Modifier.size(20.dp), tint = NoorTealStart) },
                    onClick = { viewModel.navigateTo(NoorDestination.TASBIH) }
                )
            }

            // Row 2 (3 Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 4: Du'as & Fortress
                SpiritualEssentialCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_duas_azkar),
                    subtitle = stringResource(R.string.home_hisn_muslim),
                    icon = { IslamicIconDua(modifier = Modifier.size(20.dp), tint = NoorTealStart) },
                    onClick = { viewModel.navigateTo(NoorDestination.DUAS_LIBRARY) }
                )

                // Card 5: Salat
                SpiritualEssentialCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_salat_qaza),
                    subtitle = stringResource(R.string.home_times_streaks),
                    icon = { IslamicIconSalat(modifier = Modifier.size(20.dp), tint = NoorTealStart) },
                    onClick = { viewModel.navigateToSalat(SalatTab.TIMES) }
                )

                // Card 6: Daily Habits
                SpiritualEssentialCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_sunnah_habits),
                    subtitle = stringResource(R.string.home_daily_routines),
                    icon = { IslamicIconTask(modifier = Modifier.size(20.dp), tint = NoorTealStart) },
                    onClick = { viewModel.navigateTo(NoorDestination.HABIT_TRACKER) }
                )
            }
        }
    }
}

// Backward compatibility alias
@Composable
fun GeometricFeaturedGrid(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    SpiritualEssentialsGrid(viewModel = viewModel, modifier = modifier)
}

// ============================================================
// 4. QURAN ACTIVE READING PROGRESS
// ============================================================

@Composable
fun QuranContinuationWidget(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val progressEntity by viewModel.readingProgress.collectAsStateWithLifecycle()

    val hasBookmark = progressEntity != null
    val surahName = progressEntity?.surahName ?: stringResource(R.string.home_fatihah_name)
    val ayahNum = progressEntity?.ayahNumber ?: 1
    val totalAyahs = progressEntity?.totalAyahs ?: 7

    val onAction = {
        if (progressEntity != null) {
            viewModel.resumeReading(progressEntity!!)
        } else {
            viewModel.selectSurahForReading(QuranData.surahs.first(), 0)
        }
    }

    NoorSectionContainer(
        icon = Icons.AutoMirrored.Filled.MenuBook,
        title = if (hasBookmark) stringResource(R.string.home_continue_reading) else stringResource(R.string.home_start_reading),
        subtitle = if (hasBookmark) stringResource(R.string.home_surah_ayah_progress, surahName, ayahNum, totalAyahs) else stringResource(R.string.home_surah_fatihah_sub),
        actionLabel = stringResource(R.string.home_open_mushaf),
        onActionClick = onAction,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAction),
            shape = RoundedCornerShape(20.dp),
            color = NoorSurfaceSoft,
            border = BorderStroke(1.dp, NoorCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (hasBookmark) surahName else stringResource(R.string.home_fatihah_name),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = NoorDarkPine
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NoorGoldSoft,
                            border = BorderStroke(1.dp, NoorGoldBorder)
                        ) {
                            Text(
                                text = if (hasBookmark) stringResource(R.string.home_ayah_counter, ayahNum, totalAyahs) else stringResource(R.string.home_surah_1),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NoorGoldAccent
                                )
                            )
                        }
                    }

                    Text(
                        text = if (hasBookmark) "Juz ${progressEntity?.let { KhatmaEngine.getAyahCoordinate(KhatmaEngine.getAbsoluteAyahIndex(it.surahNumber, it.ayahNumber)).juzNumber } ?: 1}" else "Meccan • 7 Ayahs",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NoorSageSlate
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (hasBookmark) stringResource(R.string.home_saved_bookmark_desc) else stringResource(R.string.home_begin_recitation_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = NoorSageSlate,
                        lineHeight = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                val progress = if (hasBookmark) (ayahNum.toFloat() / totalAyahs.toFloat()).coerceIn(0.05f, 1f) else 0.05f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE2EBE6))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(NoorAccentGradient)
                    )
                }
            }
        }
    }
}

// ============================================================
// 4B. QURAN KHATMA PROGRESS WIDGET (PROMINENT STANDOUT DESIGN)
// ============================================================

@Composable
fun QuranKhatmaHomeWidget(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val khatmaState by viewModel.khatmaDashboardState.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val state = khatmaState
    val isPlanActive = state != null && !state.plan.isCompleted

    val hasBookmark = readingProgress != null
    val surahName = readingProgress?.surahName ?: stringResource(R.string.home_fatihah_name)
    val ayahNum = readingProgress?.ayahNumber ?: 1
    val totalAyahs = readingProgress?.totalAyahs ?: 7

    val onResumeReading = {
        if (readingProgress != null) {
            viewModel.resumeReading(readingProgress!!)
        } else {
            viewModel.selectSurahForReading(QuranData.surahs.first(), 0)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Subtle ambient gradient aura in the top-right corner
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                (if (isPlanActive) DeepVibrantTeal else MetallicGold).copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(NoorDestination.QURAN_KHATMA) },
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
                                .size(44.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(if (isPlanActive) SoftTealTint else GoldBadgeBg)
                                .border(1.dp, if (isPlanActive) BorderTealGray else MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = stringResource(R.string.home_quran_khatma),
                                tint = if (isPlanActive) DeepVibrantTeal else MetallicGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.home_khatma_plan_title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = DarkPine
                                    )
                                )
                                if (isPlanActive) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SoftTealTint,
                                        border = BorderStroke(0.5.dp, DeepVibrantTeal.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = "Day ${state.currentDayNumber}/${state.totalDays}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = DeepVibrantTeal,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isPlanActive) {
                                    "${state.daysRemaining} days remaining • Target: ${state.todayTargetAyahs} Ayahs/day"
                                } else {
                                    "Complete the Noble Quran at your personal pace"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = SlateTealMuted
                                )
                            )
                        }
                    }

                    // Single CTA Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepVibrantTeal,
                        modifier = Modifier.clickable { viewModel.navigateTo(NoorDestination.QURAN_KHATMA) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (isPlanActive) stringResource(R.string.action_details) else stringResource(R.string.action_start),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                if (isPlanActive) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Prominent Progress Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "${state.readAyahsCount} / ${state.totalAyahs} Ayahs",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DarkPine,
                                    fontSize = 19.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Today: ${state.todayReadAyahs} of ${state.todayTargetAyahs} Ayahs read",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (state.isTodayTargetAchieved) DeepVibrantTeal else SlateTealMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (state.isTodayTargetAchieved) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }

                        // Big Percentage Badge
                        Text(
                            text = "${state.progressPercentage}%",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DeepVibrantTeal,
                                fontSize = 24.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prominent Glowing Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFFE2EBE6))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(state.progressFraction.coerceIn(0.02f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(PrimaryTealGradient)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Footer Target Detail
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(DeepVibrantTeal)
                            )
                            Text(
                                text = "Next Reading: ${state.nextReadingPosition.displayShort} (Juz ${state.currentPosition.juzNumber})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DarkPine,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.5.sp
                                )
                            )
                        }

                        val paceLabel = when (state.paceStatus) {
                            KhatmaPaceStatus.AHEAD -> "Ahead of schedule"
                            KhatmaPaceStatus.ON_TRACK -> "On track"
                            KhatmaPaceStatus.BEHIND -> "Behind schedule"
                            KhatmaPaceStatus.COMPLETED -> "Completed"
                        }
                        Text(
                            text = paceLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (state.isTodayTargetAchieved) DeepVibrantTeal else MetallicGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                    // When no plan active: standout invitation showcase with subtle inner border
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SoftTealTint.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, BorderTealGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(NoorDestination.QURAN_KHATMA) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = DeepVibrantTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Choose a 7, 15, 30, or 60 day completion plan with dynamic daily Ayah pacing & milestone celebrations.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DarkPine,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }

                    // Divider and Start / Continue Reading Section
                    AnimatedVisibility(
                        visible = !isPlanActive,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // "Or..." Divider Text
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    thickness = 1.dp,
                                    color = Color(0xFFE2EBE6)
                                )
                                Text(
                                    text = if (hasBookmark) "Or continue reading from your bookmark" else "Or start free recitation without a plan",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SlateTealMuted
                                    )
                                )
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    thickness = 1.dp,
                                    color = Color(0xFFE2EBE6)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Start / Continue Reading Inner Card
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = onResumeReading),
                                shape = RoundedCornerShape(16.dp),
                                color = NoorSurfaceSoft,
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
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f, fill = false)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(NoorSoftGreenBg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                                    contentDescription = null,
                                                    tint = NoorTealStart,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }

                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = if (hasBookmark) surahName else stringResource(R.string.home_fatihah_name),
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp,
                                                            color = NoorDarkPine
                                                        ),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = NoorGoldSoft,
                                                        border = BorderStroke(1.dp, NoorGoldBorder)
                                                    ) {
                                                        Text(
                                                            text = if (hasBookmark) stringResource(R.string.home_ayah_counter, ayahNum, totalAyahs) else stringResource(R.string.home_surah_1),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 10.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = NoorGoldAccent
                                                            )
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = if (hasBookmark) "Juz ${readingProgress?.let { KhatmaEngine.getAyahCoordinate(KhatmaEngine.getAbsoluteAyahIndex(it.surahNumber, it.ayahNumber)).juzNumber } ?: 1}" else "Meccan • 7 Ayahs",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = NoorSageSlate
                                                    )
                                                )
                                            }
                                        }

                                        // Action pill / button
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = SoftTealTint,
                                            border = BorderStroke(1.dp, BorderTealGray)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = if (hasBookmark) stringResource(R.string.action_continue) else stringResource(R.string.home_open_mushaf),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = DeepVibrantTeal
                                                    )
                                                )
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                    contentDescription = null,
                                                    tint = DeepVibrantTeal,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = if (hasBookmark) stringResource(R.string.home_saved_bookmark_desc) else stringResource(R.string.home_begin_recitation_desc),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = NoorSageSlate,
                                            lineHeight = 16.sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Progress Bar
                                    val progress = if (hasBookmark) (ayahNum.toFloat() / totalAyahs.toFloat()).coerceIn(0.05f, 1f) else 0.05f
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFFE2EBE6))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(progress)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(NoorAccentGradient)
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

// ============================================================
// 5. DAILY REVELATION (AYAH OF THE DAY & AUTHENTIC SUPPLICATION)
// ============================================================

@Composable
fun DailyAyahAndDuaShowcase(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val dailyAyah = DuaData.dailyAyah
    val dailyDua = DuaData.dailyDua
    val showArabicSecondary by viewModel.showArabicSecondaryText.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val isArabicPrimary = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية" || appLanguage.startsWith("ar", ignoreCase = true)

    NoorSectionContainer(
        icon = Icons.Default.BookmarkBorder,
        title = stringResource(R.string.home_daily_revelation),
        subtitle = stringResource(R.string.home_daily_revelation_sub),
        actionLabel = stringResource(R.string.action_copy),
        onActionClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val textToCopy = if (isArabicPrimary) {
                "${dailyAyah.arabicText}\n(${dailyAyah.referenceAr.ifBlank { dailyAyah.reference }})"
            } else {
                "${dailyAyah.translation} (${dailyAyah.reference})${if (showArabicSecondary) "\n" + dailyAyah.arabicText else ""}"
            }
            val clip = ClipData.newPlainText("Ayah of the Day", textToCopy)
            clipboard.setPrimaryClip(clip)
            viewModel.showToast(context.getString(R.string.home_ayah_copied))
        },
        modifier = modifier
    ) {
        // Daily Ayah Editorial Manuscript - Inner border styling
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.Transparent,
            border = BorderStroke(1.2.dp, NoorGoldAccent.copy(alpha = 0.55f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFFDF5),
                                Color(0xFFFFF9EE),
                                Color(0xFFFFF4E0)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFAF0D7),
                            border = BorderStroke(1.dp, Color(0xFFE8D2A0))
                        ) {
                            Text(
                                text = stringResource(R.string.home_ayah_of_the_day),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = NoorGoldAccent
                                )
                            )
                        }

                        Text(
                            text = if (isArabicPrimary) dailyAyah.referenceAr.ifBlank { dailyAyah.reference } else dailyAyah.reference,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NoorDarkPine
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isArabicPrimary) {
                        // In Arabic mode: ONLY show pristine Arabic text, no English translation or transliteration
                        Text(
                            text = dailyAyah.arabicText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal,
                                color = NoorDarkPine,
                                textAlign = TextAlign.Start,
                                lineHeight = 38.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // 1. PRIMARY LAYER (ENGLISH DOMINANT): Regular font weight, no quotes
                        Text(
                            text = dailyAyah.translation,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.5.sp,
                                color = NoorDarkPine,
                                lineHeight = 24.sp
                            )
                        )

                        // 2. SECONDARY LAYER: Phonetic Transliteration
                        if (dailyAyah.transliteration.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = dailyAyah.transliteration,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 13.5.sp,
                                    color = NoorSageSlate,
                                    lineHeight = 20.sp
                                )
                            )
                        }

                        // 3. TERTIARY LAYER: Traditional Arabic Script (Subject to global toggle, Regular weight)
                        if (showArabicSecondary && dailyAyah.arabicText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = NoorGoldBorder.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = dailyAyah.arabicText,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = NoorDarkPine.copy(alpha = 0.9f),
                                    textAlign = TextAlign.End,
                                    lineHeight = 36.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Daily Dua with dynamic language switching & regular font weight for Arabic
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(NoorDestination.DUAS_LIBRARY) },
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(1.dp, NoorGoldBorder.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IslamicIconDua(modifier = Modifier.size(20.dp), tint = NoorGoldAccent)
                        Text(
                            text = if (isArabicPrimary) dailyDua.categoryAr.ifBlank { "دعاء اليوم" } else dailyDua.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = NoorGoldAccent
                            )
                        )
                    }

                    Text(
                        text = if (isArabicPrimary) dailyDua.referenceAr.ifBlank { dailyDua.reference } else dailyDua.reference,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NoorSageSlate
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isArabicPrimary) {
                    // In Arabic mode: ONLY show pristine Arabic text
                    Text(
                        text = dailyDua.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = NoorDarkPine,
                            textAlign = TextAlign.Start,
                            lineHeight = 30.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // 1. Primary Layer: English Translation
                    Text(
                        text = dailyDua.translation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = NoorDarkPine,
                            lineHeight = 21.sp
                        )
                    )

                    // 2. Secondary Layer: Phonetic Transliteration
                    if (dailyDua.transliteration.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dailyDua.transliteration,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 12.5.sp,
                                color = NoorSageSlate,
                                lineHeight = 18.sp
                            )
                        )
                    }

                    // 3. Tertiary Layer: Arabic Script (Regular weight)
                    if (showArabicSecondary && dailyDua.arabicText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dailyDua.arabicText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontSize = 17.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = NoorDarkPine.copy(alpha = 0.85f),
                                textAlign = TextAlign.End,
                                lineHeight = 28.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// 6. HEART & SOUL: MOOD & AI WISDOM REFLECTION
// ============================================================

@Composable
fun DailyMoodWisdomSection(
    viewModel: MainViewModel,
    selectedMood: String,
    isIslamic: Boolean,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val moods = listOf(
        "Anxious" to stringResource(R.string.mood_anxious),
        "Grateful" to stringResource(R.string.mood_grateful),
        "Tired" to stringResource(R.string.mood_tired),
        "Hopeful" to stringResource(R.string.mood_hopeful),
        "Lost" to stringResource(R.string.mood_lost),
        "Peaceful" to stringResource(R.string.mood_peaceful)
    )

    NoorSectionContainer(
        icon = Icons.Default.Psychology,
        title = stringResource(R.string.home_spiritual_mood),
        subtitle = stringResource(R.string.home_how_is_heart_today),
        modifier = modifier
    ) {
        // Horizontal Mood Pills with subtle inner border
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            items(moods) { (moodKey, moodLabel) ->
                val isSelected = selectedMood.equals(moodKey, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) NoorTealStart else NoorSurfaceSoft,
                    border = BorderStroke(1.dp, if (isSelected) NoorTealStart else NoorCardBorder),
                    modifier = Modifier.clickable {
                        viewModel.selectMood(moodKey)
                    }
                ) {
                    Text(
                        text = moodLabel,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else NoorDarkPine
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Wisdom Response Box with subtle inner border
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = NoorSurfaceSoft,
            border = BorderStroke(1.dp, NoorCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
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
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NoorGoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.home_daily_reflection_wisdom),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = NoorGoldAccent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val wisdom = viewModel.getCurrentMoodWisdom()
                val isArabicPrimary = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية" || appLanguage.startsWith("ar", ignoreCase = true)

                if (wisdom.arabicText.isNotBlank()) {
                    Text(
                        text = wisdom.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = NoorDarkPine,
                            textAlign = TextAlign.End,
                            lineHeight = 30.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = if (isArabicPrimary) {
                        "${wisdom.explanationAr.ifBlank { wisdom.explanation }} — ${wisdom.sourceAr.ifBlank { wisdom.source }}"
                    } else {
                        "${wisdom.translation} — ${wisdom.source}"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.5.sp,
                        color = NoorDarkPine,
                        lineHeight = 22.sp
                    )
                )
            }
        }
    }
}

// ============================================================
// 7. PREMIUM UPGRADE CARD (Under Spiritual Mood Section)
// ============================================================

@Composable
fun PremiumUpgradeCard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                viewModel.showToast(context.getString(R.string.home_premium_toast))
            },
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFFDF5),
                            Color(0xFFFFF5DF),
                            Color(0xFFFFEFA8)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Clean Light & Gold Styling: Soft Golden/Mint container with Gold Icon
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFAF0D7))
                                .border(1.2.dp, Color(0xFFE2C983), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Premium Icon",
                                tint = NoorGoldAccent,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.home_unlock_pro),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = NoorDarkPine
                                    )
                                )
                                // Clean Light Gold PRO Badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFAF0D7),
                                    border = BorderStroke(0.8.dp, Color(0xFFE2C983))
                                ) {
                                    Text(
                                        text = stringResource(R.string.home_pro_badge),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NoorGoldAccent
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = stringResource(R.string.home_pro_desc),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.5.sp,
                                    color = NoorSageSlate
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Feature Highlights Chips with subtle inner borders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFAF0D7),
                        border = BorderStroke(1.dp, Color(0xFFE8D2A0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = "✨", fontSize = 12.sp)
                            Text(
                                text = stringResource(R.string.home_unlimited_ai),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NoorDarkPine
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFAF0D7),
                        border = BorderStroke(1.dp, Color(0xFFE8D2A0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = "🎧", fontSize = 12.sp)
                            Text(
                                text = stringResource(R.string.home_offline_qaris),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NoorDarkPine
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFAF0D7),
                        border = BorderStroke(1.dp, Color(0xFFE8D2A0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = "🕌", fontSize = 12.sp)
                            Text(
                                text = stringResource(R.string.home_ad_free),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NoorDarkPine
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Inverted CTA Button: Vibrant Golden Gradient Container with subtle border
                Surface(
                    onClick = {
                        viewModel.showToast(context.getString(R.string.home_premium_toast))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFFE5B958)),
                    shadowElevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFFD54F),
                                        Color(0xFFE5B54F),
                                        Color(0xFFD4A340)
                                    )
                                )
                            )
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.home_unlock_upgrade_cta),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NoorDarkPine
                                )
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = NoorDarkPine,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



// ============================================================
// 8. QURAN AUDIO RECITERS SHOWCASE
// ============================================================

@Composable
fun QuranRecitersShowcase(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val reciters = QuranData.reciters
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية" || appLanguage.startsWith("ar", ignoreCase = true)

    NoorSectionContainer(
        icon = Icons.Default.Headphones,
        title = stringResource(R.string.home_quran_recitations),
        subtitle = stringResource(R.string.home_reciters_sub),
        actionLabel = stringResource(R.string.home_audio_stream),
        onActionClick = { viewModel.navigateTo(NoorDestination.QURAN_RECITERS) },
        modifier = modifier
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            items(reciters) { reciter ->
                val reciterName = if (isArabic) reciter.nameAr.ifBlank { reciter.name } else reciter.name
                val reciterStyle = if (isArabic) reciter.styleAr.ifBlank { reciter.style } else reciter.style

                Surface(
                    modifier = Modifier
                        .width(168.dp)
                        .clickable {
                            viewModel.selectReciter(reciter)
                            viewModel.playSurahAudio(QuranData.surahs.first(), openPlayer = true)
                        },
                    shape = RoundedCornerShape(18.dp),
                    color = NoorSurfaceSoft,
                    border = BorderStroke(1.dp, NoorCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(NoorAccentGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = reciterName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NoorDarkPine,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = reciterStyle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = NoorSageSlate
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}



data class ShareableCardData(
    val quote: String,
    val reference: String,
    val themeGradient: List<Color>
)

// ============================================================
// SHAREABLE IMAGES CAROUSEL (Bottom of Page)
// ============================================================

@Composable
fun ShareableImagesCarouselSection(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية" || appLanguage.startsWith("ar", ignoreCase = true)

    val shareableCards = if (isArabic) {
        listOf(
            ShareableCardData(
                quote = "«إن مع العسر يسراً»",
                reference = "سورة الشرح 94:6",
                themeGradient = listOf(Color(0xFF0C3829), Color(0xFF13503B))
            ),
            ShareableCardData(
                quote = "«وهو معكم أينما كنتم»",
                reference = "سورة الحديد 57:4",
                themeGradient = listOf(Color(0xFF2C1810), Color(0xFF5A3522))
            ),
            ShareableCardData(
                quote = "«سبحان الله وبحمده، سبحان الله العظيم»",
                reference = "صحيح البخاري",
                themeGradient = listOf(Color(0xFF1A365D), Color(0xFF2A4365))
            ),
            ShareableCardData(
                quote = "«بارك الله لك في بيتك وأهلك بالسلامة والبركة»",
                reference = "تذكير يومي عائلي",
                themeGradient = listOf(Color(0xFF4A3500), Color(0xFF745100))
            )
        )
    } else {
        listOf(
            ShareableCardData(
                quote = "“Verily, with hardship comes ease.”",
                reference = "Surah Ash-Sharh 94:6",
                themeGradient = listOf(Color(0xFF0C3829), Color(0xFF13503B))
            ),
            ShareableCardData(
                quote = "“And He is with you wherever you may be.”",
                reference = "Surah Al-Hadid 57:4",
                themeGradient = listOf(Color(0xFF2C1810), Color(0xFF5A3522))
            ),
            ShareableCardData(
                quote = "“SubhanAllah wa bihamdihi, SubhanAllah al-Azim.”",
                reference = "Sahih Al-Bukhari",
                themeGradient = listOf(Color(0xFF1A365D), Color(0xFF2A4365))
            ),
            ShareableCardData(
                quote = "“May Allah bless your home with peace & barakah.”",
                reference = "Family Daily Reminder",
                themeGradient = listOf(Color(0xFF4A3500), Color(0xFF745100))
            )
        )
    }

    NoorSectionContainer(
        icon = Icons.Default.Share,
        title = stringResource(R.string.home_shareable_cards_title),
        subtitle = stringResource(R.string.home_shareable_cards_sub),
        actionLabel = stringResource(R.string.home_more),
        onActionClick = { viewModel.showToast(context.getString(R.string.home_exploring_shareable_toast)) },
        modifier = modifier
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(shareableCards) { card ->
                Surface(
                    modifier = Modifier
                        .width(220.dp)
                        .height(150.dp)
                        .clickable {
                            viewModel.showToast(context.getString(R.string.home_shareable_card_copied))
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFFE2C983).copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(card.themeGradient))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "✨ النور" else "✨ Al-Noor",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFDF79)
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = stringResource(R.string.action_share),
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.action_share),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = card.quote,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        lineHeight = 17.sp
                                    ),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = card.reference,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.5.sp,
                                        color = Color(0xFFFFDF79)
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
