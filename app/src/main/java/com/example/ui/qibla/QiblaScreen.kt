package com.example.ui.qibla

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.prayer.CompassSensorManager
import com.example.data.prayer.QiblaCalculator
import com.example.ui.MainViewModel
import com.example.ui.SalatTab
import com.example.ui.components.BentoCard
import com.example.ui.components.GoldBadge
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.BorderTealLight
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceWhite
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedZone by viewModel.selectedPrayerZone.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val qiblaInfo = remember(selectedZone) {
        QiblaCalculator.calculateQibla(selectedZone.latitude, selectedZone.longitude)
    }

    // Hardware sensor integration
    val compassSensorManager = remember(context) { CompassSensorManager(context) }
    var sensorHeading by remember { mutableFloatStateOf(0f) }
    var manualSimulatedHeading by remember { mutableFloatStateOf(0f) }
    var isManualMode by remember { mutableStateOf(!compassSensorManager.hasCompassSensors) }

    var isZoneMenuOpen by remember { mutableStateOf(false) }

    // Collect sensor data continuously when active
    LaunchedEffect(isManualMode) {
        if (!isManualMode && compassSensorManager.hasCompassSensors) {
            compassSensorManager.getHeadingFlow().collectLatest { rawHeading ->
                sensorHeading = rawHeading
            }
        }
    }

    val currentHeading = if (isManualMode) manualSimulatedHeading else sensorHeading

    // Shortest path angle rotation interpolation
    val animatedHeading = remember { Animatable(currentHeading) }

    LaunchedEffect(currentHeading) {
        val diff = (currentHeading - animatedHeading.value + 540f) % 360f - 180f
        animatedHeading.animateTo(
            targetValue = animatedHeading.value + diff,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        )
    }

    // Live relative angle towards Kaaba from phone's top: (QiblaBearing - Heading)
    val relativeAngle = remember(qiblaInfo.azimuthDegrees, currentHeading) {
        ((qiblaInfo.azimuthDegrees - currentHeading + 540f) % 360f) - 180f
    }

    val isAligned = abs(relativeAngle) <= 4.0f

    // Trigger haptic vibration on alignment entry
    var wasAligned by remember { mutableStateOf(false) }
    LaunchedEffect(isAligned) {
        if (isAligned && !wasAligned) {
            viewModel.triggerHaptic()
        }
        wasAligned = isAligned
    }

    // Pulsing aura when aligned
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val dialBorderColor by animateColorAsState(
        targetValue = if (isAligned) SuccessGreen else BorderTealGray,
        animationSpec = tween(400),
        label = "borderColor"
    )

    Scaffold(
        topBar = {
            NoorTopBar(
                title = if (isArabic) "اتجاه القبلة" else "Live Qibla Compass",
                eyebrow = if (isArabic) "الكعبة المشرفة" else "KAABA 🕋",
                subtitle = if (isManualMode) {
                    if (isArabic) "وضع التدوير اليدوي" else "Interactive Compass Mode"
                } else {
                    if (isArabic) "مستشعر الجهاز المغناطيسي نشط" else "Live Device Magnetometer"
                },
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                actions = {
                    NoorGlassIconButton(
                        onClick = {
                            isManualMode = !isManualMode
                            viewModel.triggerHaptic()
                        },
                        icon = if (isManualMode) Icons.Default.Explore else Icons.Default.Sensors,
                        contentDescription = "Toggle Sensor Mode",
                        isActive = isAligned
                    )
                }
            )
        },
        containerColor = CanvasMint,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // Location & Target Bearing Banner
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SurfaceWhite,
                borderColor = if (isAligned) SuccessGreen.copy(alpha = 0.5f) else BorderTealGray
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isZoneMenuOpen = true }
                                    .padding(vertical = 2.dp, horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = DeepVibrantTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${selectedZone.name}, ${selectedZone.country}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "▾",
                                    color = DeepVibrantTeal,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            DropdownMenu(
                                expanded = isZoneMenuOpen,
                                onDismissRequest = { isZoneMenuOpen = false }
                            ) {
                                viewModel.prayerZones.forEach { zone ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${zone.name}, ${zone.country} (${zone.zoneLabel})",
                                                fontWeight = if (zone.id == selectedZone.id) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectPrayerZone(zone)
                                            isZoneMenuOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        val directionLabel = if (isArabic) qiblaInfo.arabicDirection else qiblaInfo.cardinalDirection
                        Text(
                            text = if (isArabic) {
                                "زاوية القبلة: ${String.format(Locale.getDefault(), "%.1f", qiblaInfo.azimuthDegrees)}° ($directionLabel)"
                            } else {
                                "Kaaba Bearing: ${String.format(Locale.US, "%.1f", qiblaInfo.azimuthDegrees)}° ($directionLabel)"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = SlateTealMuted)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = SoftTealTint,
                        border = BorderStroke(1.dp, BorderTealLight)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%,.0f km", qiblaInfo.distanceKm),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepVibrantTeal,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Real-Time Guidance Status Alert Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (isAligned) SuccessGreen.copy(alpha = 0.12f) else SoftTealTint,
                border = BorderStroke(1.dp, if (isAligned) SuccessGreen else BorderTealLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isAligned) SuccessGreen else DeepVibrantTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.Navigation,
                            contentDescription = "Status",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAligned) {
                                if (isArabic) "أنت مواجه للقبلة المشرفة تماماً!" else "You are perfectly facing the Qibla!"
                            } else if (relativeAngle > 0) {
                                if (isArabic) "استدر يميناً بمقدار ${relativeAngle.roundToInt()}°" else "Turn right ${relativeAngle.roundToInt()}° to align"
                            } else {
                                if (isArabic) "استدر يساراً بمقدار ${abs(relativeAngle).roundToInt()}°" else "Turn left ${abs(relativeAngle).roundToInt()}° to align"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isAligned) SuccessGreen else DarkPine
                            )
                        )
                        Text(
                            text = if (isArabic) {
                                "اتجاه الهاتف: ${(currentHeading % 360f + 360f).roundToInt() % 360}° • زاوية الكعبة: ${qiblaInfo.azimuthDegrees.roundToInt()}°"
                            } else {
                                "Phone Heading: ${(currentHeading % 360f + 360f).roundToInt() % 360}° • Kaaba: ${qiblaInfo.azimuthDegrees.roundToInt()}°"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateTealMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Main Rotating Dynamic Compass
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .scale(if (isAligned) pulseScale else 1.0f)
                    .shadow(
                        elevation = if (isAligned) 14.dp else 8.dp,
                        shape = CircleShape,
                        ambientColor = if (isAligned) SuccessGreen.copy(alpha = 0.35f) else DeepVibrantTeal.copy(alpha = 0.15f),
                        spotColor = if (isAligned) SuccessGreen.copy(alpha = 0.45f) else DeepVibrantTeal.copy(alpha = 0.25f)
                    )
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(3.5.dp, dialBorderColor, CircleShape)
                    .pointerInput(Unit) {
                        // Allow dragging dial to rotate interactively
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            isManualMode = true
                            manualSimulatedHeading = (manualSimulatedHeading - dragAmount.x * 0.5f + 360f) % 360f
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Fixed top index pointer (12 o'clock phone orientation indicator)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isAligned) SuccessGreen else DeepVibrantTeal)
                    )
                }

                // Rotating Compass Dial (Rotates negatively with device heading: -heading)
                val dialRotation = -animatedHeading.value
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(dialRotation),
                    contentAlignment = Alignment.Center
                ) {
                    // Dial markings (Ticks and North/South/East/West labels)
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        drawCompassDial(isAligned = isAligned)
                    }

                    // Kaaba Needle Marker located at Kaaba azimuth on the dial
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(qiblaInfo.azimuthDegrees),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 22.dp)
                        ) {
                            // Kaaba icon badge
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DarkPine)
                                    .border(2.dp, if (isAligned) SuccessGreen else MetallicGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = "Kaaba Marker",
                                    tint = if (isAligned) SuccessGreen else MetallicGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Glowing pointing vector towards center
                            Box(
                                modifier = Modifier
                                    .width(5.dp)
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                if (isAligned) SuccessGreen else MetallicGold,
                                                DeepVibrantTeal,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }

                // Central Hub (Shows current Kaaba angle & Alignment glow)
                Surface(
                    shape = CircleShape,
                    color = if (isAligned) SuccessGreen else DarkPine,
                    border = BorderStroke(2.5.dp, Color.White),
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(4.dp, CircleShape)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${qiblaInfo.azimuthDegrees.roundToInt()}°",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = if (isAligned) "QIBLA" else "BEARING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isAligned) Color.White else MetallicGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            }

            // Interactive Controls / Testing Buttons (Rotate phone simulation)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceWhite,
                border = BorderStroke(1.dp, BorderTealLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "تجربة وتدوير البوصلة (Simulation)" else "Interactive Compass Rotation",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPine
                            )
                        )

                        Text(
                            text = if (isManualMode) "Manual Mode" else "Sensor Mode",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isManualMode) DeepVibrantTeal else SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isManualMode = true
                                manualSimulatedHeading = (manualSimulatedHeading - 45f + 360f) % 360f
                                viewModel.triggerHaptic()
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("-45°", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                isManualMode = true
                                manualSimulatedHeading = (manualSimulatedHeading - 15f + 360f) % 360f
                                viewModel.triggerHaptic()
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("-15°", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                isManualMode = true
                                manualSimulatedHeading = qiblaInfo.azimuthDegrees
                                viewModel.triggerHaptic()
                            },
                            modifier = Modifier.weight(1.3f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAligned) SuccessGreen else DeepVibrantTeal
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isArabic) "وجه للقبلة" else "Align 🕋",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                isManualMode = true
                                manualSimulatedHeading = (manualSimulatedHeading + 15f) % 360f
                                viewModel.triggerHaptic()
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+15°", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                isManualMode = true
                                manualSimulatedHeading = (manualSimulatedHeading + 45f) % 360f
                                viewModel.triggerHaptic()
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+45°", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom Actions: Prayer Timings & Calibration info
            Button(
                onClick = { viewModel.navigateToSalat(SalatTab.TIMES) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepVibrantTeal),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = "Salat",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "عرض مواقيت الصلاة والأذان" else "View Salat & Prayer Timings",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

private fun DrawScope.drawCompassDial(isAligned: Boolean) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = size.minDimension / 2f

    // Draw ticks around 360 degrees (every 5 degrees)
    for (i in 0 until 360 step 5) {
        val isCardinal = i % 90 == 0
        val isMajor = i % 30 == 0

        val tickLen = when {
            isCardinal -> 16.dp.toPx()
            isMajor -> 10.dp.toPx()
            else -> 5.dp.toPx()
        }

        val angleRad = Math.toRadians(i.toDouble() - 90.0)
        val startX = (center.x + (radius - tickLen) * kotlin.math.cos(angleRad)).toFloat()
        val startY = (center.y + (radius - tickLen) * kotlin.math.sin(angleRad)).toFloat()
        val endX = (center.x + radius * kotlin.math.cos(angleRad)).toFloat()
        val endY = (center.y + radius * kotlin.math.sin(angleRad)).toFloat()

        val tickColor = when {
            i == 0 -> Color(0xFFE53935) // North is Red
            isCardinal -> DeepVibrantTeal
            isMajor -> SlateTealMuted
            else -> SlateTealMuted.copy(alpha = 0.35f)
        }

        val strokeW = when {
            isCardinal -> 3.dp.toPx()
            isMajor -> 1.8.dp.toPx()
            else -> 1.dp.toPx()
        }

        drawLine(
            color = tickColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeW
        )
    }
}
