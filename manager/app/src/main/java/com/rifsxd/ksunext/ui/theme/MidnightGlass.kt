package com.rifsxd.ksunext.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Design tokens for the "Midnight Glass, Grid 2" look (soft 3D + iOS liquid glass). */
object Midnight {
    // Background gradient (no coloured glow blobs)
    val BgTop = Color(0xFF2A2F45)
    val BgMid = Color(0xFF12151F)
    val BgBottom = Color(0xFF05060B)
    val background: Brush get() = Brush.verticalGradient(listOf(BgTop, BgMid, BgBottom))

    // Text and accent
    val Accent = Color(0xFF9DB4FF)
    val Text = Color(0xFFF2F4FF)
    val TextSecondary = Text.copy(alpha = 0.62f)
    val SwitchGreen = Color(0xFF34C759)

    // Glass surface
    val GlassTop = Color.White.copy(alpha = 0.13f)
    val GlassBottom = Color.White.copy(alpha = 0.03f)
    val GlassBorder = Color.White.copy(alpha = 0.14f)
    val glassFill: Brush get() = Brush.linearGradient(listOf(GlassTop, GlassBottom))

    // Soft 3D
    val ShadowDark = Color.Black.copy(alpha = 0.55f)
    val ShadowLight = Color.White.copy(alpha = 0.07f)
    val Highlight = Color.White.copy(alpha = 0.30f)

    // Metrics
    val CardRadius = 28.dp
    val WellSize = 48.dp
    val NavHeight = 62.dp
}
