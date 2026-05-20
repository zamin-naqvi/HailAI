package com.aeroloomstudio.hailai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.math.sin

enum class GradientState { IDLE, THINKING, DONE }

@Composable
fun GradientBackground(
    state: GradientState = GradientState.IDLE,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "grad")

    // ── Entrance animation: 10 seconds, 1.0→0.0 ──────────────────────────
    val entranceAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        delay(100)
        entranceAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(10000, easing = FastOutSlowInEasing),
        )
    }
    val entrance = entranceAnim.value

    // ── Colour cycling for THINKING ───────────────────────────────────────
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "color_phase",
    )

    // ── Subtle breathing ──────────────────────────────────────────────────
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "breathe",
    )

    // ── Overall alpha ─────────────────────────────────────────────────────
    val gradientAlpha by animateFloatAsState(
        targetValue = when (state) {
            GradientState.IDLE -> 1f
            GradientState.THINKING -> 1f
            GradientState.DONE -> 0f
        },
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "grad_alpha",
    )

    // ── Position ──────────────────────────────────────────────────────────
    val gradientCenterY by animateFloatAsState(
        targetValue = when (state) {
            GradientState.IDLE -> 1f
            GradientState.THINKING -> 0f
            GradientState.DONE -> 1f
        },
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow),
        label = "grad_center",
    )

    // ── Thinking colour cycle ─────────────────────────────────────────────
    val thinkingColor1 = when {
        colorPhase < 0.25f -> lerp(Color(0xFF7BA3F0), Color(0xFFB48AD8), colorPhase * 4f)
        colorPhase < 0.50f -> lerp(Color(0xFFB48AD8), Color(0xFFE88FAF), (colorPhase - 0.25f) * 4f)
        colorPhase < 0.75f -> lerp(Color(0xFFE88FAF), Color(0xFF6FC7AE), (colorPhase - 0.50f) * 4f)
        else -> lerp(Color(0xFF6FC7AE), Color(0xFF7BA3F0), (colorPhase - 0.75f) * 4f)
    }
    val thinkingColor2 = when {
        colorPhase < 0.25f -> lerp(Color(0xFF9BBCF5), Color(0xFFCCA0E0), colorPhase * 4f)
        colorPhase < 0.50f -> lerp(Color(0xFFCCA0E0), Color(0xFFF0ABC5), (colorPhase - 0.25f) * 4f)
        colorPhase < 0.75f -> lerp(Color(0xFFF0ABC5), Color(0xFF90D8C4), (colorPhase - 0.50f) * 4f)
        else -> lerp(Color(0xFF90D8C4), Color(0xFF9BBCF5), (colorPhase - 0.75f) * 4f)
    }

    // ── Corner colours & alpha ────────────────────────────────────────────
    val breatheAlpha = sin(breathe * 2f * Math.PI.toFloat()) * 0.08f + 0.08f
    val cornerAlpha = (entrance * 0.75f + breatheAlpha * (1f - entrance)) * gradientAlpha

    val cornerBlue     = Color(0xFF5B8AE5)   // top-left
    val cornerLavender = Color(0xFFA47AD0)   // top-right
    val cornerTeal     = Color(0xFF5CBFA0)   // bottom-left
    val cornerPink     = Color(0xFFE07FA0)   // bottom-right

    // Radius shrinks as entrance goes 1→0
    val radiusFraction = 0.45f + entrance * 0.55f  // 1.0→0.45

    // Main vertical gradient
    val mainBrush = buildMainGradient(gradientCenterY, gradientAlpha, thinkingColor1, thinkingColor2)

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // White base
                drawRect(Color.White)

                if (gradientAlpha < 0.01f) return@drawBehind

                val w = size.width
                val h = size.height
                val r = maxOf(w, h) * radiusFraction

                // ── Corner radial glows using real coordinates ──

                // Top-left corner
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            cornerBlue.copy(alpha = cornerAlpha),
                            cornerBlue.copy(alpha = cornerAlpha * 0.3f),
                            Color.Transparent,
                        ),
                        center = Offset(0f, 0f),
                        radius = r,
                    ),
                    center = Offset(0f, 0f),
                    radius = r,
                )

                // Top-right corner
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            cornerLavender.copy(alpha = cornerAlpha * 0.8f),
                            cornerLavender.copy(alpha = cornerAlpha * 0.2f),
                            Color.Transparent,
                        ),
                        center = Offset(w, 0f),
                        radius = r * 0.9f,
                    ),
                    center = Offset(w, 0f),
                    radius = r * 0.9f,
                )

                // Bottom-left corner
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            cornerTeal.copy(alpha = cornerAlpha * 0.65f),
                            cornerTeal.copy(alpha = cornerAlpha * 0.15f),
                            Color.Transparent,
                        ),
                        center = Offset(0f, h),
                        radius = r * 0.85f,
                    ),
                    center = Offset(0f, h),
                    radius = r * 0.85f,
                )

                // Bottom-right corner
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            cornerPink.copy(alpha = cornerAlpha * 0.55f),
                            cornerPink.copy(alpha = cornerAlpha * 0.1f),
                            Color.Transparent,
                        ),
                        center = Offset(w, h),
                        radius = r * 0.8f,
                    ),
                    center = Offset(w, h),
                    radius = r * 0.8f,
                )

                // ── Main vertical gradient band ──
                drawRect(brush = mainBrush)
            },
        content = content,
    )
}

private fun buildMainGradient(
    gradientCenterY: Float,
    gradientAlpha: Float,
    thinkingColor1: Color,
    thinkingColor2: Color,
): Brush {
    val idleColors = listOf(
        Color.White,
        Color.White,
        Color(0xFFCDDDF8).copy(alpha = 0.55f),
        Color(0xFF8AABF0).copy(alpha = 0.72f),
        Color(0xFF6B96EB).copy(alpha = 0.82f),
    )
    val thinkingColors = listOf(
        thinkingColor1.copy(alpha = 0.78f),
        thinkingColor2.copy(alpha = 0.60f),
        Color.White.copy(alpha = 0.95f),
        Color.White,
        Color.White,
    )
    val blendedColors = idleColors.zip(thinkingColors).map { (idle, thinking) ->
        lerp(thinking, idle, gradientCenterY).let { blended ->
            blended.copy(alpha = blended.alpha * gradientAlpha)
        }
    }
    return Brush.verticalGradient(colors = blendedColors)
}

private fun lerp(a: Color, b: Color, t: Float): Color {
    val ct = t.coerceIn(0f, 1f)
    return Color(
        red   = a.red   + (b.red   - a.red)   * ct,
        green = a.green + (b.green - a.green) * ct,
        blue  = a.blue  + (b.blue  - a.blue)  * ct,
        alpha = a.alpha + (b.alpha - a.alpha) * ct,
    )
}
