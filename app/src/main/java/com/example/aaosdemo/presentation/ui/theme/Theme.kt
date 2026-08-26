// ============================================================
// presentation/ui/theme/Theme.kt
// ============================================================
// PRESENTATION LAYER — Compose Theme.
//
// Defines the visual design system for the IVI app:
//   • Color palette (dark automotive theme)
//   • Typography (readable at IVI distances)
//   • Shapes
//
// WHY a dark theme for automotive?
//   • Reduces eye strain at night
//   • OLED screens use less power with dark backgrounds
//   • Industry standard (Volvo, Mercedes, BMW all use dark IVI themes)
// ============================================================

package com.example.aaosdemo.presentation.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── COLORS ─────────────────────────────────────────────────────────────────
object AAOSColors {
    val Background      = Color(0xFF0A0E17)   // Deep navy — main background
    val Surface         = Color(0xFF111827)   // Slightly lighter — cards
    val SurfaceVariant  = Color(0xFF1A2234)   // Even lighter — elevated cards

    val Accent          = Color(0xFF00D4FF)   // Cyan — primary interactive color
    val AccentGreen     = Color(0xFF4ADE80)   // Green — success / battery / playing
    val AccentAmber     = Color(0xFFF59E0B)   // Amber — warnings / temp
    val AccentPurple    = Color(0xFFA78BFA)   // Purple — secondary accent

    val TextPrimary     = Color(0xFFE2E8F0)   // Near-white
    val TextSecondary   = Color(0xFF94A3B8)   // Muted blue-grey
    val TextTertiary    = Color(0xFF64748B)   // Dimmed

    val Border          = Color(0xFF1E2A3A)   // Subtle borders
    val BorderAccent    = Color(0x4D00D4FF)   // Cyan with 30% alpha

    val Error           = Color(0xFFEF4444)
    val Success         = Color(0xFF22C55E)
}

// ── DARK COLOR SCHEME ──────────────────────────────────────────────────────
private val AAOSDarkColorScheme = darkColorScheme(
    primary          = AAOSColors.Accent,
    onPrimary        = Color(0xFF000000),
    primaryContainer = Color(0xFF003544),
    secondary        = AAOSColors.AccentPurple,
    background       = AAOSColors.Background,
    surface          = AAOSColors.Surface,
    onBackground     = AAOSColors.TextPrimary,
    onSurface        = AAOSColors.TextPrimary,
    error            = AAOSColors.Error
)

// ── TYPOGRAPHY ─────────────────────────────────────────────────────────────
// IVI displays are typically 10–15 inches, viewed from ~60cm.
// Fonts must be larger and bolder than typical phone apps.
val AAOSTypography = Typography(
    // Large display — speed, temperature numbers
    displayLarge = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        color = AAOSColors.TextPrimary
    ),
    // Section headings
    headlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = AAOSColors.TextPrimary
    ),
    // Card titles
    titleMedium = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = AAOSColors.TextPrimary
    ),
    // Body text
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = AAOSColors.TextSecondary
    ),
    // Labels, metadata
    labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        color = AAOSColors.TextTertiary
    )
)

// ── THEME COMPOSABLE ───────────────────────────────────────────────────────
@Composable
fun AAOSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AAOSDarkColorScheme,
        typography = AAOSTypography,
        content = content
    )
}
