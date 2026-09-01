package com.example.ui.qibla

import android.hardware.SensorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.data.prayer.CompassReading
import com.example.data.prayer.CompassSensorManager
import com.example.data.prayer.QiblaCalculator
import com.example.ui.MainViewModel
import com.example.ui.SalatTab
import com.example.ui.components.BentoCard
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

    // Hardware sensor integration with declination correction & accuracy monitoring
    val compassSensorManager = remember(context) { CompassSensorManager(context) }
    var currentReading by remember { mutableStateOf<CompassReading?>(null) }
    var sensorHeading by remember { mutableFloatStateOf(0f) }
    var sensorAccuracy by remember { mutableIntStateOf(SensorManager.SENSOR_STATUS_ACCURACY_HIGH) }
    var isLowAccuracy by remember { mutableStateOf(false) }

    var manualSimulatedHeading by remember { mutableFloatStateOf(0f) }
    var isManualMode by remember { mutableStateOf(!compassSensorManager.hasCompassSensors) }

    var isZoneMenuOpen by remember { mutableStateOf(false) }

    // Collect sensor data continuously with True North declination correction
    LaunchedEffect(isManualMode, selectedZone) {
        if (!isManualMode && compassSensorManager.hasCompassSensors) {
            compassSensorManager.getCompassFlow(selectedZone.latitude, selectedZone.longitude)
                .collectLatest { reading ->
                    currentReading = reading
                    sensorHeading = reading.trueHeading
                    sensorAccuracy = reading.accuracy
                    isLowAccuracy = reading.isLowAccuracy
                }
        }
    }

    // Always use True North heading (corrected for geomagnetic declination)
    val currentHeading = if (isManualMode) manualSimulatedHeading else sensorHeading
    val activeDeclination = currentReading?.declination ?: qiblaInfo.magneticDeclination

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
        QiblaCalculator.computeRelativeAngle(qiblaInfo.azimuthDegrees, currentHeading)
    }

    val isAligned = abs(relativeAngle) <= 4.0f && !isLowAccuracy

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
        targetValue = when {
            isLowAccuracy -> Color(0xFFE57373)
            isAligned -> SuccessGreen
            else -> BorderTealGray
        },
        animationSpec = tween(400),
        label = "borderColor"
    )

    Scaffold(
        topBar = {
            val declinationSign = if (activeDeclination >= 0) "+" else ""
            val declinationFormatted = "$declinationSign${String.format(Locale.US, "%.1f", activeDeclination)}°"
            NoorTopBar(
                title = if (isArabic) "اتجاه القبلة المشرفة" else "Qibla Direction",
                eyebrow = if (isArabic) "الكعبة المشرفة" else "MAKKAH AL-MUKARRAMAH",
                subtitle = if (isArabic) {
                    "الشمال الحقيقي • تصحيح الميل ($declinationFormatted)"
                } else {
                    "True North • Declination ($declinationFormatted)"
                },
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                actions = {
                    if (compassSensorManager.hasCompassSensors) {
                        NoorGlassIconButton(
                            onClick = {
                                isManualMode = !isManualMode
                                viewModel.triggerHaptic()
                            },
                            icon = if (isManualMode) Icons.Default.Explore else Icons.Default.Sensors,
                            contentDescription = "Sensor Mode",
                            isActive = isAligned
                        )
                    }
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

            // Location & Target Bearing Banner with Declination Offset
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
                        val declinationText = "${if (activeDeclination >= 0) "+" else ""}${String.format(Locale.US, "%.1f", activeDeclination)}°"
                        Text(
                            text = if (isArabic) {
                                "زاوية القبلة: ${String.format(Locale.getDefault(), "%.1f", qiblaInfo.azimuthDegrees)}° ($directionLabel) • ميل: $declinationText"
                            } else {
                                "Kaaba Bearing: ${String.format(Locale.US, "%.1f", qiblaInfo.azimuthDegrees)}° ($directionLabel) • Decl: $declinationText"
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
                color = when {
                    isLowAccuracy -> Color(0xFFFFFBEB)
                    isAligned -> SuccessGreen.copy(alpha = 0.12f)
                    else -> SoftTealTint
                },
                border = BorderStroke(
                    1.dp,
                    when {
                        isLowAccuracy -> Color(0xFFFDE68A)
                        isAligned -> SuccessGreen
                        else -> BorderTealLight
                    }
                )
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
                            .background(
                                when {
                                    isLowAccuracy -> Color(0xFFD97706)
                                    isAligned -> SuccessGreen
                                    else -> DeepVibrantTeal
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isLowAccuracy -> Icons.Default.CompassCalibration
                                isAligned -> Icons.Default.CheckCircle
                                else -> Icons.Default.Navigation
                            },
                            contentDescription = "Status",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when {
                                isLowAccuracy -> {
                                    if (isArabic) "دقة البوصلة منخفضة — يرجى المعايرة" else "Compass Accuracy Low — Calibrate Sensor"
                                }
                                isAligned -> {
                                    if (isArabic) "أنت مواجه للقبلة المشرفة تماماً!" else "You are perfectly facing the Qibla!"
                                }
                                relativeAngle > 0 -> {
                                    if (isArabic) "استدر يميناً بمقدار ${relativeAngle.roundToInt()}°" else "Turn right ${relativeAngle.roundToInt()}° to align"
                                }
                                else -> {
                                    if (isArabic) "استدر يساراً بمقدار ${abs(relativeAngle).roundToInt()}°" else "Turn left ${abs(relativeAngle).roundToInt()}° to align"
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isLowAccuracy -> Color(0xFF92400E)
                                    isAligned -> SuccessGreen
                                    else -> DarkPine
                                }
                            )
                        )
                        Text(
                            text = if (isArabic) {
                                "الشمال الحقيقي: ${(currentHeading % 360f + 360f).roundToInt() % 360}° • زاوية الكعبة: ${qiblaInfo.azimuthDegrees.roundToInt()}°"
                            } else {
                                "True Heading: ${(currentHeading % 360f + 360f).roundToInt() % 360}° (True North) • Kaaba: ${qiblaInfo.azimuthDegrees.roundToInt()}°"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateTealMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Low-Accuracy Sensor Warning Alert (Figure-8 recalibration guidance)
            AnimatedVisibility(
                visible = isLowAccuracy,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFF7ED),
                    border = BorderStroke(1.2.dp, Color(0xFFFDBA74))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA580C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompassCalibration,
                                contentDescription = "Calibrate",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "معايرة البوصلة مطلوبة" else "Recalibration Required",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9A3412)
                                )
                            )
                            Text(
                                text = if (isArabic) {
                                    "حرك هاتفك في الهواء بحركة شكل الرقم (8) لتحسين دقة مستشعر البوصلة وإزالة التشويش."
                                } else {
                                    "Move your phone in a smooth figure-8 motion in the air to calibrate the magnetic sensor."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF7C2D12),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            // Main Rotating Dynamic Compass Dial
            val compassAlpha = if (isLowAccuracy) 0.5f else 1.0f

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
                        if (isManualMode) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                manualSimulatedHeading = (manualSimulatedHeading - dragAmount.x * 0.5f + 360f) % 360f
                            }
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
                            .background(
                                when {
                                    isLowAccuracy -> Color(0xFF9E9E9E)
                                    isAligned -> SuccessGreen
                                    else -> DeepVibrantTeal
                                }
                            )
                    )
                }

                // Rotating Compass Dial (Rotates with negative device True Heading: -heading)
                val dialRotation = -animatedHeading.value
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(compassAlpha)
                        .rotate(dialRotation),
                    contentAlignment = Alignment.Center
                ) {
                    // Dial markings (Ticks and North/South/East/West labels)
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        drawCompassDial(isAligned = isAligned, isLowAccuracy = isLowAccuracy)
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
                            // Kaaba icon badge (dimmed/grayed when accuracy is low)
                            val markerBorderColor = when {
                                isLowAccuracy -> Color(0xFFB0BEC5)
                                isAligned -> SuccessGreen
                                else -> MetallicGold
                            }
                            val markerBgColor = when {
                                isLowAccuracy -> Color(0xFF546E7A)
                                else -> DarkPine
                            }
                            val markerTint = when {
                                isLowAccuracy -> Color(0xFFCFD8DC)
                                isAligned -> SuccessGreen
                                else -> MetallicGold
                            }

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(markerBgColor)
                                    .border(2.dp, markerBorderColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = "Kaaba Marker",
                                    tint = markerTint,
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
                                                if (isLowAccuracy) Color(0xFF90A4AE) else (if (isAligned) SuccessGreen else MetallicGold),
                                                if (isLowAccuracy) Color(0xFFB0BEC5) else DeepVibrantTeal,
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
                    color = when {
                        isLowAccuracy -> Color(0xFF64748B)
                        isAligned -> SuccessGreen
                        else -> DarkPine
                    },
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
                                text = when {
                                    isLowAccuracy -> "UNCAL"
                                    isAligned -> "QIBLA"
                                    else -> "BEARING"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isAligned) Color.White else (if (isLowAccuracy) Color(0xFFE2E8F0) else MetallicGold),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }

                // Low Accuracy Warning Overlay Badge in Center-Bottom of Dial
                if (isLowAccuracy) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 26.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFDC2626),
                            border = BorderStroke(1.dp, Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isArabic) "دقة منخفضة" else "Uncalibrated",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Magnetic Interference Notice (Persistent Notice with Soft Red/Pink background)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFF1F0), // Soft rose / salmon background
                border = BorderStroke(1.dp, Color(0xFFFCA5A5).copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Interference Notice",
                            tint = Color(0xFFB91C1C),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = if (isArabic) "تنبيه التداخل المغناطيسي" else "Compass Accuracy & Interference Notice",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7F1D1D)
                            )
                        )
                        Text(
                            text = if (isArabic) {
                                "للحصول على نتائج دقيقة، ابتعد عن الأجسام المعدنية (هياكل السيارات، الأثاث الفولاذي)، والأغطية وحوامل الهواتف المغناطيسية، والسماعات ومكبرات الصوت، والأجهزة الإلكترونية وكابلات الكهرباء، ثم عاير البوصلة بتحريك هاتفك في الهواء على شكل رقم 8."
                            } else {
                                "For accurate results, move away from metal objects (car bodies, steel furniture, rebar), magnetic phone cases or mounts, speakers/headphones or devices with magnets, and nearby electronics or power cables, then calibrate by moving your phone in a figure-8 motion."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF881337),
                                fontSize = 11.8.sp,
                                lineHeight = 16.5.sp
                            )
                        )
                    }
                }
            }

            // Fallback Manual Calibration Simulator (Only for testing or when hardware sensor is missing)
            if (isManualMode || !compassSensorManager.hasCompassSensors) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                                text = if (isArabic) "محاكي الاتجاه اليدوي" else "Manual Orientation Simulator",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPine
                                )
                            )

                            Text(
                                text = "${manualSimulatedHeading.roundToInt()}°",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DeepVibrantTeal,
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
            }

            // Bottom Actions: Prayer Timings Navigation
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

private fun DrawScope.drawCompassDial(isAligned: Boolean, isLowAccuracy: Boolean = false) {
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
            isLowAccuracy -> Color(0xFF9E9E9E).copy(alpha = if (isCardinal) 0.8f else 0.4f)
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

