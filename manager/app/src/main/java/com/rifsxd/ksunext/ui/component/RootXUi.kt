package com.rifsxd.ksunext.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight as RxFontWeight
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Row
import com.rifsxd.ksunext.ui.util.RootXPin
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rifsxd.ksunext.R
import com.rifsxd.ksunext.ui.theme.Midnight
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

val RootXCyan = Color(0xFF3CC8FF)
val RootXIce = Color(0xFFBFF0FF)

/** Midnight Glass surface: translucent gradient fill, 1dp border, inset highlight and soft 3D shadows. */
fun Modifier.glass(shape: Shape = RoundedCornerShape(Midnight.CardRadius)): Modifier = this
    .softShadow(shape)
    .clip(shape)
    .background(Midnight.glassFill, shape)
    .border(1.dp, Midnight.GlassBorder, shape)
    .innerHighlight(shape)

enum class XBoomMood { Open, Happy, Scared, Sleepy }

/** The xBooM mascot. Expression is a separate layer so it can change (and blink). */
@Composable
fun XBoom(
    modifier: Modifier = Modifier,
    mood: XBoomMood = XBoomMood.Open,
    blink: Boolean = true,
    bob: Boolean = false
) {
    var blinking by remember { mutableStateOf(false) }
    LaunchedEffect(blink, mood) {
        if (blink && mood == XBoomMood.Open) {
            while (true) {
                delay(2800)
                blinking = true
                delay(130)
                blinking = false
            }
        } else {
            blinking = false
        }
    }
    val transition = rememberInfiniteTransition(label = "xboom")
    val bobValue by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    val shown = if (blinking) XBoomMood.Sleepy else mood
    val eyes = when (shown) {
        XBoomMood.Open -> R.drawable.xboom_eyes_open
        XBoomMood.Happy -> R.drawable.xboom_eyes_happy
        XBoomMood.Scared -> R.drawable.xboom_eyes_scared
        XBoomMood.Sleepy -> R.drawable.xboom_eyes_sleepy
    }
    Box(
        modifier
            .aspectRatio(120f / 150f)
            .graphicsLayer { translationY = if (bob) (bobValue * 6f - 3f) * density else 0f }
    ) {
        Image(painterResource(R.drawable.xboom_base), contentDescription = null, modifier = Modifier.fillMaxSize())
        Image(painterResource(eyes), contentDescription = null, modifier = Modifier.fillMaxSize())
    }
}

/** Loading indicator: xBooM riding a paper plane through the clouds. */
@Composable
fun XLoader(modifier: Modifier = Modifier, label: String = "Flying in...") {
    val transition = rememberInfiniteTransition(label = "plane")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2600, easing = LinearEasing)),
        label = "phase"
    )
    val bob by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobp"
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(width = 270.dp, height = 200.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val trail = Path().apply {
                    moveTo(0f, h * 0.97f)
                    quadraticBezierTo(w * 0.22f, h * 0.78f, w * 0.48f, h * 0.62f)
                }
                drawPath(
                    path = trail,
                    color = Color(0xCCFFFFFF),
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 22f), -phase * 24f)
                    )
                )
                val cloudY = listOf(0.16f, 0.46f, 0.84f)
                for (k in 0..2) {
                    val base = (phase + k / 3f) % 1f
                    val x = w - base * (w + 60.dp.toPx())
                    val y = h * cloudY[k]
                    drawOval(Color(0xE6FFFFFF), Offset(x, y), Size(54.dp.toPx(), 13.dp.toPx()))
                    drawOval(Color(0xE6FFFFFF), Offset(x + 14.dp.toPx(), y - 6.dp.toPx()), Size(30.dp.toPx(), 15.dp.toPx()))
                }
                val bobPx = 5.dp.toPx()
                withTransform({
                    translate(left = w * 0.46f, top = h * 0.62f + bob * bobPx)
                    rotate(degrees = -14f + bob * 2f, pivot = Offset.Zero)
                }) {
                    val wing = Path().apply {
                        moveTo(0f, 22.dp.toPx())
                        lineTo(112.dp.toPx(), 0f)
                        lineTo(46.dp.toPx(), 42.dp.toPx())
                        close()
                    }
                    drawPath(wing, Color(0xFFFFFFFF))
                    val fold = Path().apply {
                        moveTo(46.dp.toPx(), 42.dp.toPx())
                        lineTo(112.dp.toPx(), 0f)
                        lineTo(30.dp.toPx(), 64.dp.toPx())
                        close()
                    }
                    drawPath(fold, Color(0xFFBCD2F5))
                    val shade = Path().apply {
                        moveTo(46.dp.toPx(), 42.dp.toPx())
                        lineTo(30.dp.toPx(), 64.dp.toPx())
                        lineTo(38.dp.toPx(), 36.dp.toPx())
                        close()
                    }
                    drawPath(shade, Color(0xFF98B4E6))
                }
                val sparkle = Color(0xFFFFFFFF)
                drawCircle(sparkle.copy(alpha = 0.5f + 0.5f * kotlin.math.abs(bob)), 3.dp.toPx(), Offset(w * 0.1f, h * 0.35f))
                drawCircle(Color(0xFFFFD1F0).copy(alpha = 1f - 0.5f * kotlin.math.abs(bob)), 2.5.dp.toPx(), Offset(w * 0.9f, h * 0.7f))
            }
            XBoom(
                Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 112.dp, y = (36 + bob * 4).dp)
                    .height(66.dp),
                mood = XBoomMood.Happy,
                blink = false
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

/** Five anime pictures plus five drawn themes. The chosen one is remembered and drawn behind the whole app. */
object RootXBg {
    val drawables = listOf(R.drawable.bg_fireworks, R.drawable.bg_ocean, R.drawable.bg_snow, R.drawable.bg_field, R.drawable.bg_fuji)
    val names = listOf("Fireworks", "Ocean", "Snow", "Field", "Fuji", "Sakura", "Sunset", "System", "Monarch", "Gate", "Midnight")
    private val scrims = listOf(0.25f, 0.3f, 0.3f, 0.5f, 0.35f, 0.1f, 0.1f, 0.05f, 0.05f, 0.05f, 0f)
    private val accents = listOf(
        Color(0xFF4FD0FF), Color(0xFF4FD0FF), Color(0xFF4FD0FF), Color(0xFF4FD0FF), Color(0xFF4FD0FF),
        Color(0xFFFF8FB8), Color(0xFFFFE08A), Color(0xFF4DA3FF), Color(0xFFB58BFF), Color(0xFF3DF0E0), Midnight.Accent
    )
    val MIDNIGHT = 10
    val previews = listOf(
        listOf(Color(0xFF3A1B3F), Color(0xFFC77FA6)),
        listOf(Color(0xFF2B1055), Color(0xFFF59E6B)),
        listOf(Color(0xFF02040A), Color(0xFF4DA3FF)),
        listOf(Color(0xFF05030D), Color(0xFFB58BFF)),
        listOf(Color(0xFF020A12), Color(0xFF3DF0E0)),
        listOf(Midnight.BgTop, Midnight.BgBottom)
    )
    var index by mutableIntStateOf(0)
    private var loaded = false

    fun scrim(): Float = scrims[index.coerceIn(0, scrims.size - 1)]
    fun accent(): Color = accents[index.coerceIn(0, accents.size - 1)]

    fun load(c: android.content.Context) {
        if (!loaded) {
            index = c.getSharedPreferences("rootx_ui", android.content.Context.MODE_PRIVATE).getInt("bg", MIDNIGHT).coerceIn(0, names.size - 1)
            loaded = true
        }
    }

    fun set(c: android.content.Context, i: Int) {
        index = i
        c.getSharedPreferences("rootx_ui", android.content.Context.MODE_PRIVATE).edit().putInt("bg", i).apply()
    }
}

/** A drawn theme: gradient sky, glow and slowly drifting particles. theme is 0..4 (Sakura, Sunset, System, Monarch, Gate). */
@Composable
fun RootXThemeScene(theme: Int, modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "scene")
    val phase by t.animateFloat(0f, 1f, infiniteRepeatable(tween(26000, easing = LinearEasing)), label = "phase")
    val seeds = remember { List(46) { i -> Triple(((i * 97) % 100) / 100f, 1 + (i % 2), ((i * 31) % 100) / 100f) } }
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val d = density
        val pColor: Color
        val falling: Boolean
        when (theme) {
            0 -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF3A1B3F), Color(0xFF7B3F73), Color(0xFFC77FA6))))
                drawCircle(Brush.radialGradient(listOf(Color(0x55FFB6D1), Color.Transparent), Offset(w * 0.5f, h * 0.15f), w * 0.9f), w * 0.9f, Offset(w * 0.5f, h * 0.15f))
                pColor = Color(0xFFFFB6D1); falling = true
            }
            1 -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF2B1055), Color(0xFF6B2C8E), Color(0xFFD4507A), Color(0xFFF59E6B))))
                drawCircle(Color(0x66FFD27A), 70f * d, Offset(w * 0.72f, h * 0.4f))
                val hill = Path().apply {
                    moveTo(0f, h); lineTo(0f, h * 0.84f); lineTo(w * 0.22f, h * 0.76f); lineTo(w * 0.42f, h * 0.83f)
                    lineTo(w * 0.65f, h * 0.74f); lineTo(w * 0.85f, h * 0.82f); lineTo(w, h * 0.78f); lineTo(w, h); close()
                }
                drawPath(hill, Color(0xCC3A1558))
                pColor = Color(0xFFFFE9C7); falling = false
            }
            2 -> {
                drawRect(Color(0xFF02040A))
                var gx = 0f
                while (gx < w) { drawLine(Color(0x1A4DA3FF), Offset(gx, 0f), Offset(gx, h), 1f); gx += 28f * d }
                var gy = 0f
                while (gy < h) { drawLine(Color(0x1A4DA3FF), Offset(0f, gy), Offset(w, gy), 1f); gy += 28f * d }
                drawCircle(Brush.radialGradient(listOf(Color(0x664DA3FF), Color.Transparent), Offset(w * 0.5f, 0f), w), w, Offset(w * 0.5f, 0f))
                pColor = Color(0xFF4DA3FF); falling = false
            }
            3 -> {
                drawRect(Color(0xFF05030D))
                drawCircle(Brush.radialGradient(listOf(Color(0x66A06BFF), Color.Transparent), Offset(w * 0.5f, h * 0.22f), w * 0.9f), w * 0.9f, Offset(w * 0.5f, h * 0.22f))
                drawCircle(Brush.radialGradient(listOf(Color(0x556E3CDC), Color.Transparent), Offset(w * 0.5f, h), w), w, Offset(w * 0.5f, h))
                pColor = Color(0xFFB58BFF); falling = false
            }
            else -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF020A12), Color(0xFF04131F), Color(0xFF02080F))))
                drawCircle(Brush.radialGradient(listOf(Color(0x553DF0E0), Color.Transparent), Offset(w * 0.5f, h * 0.26f), w * 0.9f), w * 0.9f, Offset(w * 0.5f, h * 0.26f))
                listOf(94f to 0.9f, 78f to 0.5f, 62f to 0.35f).forEach { (r, a) ->
                    drawCircle(Color(0xFF3DF0E0).copy(alpha = a), r * d, Offset(w * 0.5f, h * 0.26f), style = Stroke(width = 2f * d))
                }
                pColor = Color(0xFF3DF0E0); falling = false
            }
        }
        seeds.forEach { (sx, sp, so) ->
            val prog = (phase * sp + so) % 1f
            val y = if (falling) prog * h else (1f - prog) * h
            val x = sx * w + sin((phase * sp + so) * 6.2832f) * 14f * d
            drawCircle(pColor.copy(alpha = 0.75f), (1.2f + so * 2.2f) * d, Offset(x, y))
        }
    }
}

@Composable
fun RootXBackdrop(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    RootXBg.load(context)
    Box(modifier.fillMaxSize().background(Color(0xFF0B1033))) {
        Crossfade(targetState = RootXBg.index, animationSpec = tween(500), label = "bg") { i ->
            if (i == RootXBg.MIDNIGHT) {
                Box(Modifier.fillMaxSize().background(Midnight.background))
            } else if (i < RootXBg.drawables.size) {
                Image(
                    painter = painterResource(RootXBg.drawables[i]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                RootXThemeScene(i - RootXBg.drawables.size)
            }
        }
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = RootXBg.scrim())))
    }
}

/** Small caps heading that groups settings cards. */
@Composable
fun RootXSectionLabel(text: String) {
    Text(
        text,
        modifier = Modifier.padding(start = 6.dp, top = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/** Liquid-glass bubbles for picking the app background or theme (lives in Settings > Customization). */
@Composable
fun RootXBackgroundPicker() {
    val context = LocalContext.current
    RootXBg.load(context)
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        listOf("Background" to (0..4), "Themes" to (5..10)).forEach { (title, range) ->
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                if (title == "Background") "Pick a scene for the whole app" else "Drawn themes with their own accent colour",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                range.forEach { i ->
                    val sel = RootXBg.index == i
                    val bubble by animateDpAsState(if (sel) 56.dp else 48.dp, label = "bubble")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            RootXBg.set(context, i)
                        }
                    ) {
                        Box(
                            Modifier
                                .size(bubble)
                                .clip(CircleShape)
                                .border(if (sel) 2.dp else 1.dp, if (sel) RootXBg.accent() else Color(0x80FFFFFF), CircleShape)
                        ) {
                            if (i < RootXBg.drawables.size) {
                                Image(painterResource(RootXBg.drawables[i]), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Box(Modifier.fillMaxSize().background(Brush.linearGradient(RootXBg.previews[i - RootXBg.drawables.size])))
                            }
                            Box(
                                Modifier.fillMaxSize().background(
                                    Brush.radialGradient(listOf(Color(0x99FFFFFF), Color.Transparent), center = Offset(24f, 20f), radius = 70f)
                                )
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            RootXBg.names[i],
                            fontSize = 10.sp,
                            color = if (sel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

/** Splash shown on launch: xBooM in a glowing portal, the RootX name and a progress line. */
@Composable
fun RootXSplashOverlay() {
    var show by remember { mutableStateOf(true) }
    var target by remember { mutableStateOf(0f) }
    val progress by animateFloatAsState(target, tween(1300, easing = FastOutSlowInEasing), label = "splash")
    LaunchedEffect(Unit) {
        target = 1f
        delay(1500)
        show = false
    }
    AnimatedVisibility(visible = show, enter = EnterTransition.None, exit = fadeOut(tween(350))) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1033))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
        ) {
            RootXBackdrop()
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .size(210.dp)
                        .clip(CircleShape)
                        .background(Color(0x143CC8FF))
                        .border(2.dp, Color(0xFF6FDCFF), CircleShape),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    XBoom(Modifier.height(190.dp), mood = XBoomMood.Happy, blink = false, bob = true)
                }
                Spacer(Modifier.height(28.dp))
                Text("RootX", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Root, your way", color = Color(0xFF9FC4D2), fontSize = 13.sp)
                Spacer(Modifier.height(34.dp))
                Box(
                    Modifier
                        .width(150.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x1FFFFFFF))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(progress)
                            .height(4.dp)
                            .background(Brush.horizontalGradient(listOf(RootXCyan, Color(0xFFC3A4FF))))
                    )
                }
            }
        }
    }
}


/** Full-screen PIN pad used by App lock when the lock method is App PIN. */
@Composable
fun RootXPinPrompt(onUnlocked: () -> Unit) {
    val context = LocalContext.current
    var entered by remember { mutableStateOf("") }
    var wrong by remember { mutableStateOf(false) }
    val need = RootXPin.length(context)

    fun press(d: String) {
        if (entered.length >= need) return
        entered += d
        wrong = false
        if (entered.length == need) {
            if (RootXPin.check(context, entered)) onUnlocked() else { wrong = true; entered = "" }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1033))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
    ) {
        RootXBackdrop()
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            XBoom(Modifier.height(110.dp), mood = if (wrong) XBoomMood.Scared else XBoomMood.Open)
            Spacer(Modifier.height(14.dp))
            Text(if (wrong) "Wrong PIN" else "Enter your PIN", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(need) { i ->
                    Box(
                        Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(if (i < entered.length) RootXCyan else Color(0x33FFFFFF))
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
            val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("C", "0", "<"))
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    row.forEach { k ->
                        OutlinedButton(
                            onClick = {
                                when (k) {
                                    "C" -> entered = ""
                                    "<" -> if (entered.isNotEmpty()) entered = entered.dropLast(1)
                                    else -> press(k)
                                }
                            },
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) { Text(k, fontSize = 22.sp, color = Color.White) }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}
