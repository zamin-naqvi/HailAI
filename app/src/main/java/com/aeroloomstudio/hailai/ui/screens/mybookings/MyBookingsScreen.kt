package com.aeroloomstudio.hailai.ui.screens.mybookings

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.data.model.*
import com.aeroloomstudio.hailai.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    bookings: List<Booking>,
    onBackClick: () -> Unit,
    onBookingClick: (Booking) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf("Active", "Completed", "Cancelled")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    // ── Animated bottom gradient (always visible, color-cycling) ─────────
    val infiniteTransition = rememberInfiniteTransition(label = "bookings_grad")
    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "color_phase",
    )

    val gradColor1 = when {
        colorPhase < 0.25f -> lerpColor(Color(0xFF93B4F5), Color(0xFFC4A1E0), colorPhase * 4f)
        colorPhase < 0.50f -> lerpColor(Color(0xFFC4A1E0), Color(0xFFF0A8C4), (colorPhase - 0.25f) * 4f)
        colorPhase < 0.75f -> lerpColor(Color(0xFFF0A8C4), Color(0xFF8DD8C4), (colorPhase - 0.50f) * 4f)
        else               -> lerpColor(Color(0xFF8DD8C4), Color(0xFF93B4F5), (colorPhase - 0.75f) * 4f)
    }
    val gradColor2 = when {
        colorPhase < 0.25f -> lerpColor(Color(0xFFB8D0FA), Color(0xFFDDB8E8), colorPhase * 4f)
        colorPhase < 0.50f -> lerpColor(Color(0xFFDDB8E8), Color(0xFFF5C0D5), (colorPhase - 0.25f) * 4f)
        colorPhase < 0.75f -> lerpColor(Color(0xFFF5C0D5), Color(0xFFACE5D8), (colorPhase - 0.50f) * 4f)
        else               -> lerpColor(Color(0xFFACE5D8), Color(0xFFB8D0FA), (colorPhase - 0.75f) * 4f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceLight),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // ── Top bar ──────────────────────────────────────────────────────
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
                    text = "My Bookings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                )
            }

            // ── Segmented Control Tabs (iOS Style) ───────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = PillShape,
                color = Color(0xFFF0F0F0), // Light Gray background
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = pagerState.currentPage == index
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = PillShape,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color.DarkGray,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Pager ────────────────────────────────────────────────────────
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
            ) { page ->
                val filtered = when (page) {
                    0 -> bookings.filter {
                        it.status in listOf(
                            BookingStatus.PENDING,
                            BookingStatus.CONFIRMED,
                            BookingStatus.REMINDER_SENT,
                            BookingStatus.IN_PROGRESS,
                        )
                    }
                    1 -> bookings.filter { it.status == BookingStatus.COMPLETED }
                    2 -> bookings.filter { it.status == BookingStatus.CANCELLED }
                    else -> emptyList()
                }

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = FeatherIcons.Calendar,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = TextTertiary,
                            )
                            Text(
                                text = "No ${tabs[page].lowercase()} bookings",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextTertiary,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 120.dp, // Extra bottom to clear gradient
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filtered, key = { it.bookingId }) { booking ->
                            BookingListCard(
                                booking = booking,
                                onClick = { onBookingClick(booking) },
                            )
                        }
                    }
                }
            }
        }

        // ── Always-visible animated bottom gradient ──────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            gradColor1.copy(alpha = 0.25f),
                            gradColor2.copy(alpha = 0.45f),
                            gradColor1.copy(alpha = 0.55f),
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun BookingListCard(
    booking: Booking,
    onClick: () -> Unit,
) {
    Surface(
        shape = CardShape,
        color = SurfaceWhite,
        onClick = onClick,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = booking.providerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                StatusBadge(booking.status)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.Tool,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary,
                    )
                    Text(
                        text = booking.serviceType,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.Clock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary,
                    )
                    Text(
                        text = booking.slotDatetime
                            .replace("T", " ")
                            .removeSuffix(":00"),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = FeatherIcons.MapPin,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = TextSecondary,
                )
                Text(
                    text = booking.locationRequested,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }

            // Booking ID
            Text(
                text = booking.bookingId,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
            )
        }
    }
}

@Composable
private fun StatusBadge(status: BookingStatus) {
    val (color, bgColor) = when (status) {
        BookingStatus.CONFIRMED, BookingStatus.REMINDER_SENT -> StatusGreen to StatusGreenLight
        BookingStatus.PENDING, BookingStatus.IN_PROGRESS -> StatusOrange to StatusOrangeLight
        BookingStatus.COMPLETED -> HailBlue to HailBlueSoft
        BookingStatus.CANCELLED, BookingStatus.DISPUTED -> StatusRed to StatusRedLight
    }

    Surface(
        shape = PillShape,
        color = bgColor,
    ) {
        Text(
            text = status.displayName(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
    }
}

/** Simple color lerp utility. */
private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val ct = t.coerceIn(0f, 1f)
    return Color(
        red   = a.red   + (b.red   - a.red)   * ct,
        green = a.green + (b.green - a.green) * ct,
        blue  = a.blue  + (b.blue  - a.blue)  * ct,
        alpha = a.alpha + (b.alpha - a.alpha) * ct,
    )
}
