package com.aeroloomstudio.hailai.ui.screens.location

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun LocationMapScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedArea by remember { mutableStateOf("Islamabad") }
    val areas = listOf("Islamabad", "Rawalpindi", "Lahore", "Karachi", "Peshawar")

    // Subtle pulse for the pin marker
    val infiniteTransition = rememberInfiniteTransition(label = "pin_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .statusBarsPadding(),
    ) {
        // ── Top bar ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = FeatherIcons.ArrowLeft,
                    contentDescription = "Back",
                    tint = TextPrimary,
                )
            }
            Text(
                text = "Default Location",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
        }

        // ── Simulated Map Area ───────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFE8F0FE),
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Canvas draws a stylized "map"
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Background gradient
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE8F0FE),
                                Color(0xFFF0F4F8),
                                Color(0xFFE3EDF7),
                            ),
                        ),
                    )

                    // Draw stylized roads — horizontal
                    val roadColor = Color(0xFFD0D8E4)
                    val roadWidth = 3f
                    for (i in 1..6) {
                        val y = h * i / 7f
                        drawLine(roadColor, Offset(0f, y), Offset(w, y), roadWidth)
                    }
                    // Vertical roads
                    for (i in 1..5) {
                        val x = w * i / 6f
                        drawLine(roadColor, Offset(x, 0f), Offset(x, h), roadWidth)
                    }

                    // Draw some "blocks" as filled rectangles
                    val blockColor = Color(0xFFD6E4FA).copy(alpha = 0.5f)
                    for (row in 0..5) {
                        for (col in 0..4) {
                            val x1 = w * col / 6f + 8f
                            val y1 = h * row / 7f + 8f
                            val x2 = w * (col + 1) / 6f - 8f
                            val y2 = h * (row + 1) / 7f - 8f
                            drawRoundRect(
                                color = blockColor,
                                topLeft = Offset(x1, y1),
                                size = androidx.compose.ui.geometry.Size(x2 - x1, y2 - y1),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                            )
                        }
                    }

                    // Draw a main road (thicker diagonal)
                    drawLine(
                        color = Color(0xFFB8C8DA),
                        start = Offset(0f, h * 0.3f),
                        end = Offset(w, h * 0.7f),
                        strokeWidth = 6f,
                    )

                    // Draw pin pulse circle
                    val pinX = w * 0.5f
                    val pinY = h * 0.45f
                    drawCircle(
                        color = HailBlue.copy(alpha = pulseAlpha),
                        radius = 30f * pulseScale,
                        center = Offset(pinX, pinY),
                    )

                    // Draw pin shadow
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.15f),
                        radius = 8f,
                        center = Offset(pinX, pinY + 28f),
                    )

                    // Draw pin marker
                    val pinPath = Path().apply {
                        moveTo(pinX, pinY + 24f)
                        lineTo(pinX - 14f, pinY - 4f)
                        quadraticBezierTo(pinX - 14f, pinY - 24f, pinX, pinY - 24f)
                        quadraticBezierTo(pinX + 14f, pinY - 24f, pinX + 14f, pinY - 4f)
                        close()
                    }
                    drawPath(pinPath, color = HailBlue, style = Fill)
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = Offset(pinX, pinY - 10f),
                    )
                }

                // Location label overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Surface(
                        shape = PillShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = FeatherIcons.MapPin,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = HailBlue,
                            )
                            Text(
                                text = selectedArea,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                            )
                        }
                    }
                }
            }
        }

        // ── Area Selection ───────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                Text(
                    text = "Select Area",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
                areas.forEach { area ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedArea = area }
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = FeatherIcons.MapPin,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (area == selectedArea) HailBlue else TextTertiary,
                        )
                        Text(
                            text = area,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (area == selectedArea) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (area == selectedArea) HailBlue else TextPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        if (area == selectedArea) {
                            Icon(
                                imageVector = FeatherIcons.Check,
                                contentDescription = "Selected",
                                modifier = Modifier.size(18.dp),
                                tint = HailBlue,
                            )
                        }
                    }
                }
            }
        }

        // ── Save Button ─────────────────────────────────────────────────────
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = HailBlue,
                contentColor = TextOnBlue,
            ),
        ) {
            Text(
                text = "Save Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(8.dp).navigationBarsPadding())
    }
}
