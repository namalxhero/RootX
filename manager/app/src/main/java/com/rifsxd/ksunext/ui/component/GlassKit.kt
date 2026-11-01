package com.rifsxd.ksunext.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rifsxd.ksunext.ui.theme.Midnight

private fun Outline.toPath(): Path = when (this) {
    is Outline.Rectangle -> Path().apply { addRect(rect) }
    is Outline.Rounded -> Path().apply { addRoundRect(roundRect) }
    is Outline.Generic -> path
}

/**
 * Soft 3D shadows: a dark one falling to the bottom-right and a faint light one on the
 * top-left. They are drawn only outside the shape so translucent glass stays clean.
 */
fun Modifier.softShadow(shape: Shape): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    val path = outline.toPath()
    clipPath(path, ClipOp.Difference) {
        drawIntoCanvas { canvas ->
            fun layer(dx: Dp, dy: Dp, blur: Dp, color: Color) {
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    this.color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(blur.toPx(), dx.toPx(), dy.toPx(), color.toArgb())
                }
                canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)
            }
            layer(8.dp, 10.dp, 18.dp, Midnight.ShadowDark)
            layer((-5).dp, (-5).dp, 12.dp, Midnight.ShadowLight)
        }
    }
}

/** Thin bright edge on the top-left, fading out towards the bottom-right (inset highlight). */
fun Modifier.innerHighlight(shape: Shape, width: Dp = 2.dp): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    drawOutline(
        outline = outline,
        brush = Brush.linearGradient(
            colors = listOf(Midnight.Highlight, Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        ),
        style = Stroke(width = width.toPx())
    )
}

/** Translucent glass card: 13% -> 3% white gradient, 1dp 14% border, soft 3D shadows. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Midnight.CardRadius),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .glass(shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        content = content
    )
}

/** 48dp circular well with an inset shadow so the icon looks pressed in. */
@Composable
fun InsetIconWell(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = Midnight.Accent,
    size: Dp = Midnight.WellSize
) {
    Box(
        modifier = modifier
            .size(size)
            .drawBehind {
                // pressed-in body: dark at the top-left, lighter lip at the bottom-right
                drawCircle(
                    Brush.linearGradient(
                        listOf(Color.Black.copy(alpha = 0.55f), Color.White.copy(alpha = 0.06f)),
                        start = Offset.Zero,
                        end = Offset(this.size.width, this.size.height)
                    )
                )
                // inner shadow gathering towards the edge
                drawCircle(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.40f)),
                        center = Offset(this.size.width * 0.62f, this.size.height * 0.62f),
                        radius = this.size.width * 0.62f
                    )
                )
                drawCircle(
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.02f), Color.White.copy(alpha = 0.18f)),
                        start = Offset.Zero,
                        end = Offset(this.size.width, this.size.height)
                    ),
                    style = Stroke(1.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}

/** Floating pill container for the bottom navigation, 62dp tall. */
@Composable
fun GlassNavBar(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .height(Midnight.NavHeight)
            .glass(RoundedCornerShape(Midnight.NavHeight / 2)),
        content = content
    )
}

/**
 * iOS style switch (green track, white thumb). Pass onCheckedChange = null to show state
 * only, e.g. when the surrounding row already handles the tap.
 */
@Composable
fun IosSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val track by animateColorAsState(
        if (checked) Midnight.SwitchGreen else Color.White.copy(alpha = 0.22f), label = "track"
    )
    val thumbX by animateDpAsState(if (checked) 20.dp else 0.dp, label = "thumb")
    Box(
        modifier = modifier
            .size(width = 51.dp, height = 31.dp)
            .background(track, CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape)
            .then(
                if (onCheckedChange != null) Modifier.toggleable(
                    value = checked,
                    enabled = enabled,
                    role = Role.Switch,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onValueChange = onCheckedChange
                ) else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            Modifier
                .offset(x = 2.dp + thumbX)
                .size(27.dp)
                .shadow(4.dp, CircleShape)
                .background(Color.White, CircleShape)
        )
    }
}

/** Material switch colours matching the iOS look, for existing Switch() call sites. */
@Composable
fun iosSwitchColors(): SwitchColors = SwitchDefaults.colors(
    checkedThumbColor = Color.White,
    checkedTrackColor = Midnight.SwitchGreen,
    checkedBorderColor = Color.Transparent,
    uncheckedThumbColor = Color.White,
    uncheckedTrackColor = Color.White.copy(alpha = 0.22f),
    uncheckedBorderColor = Color.Transparent
)

@Composable
fun glassBorder(): BorderStroke = BorderStroke(1.dp, Midnight.GlassBorder)
