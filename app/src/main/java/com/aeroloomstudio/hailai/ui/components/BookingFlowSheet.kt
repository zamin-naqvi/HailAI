package com.aeroloomstudio.hailai.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.data.model.Provider
import com.aeroloomstudio.hailai.data.model.displayCategory
import com.aeroloomstudio.hailai.data.model.priceRange
import com.aeroloomstudio.hailai.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

/**
 * Multi-step booking bottom sheet:
 * Step 1 → Select time slot
 * Step 2 → Confirm location & details
 * Step 3 → Processing animation (with bottom gradient)
 * Step 4 → Booking confirmed!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFlowSheet(
    provider: Provider,
    onDismiss: () -> Unit,
    onBookingConfirmed: (Provider, String) -> Unit,
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedSlot by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceWhite,
        shape = SheetShape,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .clip(PillShape)
                        .background(TextTertiary.copy(alpha = 0.4f)),
                )
            }
        },
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it / 3 } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it / 3 } + fadeOut()
                }
            },
            label = "booking_steps",
        ) { step ->
            when (step) {
                1 -> TimeSlotStep(
                    provider = provider,
                    selectedSlot = selectedSlot,
                    onSlotSelected = { selectedSlot = it },
                    onNext = { currentStep = 2 },
                )
                2 -> ConfirmDetailsStep(
                    provider = provider,
                    selectedSlot = selectedSlot ?: "",
                    onBack = { currentStep = 1 },
                    onConfirm = {
                        currentStep = 3
                        scope.launch {
                            delay(2200)
                            currentStep = 4
                        }
                    },
                )
                3 -> ProcessingStep(provider = provider)
                4 -> ConfirmedStep(
                    provider = provider,
                    selectedSlot = selectedSlot ?: "",
                    onDone = {
                        onBookingConfirmed(provider, selectedSlot ?: "10:00")
                        onDismiss()
                    },
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Step 1: Time Slot Selection
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun TimeSlotStep(
    provider: Provider,
    selectedSlot: String?,
    onSlotSelected: (String) -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
    ) {
        // Provider mini header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(HailBlueSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = FeatherIcons.User,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = HailBlue,
                )
            }
            Column {
                Text(
                    text = provider.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = "${provider.displayCategory()} · ${provider.priceRange()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Step indicator
        StepIndicator(currentStep = 1, totalSteps = 3)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Select a Time",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = "Choose an available slot for your appointment",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        Spacer(Modifier.height(16.dp))

        // Time slot grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 200.dp),
        ) {
            items(provider.availableSlots) { slot ->
                val isSelected = slot == selectedSlot
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) HailBlue else SurfaceLight,
                    modifier = Modifier.clickable { onSlotSelected(slot) },
                ) {
                    Text(
                        text = slot,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 14.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TextOnBlue else TextPrimary,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onNext,
            enabled = selectedSlot != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = HailBlue,
                contentColor = TextOnBlue,
                disabledContainerColor = SurfaceDim,
                disabledContentColor = TextTertiary,
            ),
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.width(8.dp))
            Icon(FeatherIcons.ArrowRight, null, Modifier.size(18.dp))
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Step 2: Confirm Details (NO gradient — clean white)
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ConfirmDetailsStep(
    provider: Provider,
    selectedSlot: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
    ) {
        StepIndicator(currentStep = 2, totalSteps = 3)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Confirm Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = "Review your booking before confirming",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        Spacer(Modifier.height(20.dp))

        // Details card
        Surface(
            shape = CardShape,
            color = SurfaceLight,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                DetailRow(
                    icon = FeatherIcons.User,
                    label = "Provider",
                    value = provider.name,
                )
                DetailRow(
                    icon = FeatherIcons.Tool,
                    label = "Service",
                    value = provider.displayCategory(),
                )
                DetailRow(
                    icon = FeatherIcons.Clock,
                    label = "Time",
                    value = selectedSlot,
                )
                DetailRow(
                    icon = FeatherIcons.MapPin,
                    label = "Location",
                    value = "${provider.area}, ${provider.city}",
                )
                DetailRow(
                    icon = FeatherIcons.DollarSign,
                    label = "Estimate",
                    value = provider.priceRange(),
                )
                DetailRow(
                    icon = FeatherIcons.Star,
                    label = "Rating",
                    value = "${provider.rating} (${provider.totalReviews} reviews)",
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = PillShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            ) {
                Icon(FeatherIcons.ArrowLeft, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Back", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HailBlue,
                    contentColor = TextOnBlue,
                ),
            ) {
                Icon(FeatherIcons.Check, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Confirm Booking",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Step 3: Processing — gradient from bottom of entire sheet
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProcessingStep(provider: Provider) {
    val infiniteTransition = rememberInfiniteTransition(label = "processing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val borderPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "border",
    )

    val color1 = Color(
        red = 0.48f + 0.22f * borderPhase,
        green = 0.64f - 0.10f * borderPhase,
        blue = 0.94f - 0.12f * borderPhase,
    )
    val color2 = Color(
        red = 0.71f - 0.27f * borderPhase,
        green = 0.54f + 0.24f * borderPhase,
        blue = 0.85f - 0.17f * borderPhase,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
    ) {
        // Bottom gradient that sits at the bottom of the sheet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            color1.copy(alpha = 0.20f),
                            color2.copy(alpha = 0.35f),
                            Color(0xFF6B96EB).copy(alpha = 0.40f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Animated circle with gradient border
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(color1, color2, color1),
                        ),
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(SurfaceWhite),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = FeatherIcons.Calendar,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = HailBlue,
                )
            }

            Text(
                text = "Booking ${provider.name}...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Setting up your appointment, scheduling reminders, and sending confirmation",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )

            // Animated progress dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 1200
                                0.3f at 0
                                0.3f at index * 200
                                1f at index * 200 + 300
                                0.3f at index * 200 + 600
                                0.3f at 1200
                            },
                            repeatMode = RepeatMode.Restart,
                        ),
                        label = "dot_$index",
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(dotAlpha)
                            .background(HailBlue, CircleShape),
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Step 4: Booking Confirmed! (NO gradient — clean white)
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ConfirmedStep(
    provider: Provider,
    selectedSlot: String,
    onDone: () -> Unit,
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val checkScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "check_scale",
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(600, delayMillis = 300),
        label = "content_alpha",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))

        // Animated success checkmark
        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(checkScale)
                .clip(CircleShape)
                .background(StatusGreenLight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = FeatherIcons.Check,
                contentDescription = "Confirmed",
                modifier = Modifier.size(40.dp),
                tint = StatusGreen,
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Booking Confirmed!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = StatusGreen,
            modifier = Modifier.alpha(contentAlpha),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${provider.name} has been booked",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.alpha(contentAlpha),
        )

        Spacer(Modifier.height(20.dp))

        // Confirmed details card
        Surface(
            shape = CardShape,
            color = SurfaceLight,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(contentAlpha),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                DetailRow(FeatherIcons.Clock, "Time", selectedSlot)
                DetailRow(FeatherIcons.MapPin, "Location", "${provider.area}, ${provider.city}")
                DetailRow(FeatherIcons.DollarSign, "Estimate", provider.priceRange())
            }
        }

        Spacer(Modifier.height(12.dp))

        // Reminders info
        Surface(
            shape = CardShape,
            color = HailBlueSoft,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(contentAlpha),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = FeatherIcons.Bell,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = HailBlue,
                )
                Text(
                    text = "Reminders have been scheduled for your appointment",
                    style = MaterialTheme.typography.bodySmall,
                    color = HailBlue,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .alpha(contentAlpha),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = HailBlue,
                contentColor = TextOnBlue,
            ),
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Shared Components
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val step = index + 1
            val isActive = step <= currentStep

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isActive) HailBlue else SurfaceDim,
                    ),
            )
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
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = TextSecondary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.width(70.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
    }
}
