package com.aeroloomstudio.hailai.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.data.model.*
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun ChatBubble(
    message: ChatMessage,
    onProviderClick: ((Provider) -> Unit)? = null,
    onBookingClick: ((Booking) -> Unit)? = null,
    onBookNowClick: ((Provider) -> Unit)? = null,
    bookedProviderIds: Set<String> = emptySet(),
    modifier: Modifier = Modifier,
) {
    when (message.type) {
        MessageType.USER -> UserBubble(message, modifier)
        MessageType.AI_TEXT -> AiBubble(message, modifier)
        MessageType.AI_THINKING -> AiThinkingBubble(message, modifier)
        MessageType.AI_AGENT_STEP -> AgentStepBubble(message, modifier)
        MessageType.AI_PROVIDER_CARD -> ProviderCardBubble(message, onProviderClick, onBookNowClick, bookedProviderIds, modifier)
        MessageType.AI_BOOKING_CONFIRM -> BookingConfirmBubble(message, onBookingClick, modifier)
        MessageType.AI_TRACE_SUMMARY -> TraceSummaryBubble(message, modifier)
        MessageType.SYSTEM -> SystemBubble(message, modifier)
    }
}

@Composable
private fun UserBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 60.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        Surface(
            shape = ChatBubbleUserShape,
            color = SurfaceWhite,
            modifier = Modifier.border(1.dp, DividerColor.copy(alpha = 0.35f), ChatBubbleUserShape),
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
            )
        }
    }
}

@Composable
private fun AiBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 60.dp, top = 4.dp, bottom = 4.dp),
    ) {
        Text(
            text = message.content,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
        )
    }
}

@Composable
private fun AiThinkingBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 60.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TypingIndicator()
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Compact single-line Agent Step — with fade-up entrance
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun AgentStepBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    val step = message.agentStep ?: return
    val isCompleted = step.status == AgentStepStatus.COMPLETED
    val isRunning = step.status == AgentStepStatus.RUNNING

    // Fade-up entrance
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val entranceAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "step_alpha",
    )
    val entranceOffset by animateFloatAsState(
        targetValue = if (appeared) 0f else 12f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "step_offset",
    )

    val icon = agentStepIcon(step.stepNumber)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 40.dp, top = 2.dp, bottom = 2.dp)
            .offset(y = entranceOffset.dp)
            .alpha(entranceAlpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (isRunning) {
            // Pulsing icon
            val infiniteTransition = rememberInfiniteTransition(label = "step_run")
            val pulse by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "pulse",
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp).alpha(pulse),
                tint = HailBlue,
            )
            Text(
                text = step.agentName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
            TypingIndicator(modifier = Modifier.width(24.dp))
        } else if (isCompleted) {
            Icon(
                imageVector = FeatherIcons.Check,
                contentDescription = "Done",
                modifier = Modifier.size(13.dp),
                tint = StatusGreen,
            )
            Text(
                text = "${step.agentName} · ${step.outputSummary}",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                maxLines = 1,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Trace Summary — compact "X steps completed → View trace"
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun TraceSummaryBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    val steps = message.completedSteps ?: return
    var showTraceSheet by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 40.dp, top = 2.dp, bottom = 6.dp)
            .clickable { showTraceSheet = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = FeatherIcons.ChevronDown,
            contentDescription = "View trace",
            modifier = Modifier.size(14.dp),
            tint = HailBlue,
        )
        Text(
            text = "${steps.size} agent steps completed",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = HailBlue,
        )
        Text(
            text = "· View trace",
            style = MaterialTheme.typography.labelSmall,
            color = HailBlue.copy(alpha = 0.7f),
        )
    }

    if (showTraceSheet) {
        AgentTraceBottomSheet(
            steps = steps,
            onDismiss = { showTraceSheet = false },
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Agent Trace Bottom Sheet — detailed step logs
// ═════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentTraceBottomSheet(
    steps: List<AgentStep>,
    onDismiss: () -> Unit,
) {
    val sheetShape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceWhite,
        shape = sheetShape,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(32.dp)
                    .height(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = PillShape,
                    color = TextTertiary.copy(alpha = 0.4f),
                ) {}
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Agent Trace",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
            Text(
                text = "${steps.size} steps completed",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(steps) { step ->
                    TraceStepCard(step)
                }
            }
        }
    }
}

@Composable
private fun TraceStepCard(step: AgentStep) {
    val icon = agentStepIcon(step.stepNumber)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceLight,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Step number circle with icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (step.status == AgentStepStatus.COMPLETED) StatusGreenLight
                        else HailBlueSoft,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (step.status == AgentStepStatus.COMPLETED) StatusGreen else HailBlue,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = step.agentName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    if (step.durationMs > 0) {
                        Text(
                            text = "${step.durationMs}ms",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                    }
                }
                Text(
                    text = step.toolUsed,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = step.outputSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Provider & Booking Bubbles
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProviderCardBubble(
    message: ChatMessage,
    onProviderClick: ((Provider) -> Unit)?,
    onBookNowClick: ((Provider) -> Unit)?,
    bookedProviderIds: Set<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (message.content.isNotBlank()) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
            )
        }
        message.providers?.forEach { provider ->
            ProviderCard(
                provider = provider,
                onClick = { onBookNowClick?.invoke(provider) ?: onProviderClick?.invoke(provider) },
                isBooked = provider.id in bookedProviderIds,
            )
        }
    }
}

@Composable
private fun BookingConfirmBubble(
    message: ChatMessage,
    onBookingClick: ((Booking) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val booking = message.booking ?: return

    // Entrance animation
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val entranceScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "booking_scale",
    )
    val entranceAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(600),
        label = "booking_alpha",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .scale(entranceScale)
            .alpha(entranceAlpha),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            shape = CardShape,
            color = Color.Transparent,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                HailBlueSoft,
                                SurfaceWhite,
                                Color(0xFFF3E8FF),
                            ),
                        ),
                        shape = CardShape,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = FeatherIcons.CheckCircle,
                            contentDescription = "Confirmed",
                            tint = StatusGreen,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text = "Booking Confirmed!",
                            style = MaterialTheme.typography.titleMedium,
                            color = StatusGreen,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Surface(
                        shape = PillShape,
                        color = StatusGreen.copy(alpha = 0.1f),
                    ) {
                        Text(
                            text = booking.bookingId,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = StatusGreen,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    HorizontalDivider(color = DividerColor)

                    InfoRow("Provider", booking.providerName)
                    InfoRow("Service", booking.serviceType)
                    InfoRow("Time", booking.slotDatetime.replace("T", " at "))
                    InfoRow("Location", booking.locationRequested)
                    InfoRow("Price", booking.priceEstimate)

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = booking.confirmationMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )

                    OutlinedButton(
                        onClick = { onBookingClick?.invoke(booking) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = PillShape,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = HailBlue,
                        ),
                    ) {
                        Text(
                            text = "View Booking Details",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
        )
    }
}

@Composable
private fun SystemBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message.content,
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Helpers
// ═════════════════════════════════════════════════════════════════════════════

/** Map agent step number to a proper icon (no emojis). */
private fun agentStepIcon(stepNumber: Int): ImageVector = when (stepNumber) {
    1 -> FeatherIcons.Cpu         // NLU Agent
    2 -> FeatherIcons.Search      // Discovery Agent
    3 -> FeatherIcons.BarChart2   // Ranking Agent
    4 -> FeatherIcons.Calendar    // Booking Agent
    5 -> FeatherIcons.Bell        // Notification Agent
    6 -> FeatherIcons.Clock       // Follow-Up Agent
    else -> FeatherIcons.Activity
}
