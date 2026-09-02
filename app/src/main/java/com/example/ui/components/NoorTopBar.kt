package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val NoorTopBarGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF133E32), // Softened rich forest green
        Color(0xFF1A5544), // Serene pine & royal green
        Color(0xFF0F7D5C)  // Luminous emerald sheen
    )
)

val NoorTopBarAntiqueGold = Color(0xFFC9A34E)
val NoorTopBarEyebrowGold = Color(0xFFE5C378)
val NoorTopBarHairlineBorder = Color.White.copy(alpha = 0.12f)
val NoorGlassBg = Color.White.copy(alpha = 0.10f)
val NoorGlassBorder = Color.White.copy(alpha = 0.15f)
val NoorGlassIconTint = Color.White.copy(alpha = 0.92f)

/**
 * Standard glass circular container for top bar action/navigation icons.
 * Subtle rounded background (white at ~8% alpha, hairline border at ~12% alpha)
 * with small crisp icon glyph (~20dp).
 */
@Composable
fun NoorGlassIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    activeTint: Color = NoorTopBarAntiqueGold,
    defaultTint: Color = NoorGlassIconTint,
    badgeCount: Int? = null
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(NoorGlassBg)
            .border(BorderStroke(1.dp, NoorGlassBorder), CircleShape)
            .clickable(
                onClick = onClick,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) activeTint else defaultTint,
            modifier = Modifier.size(20.dp)
        )
        if (badgeCount != null && badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 3.dp, end = 3.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NoorTopBarAntiqueGold)
            )
        }
    }
}

/**
 * NoorTopBar - Reusable premium top bar for all primary application destinations.
 *
 * Background: subtle linear gradient from #0A1915 -> #0E3429 -> #087F5B.
 * Border: 1dp hairline (Color.White @ 10% alpha) at bottom edge.
 * Title typography: Bold, ~20sp, white, left-aligned, with optional eyebrow label in muted gold/cream.
 * Height & Spacing: 64dp content height (plus status bar insets), 20dp horizontal padding.
 */
@Composable
fun NoorTopBar(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    backIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    backContentDescription: String = "Back",
    isLargeTitle: Boolean = false,
    titleContent: (@Composable () -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(NoorTopBarGradient)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = NoorTopBarHairlineBorder,
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            }
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = if (isLargeTitle) 16.dp else 14.dp,
                    bottom = if (isLargeTitle) 24.dp else 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Area: Back Button (if present) + Title/Eyebrow/Subtitle
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (onBackClick != null) {
                    NoorGlassIconButton(
                        onClick = onBackClick,
                        icon = backIcon,
                        contentDescription = backContentDescription
                    )
                }

                if (titleContent != null) {
                    titleContent()
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (!eyebrow.isNullOrBlank()) {
                            Text(
                                text = eyebrow.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.4.sp,
                                    color = NoorTopBarEyebrowGold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isLargeTitle) 22.sp else 20.sp,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.82f)
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Right Area: 0-2 (or more) trailing action glass icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions()
            }
        }
    }
}

/**
 * NoorGradientButton - Reusable button styled with the signature NoorTopBar emerald gradient.
 */
@Composable
fun NoorGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (enabled) NoorTopBarGradient else Brush.linearGradient(
                    listOf(Color(0xFF8BA39B), Color(0xFF8BA39B))
                )
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White)
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

