// ============================================================
// presentation/ui/screens/Components.kt
// ============================================================
// PRESENTATION LAYER — Reusable Compose Components.
//
// Shared UI building blocks used across multiple screens.
// Building a component library avoids duplication and enforces
// design consistency — critical in a large AAOS codebase.
//
// COMPOSE CONCEPTS DEMONSTRATED:
//   • @Composable functions
//   • Modifier chaining
//   • Slot APIs (content: @Composable () -> Unit)
//   • Animation with animateFloatAsState
//   • Custom drawing with Canvas
// ============================================================

package com.example.aaosdemo.presentation.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.aaosdemo.presentation.ui.theme.AAOSColors
import kotlin.math.cos
import kotlin.math.sin

// ── IVI CARD ───────────────────────────────────────────────────────────────
// Base card component with IVI styling.
// Uses slot pattern: caller provides the content lambda.
@Composable
fun IVICard(
    modifier: Modifier = Modifier,
    borderColor: Color = AAOSColors.Border,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AAOSColors.Surface)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp),
        content = content
    )
}

// ── STAT CHIP ──────────────────────────────────────────────────────────────
// Small data display: label above, value below.
@Composable
fun StatChip(
    label: String,
    value: String,
    valueColor: Color = AAOSColors.Accent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AAOSColors.SurfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AAOSColors.TextTertiary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── CIRCULAR GAUGE ─────────────────────────────────────────────────────────
// Used for speed and battery level display.
// Drawn with Canvas for precise control.
@Composable
fun CircularGauge(
    value: Float,           // Current value (e.g. 68 for km/h)
    maxValue: Float,        // Max value (e.g. 200 for km/h)
    label: String,          // e.g. "km/h"
    color: Color = AAOSColors.Accent,
    size: Dp = 160.dp,
    modifier: Modifier = Modifier
) {
    // Animate the arc — smooth transition when value changes
    val animatedFraction by animateFloatAsState(
        targetValue = (value / maxValue).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = EaseInOutCubic),
        label = "gauge_animation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12f
            val radius = (size.toPx() - strokeWidth) / 2f
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val startAngle = 135f
            val sweepAngle = 270f

            // Background track (full arc, dimmed)
            drawArc(
                color = AAOSColors.Border,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            // Value arc (colored, animated)
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle * animatedFraction,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value.toInt().toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 36.sp),
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AAOSColors.TextTertiary
            )
        }
    }
}

// ── TEMPERATURE CONTROL ────────────────────────────────────────────────────
// +/- buttons with temperature display. Used in Climate screen.
@Composable
fun TemperatureControl(
    label: String,
    temperature: Float,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AAOSColors.TextTertiary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "${"%.1f".format(temperature)}°",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 42.sp),
            color = AAOSColors.AccentAmber,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Decrease button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AAOSColors.SurfaceVariant)
                    .clickable { onDecrease() },
                contentAlignment = Alignment.Center
            ) {
                Text("−", color = AAOSColors.TextPrimary, fontSize = 22.sp)
            }

            // Increase button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AAOSColors.SurfaceVariant)
                    .clickable { onIncrease() },
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = AAOSColors.TextPrimary, fontSize = 22.sp)
            }
        }
    }
}

// ── IVI TOGGLE BUTTON ──────────────────────────────────────────────────────
@Composable
fun IVIToggle(
    label: String,
    icon: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (enabled) AAOSColors.BorderAccent else AAOSColors.SurfaceVariant
    val textColor = if (enabled) AAOSColors.Accent else AAOSColors.TextSecondary

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                1.dp,
                if (enabled) AAOSColors.Accent.copy(alpha = 0.5f) else AAOSColors.Border,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = textColor)
    }
}

// ── PROGRESS BAR (track-style for media) ───────────────────────────────────
@Composable
fun MediaProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = AAOSColors.Border,
    fillColor: Color = AAOSColors.Accent
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress"
    )

    Box(
        modifier = modifier
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(fillColor)
        )
    }
}

// ── SECTION HEADER ─────────────────────────────────────────────────────────
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium
        ),
        color = AAOSColors.TextTertiary,
        modifier = modifier
    )
}
