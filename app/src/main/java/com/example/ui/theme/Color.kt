package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Canvas & Elevated Surfaces
val CanvasMint = Color(0xFFF2F6F4)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceElevated = Color(0xFFF9FBFA)
val BorderTealGray = Color(0xFFD7ECE7)
val BorderTealLight = Color(0xFFE6F3F0)

// Primary Gradient & Teal Spectrum (099382 -> 13A795)
val TealAccentStart = Color(0xFF099382)
val TealAccentEnd = Color(0xFF13A795)
val DeepVibrantTeal = Color(0xFF099382)
val LuminousCyan = Color(0xFF13A795)
val DarkPine = Color(0xFF10261F)
val SlateTealMuted = Color(0xFF4A6B5F)
val SoftTealTint = Color(0xFFE8F6F4)

// Luxury Metallic Gold Accent
val MetallicGold = Color(0xFFC89B38)
val GoldHighlight = Color(0xFFFBF4E4)
val GoldGradientEnd = Color(0xFFDFB75A)
val GoldBadgeBg = Color(0xFFFFF9EE)

// Status & Semantic Colors
val SuccessGreen = Color(0xFF1B8755)
val SuccessGreenLight = Color(0xFFE6F7ED)
val ErrorRed = Color(0xFFD32F2F)
val ErrorRedLight = Color(0xFFFFEBEE)
val WarningAmber = Color(0xFFE67E22)

// Card Gradients & Brushes
val PrimaryTealGradient = Brush.linearGradient(
    colors = listOf(TealAccentStart, TealAccentEnd)
)

val GoldAccentGradient = Brush.linearGradient(
    colors = listOf(MetallicGold, GoldGradientEnd)
)

val CardDarkGradient = Brush.linearGradient(
    colors = listOf(DarkPine, Color(0xFF16382D))
)

val FrostedGlassGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.92f),
        Color(0xFFFFFFFF).copy(alpha = 0.75f)
    )
)

