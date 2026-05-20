package com.aeroloomstudio.hailai.ui.screens.booking

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.data.model.Booking
import com.aeroloomstudio.hailai.ui.components.GradientBackground
import com.aeroloomstudio.hailai.ui.components.GradientState
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun BookingConfirmationScreen(
    booking: Booking?,
    onBackClick: () -> Unit,
    onTraceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (booking == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Booking not found", color = TextSecondary)
        }
        return
    }

    GradientBackground(state = GradientState.DONE) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = FeatherIcons.ArrowLeft,
                        contentDescription = "Back",
                        tint = TextPrimary,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Animated checkmark
            AnimatedCheckmark(
                modifier = Modifier.size(100.dp),
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Booking Confirmed!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = StatusGreen,
            )

            Spacer(Modifier.height(8.dp))

            // Booking ID badge
            Surface(
                shape = PillShape,
                color = HailBlueSoft,
            ) {
                Text(
                    text = booking.bookingId,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = HailBlue,
                )
            }

            Spacer(Modifier.height(32.dp))

            // Details card — white bg with subtle gray outline
            Surface(
                shape = CardShape,
                color = Color.White,
                modifier = Modifier.border(
                    width = 0.8.dp,
                    color = Color(0xFF000000).copy(alpha = 0.07f),
                    shape = CardShape,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    DetailRow(
                        icon = FeatherIcons.User,
                        label = "Provider",
                        value = booking.providerName,
                    )
                    HorizontalDivider(color = DividerColor)
                    DetailRow(
                        icon = FeatherIcons.Tool,
                        label = "Service",
                        value = booking.serviceType,
                    )
                    HorizontalDivider(color = DividerColor)
                    DetailRow(
                        icon = FeatherIcons.Clock,
                        label = "Time",
                        value = booking.slotDatetime
                            .replace("T", " at ")
                            .removeSuffix(":00"),
                    )
                    HorizontalDivider(color = DividerColor)
                    DetailRow(
                        icon = FeatherIcons.MapPin,
                        label = "Location",
                        value = booking.locationRequested,
                    )
                    HorizontalDivider(color = DividerColor)
                    DetailRow(
                        icon = FeatherIcons.DollarSign,
                        label = "Estimate",
                        value = booking.priceEstimate,
                    )
                    HorizontalDivider(color = DividerColor)
                    DetailRow(
                        icon = FeatherIcons.Phone,
                        label = "Contact",
                        value = booking.providerPhone,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Confirmation message card
            Surface(
                shape = CardShape,
                color = StatusGreenLight,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.MessageCircle,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = booking.confirmationMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Action buttons
            Button(
                onClick = onTraceClick,
                modifier = Modifier.fillMaxWidth(),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HailBlue,
                    contentColor = TextOnBlue,
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Icon(
                    imageVector = FeatherIcons.Activity,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "View Agent Trace",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                shape = PillShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary,
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Icon(
                    imageVector = FeatherIcons.Home,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Back to Home",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(18.dp),
            tint = HailBlue,
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
        }
    }
}

@Composable
private fun AnimatedCheckmark(modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(StatusGreenLight),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(48.dp)) {
            val p = progress.value
            // Draw circle
            drawCircle(
                color = StatusGreen,
                radius = size.minDimension / 2 * p,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
            // Draw checkmark
            if (p > 0.5f) {
                val checkProgress = ((p - 0.5f) * 2f).coerceIn(0f, 1f)
                val cx = size.width / 2
                val cy = size.height / 2

                val startX = cx - size.width * 0.18f
                val startY = cy + size.height * 0.02f
                val midX = cx - size.width * 0.04f
                val midY = cy + size.height * 0.18f
                val endX = cx + size.width * 0.22f
                val endY = cy - size.height * 0.15f

                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(startX, startY)
                    if (checkProgress <= 0.5f) {
                        val t = checkProgress * 2
                        lineTo(
                            startX + (midX - startX) * t,
                            startY + (midY - startY) * t
                        )
                    } else {
                        lineTo(midX, midY)
                        val t = (checkProgress - 0.5f) * 2
                        lineTo(
                            midX + (endX - midX) * t,
                            midY + (endY - midY) * t
                        )
                    }
                }
                drawPath(
                    path = path,
                    color = StatusGreen,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}
