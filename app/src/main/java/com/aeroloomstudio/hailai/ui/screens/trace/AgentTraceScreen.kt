package com.aeroloomstudio.hailai.ui.screens.trace

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.data.model.AgentStep
import com.aeroloomstudio.hailai.data.model.AgentStepStatus
import com.aeroloomstudio.hailai.ui.components.GradientBackground
import com.aeroloomstudio.hailai.ui.components.GradientState
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun AgentTraceScreen(
    steps: List<AgentStep>,
    bookingId: String?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GradientBackground(state = GradientState.IDLE) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // Top bar
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Agent Trace",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    if (bookingId != null) {
                        Text(
                            text = bookingId,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                        )
                    }
                }
                // Export button
                Surface(
                    shape = PillShape,
                    color = HailBlueSoft,
                    onClick = { },
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Download,
                            contentDescription = "Export",
                            modifier = Modifier.size(14.dp),
                            tint = HailBlue,
                        )
                        Text(
                            text = "Export",
                            style = MaterialTheme.typography.labelMedium,
                            color = HailBlue,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            // Total time summary
            val totalTime = steps.sumOf { it.durationMs }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, DividerColor.copy(alpha = 0.35f), CardShape),
                shape = CardShape,
                color = SurfaceWhite,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatItem("Steps", "${steps.size}")
                    VerticalDivider(
                        modifier = Modifier.height(32.dp),
                        color = DividerColor,
                    )
                    StatItem("Total Time", "${totalTime}ms")
                    VerticalDivider(
                        modifier = Modifier.height(32.dp),
                        color = DividerColor,
                    )
                    StatItem(
                        "Status",
                        if (steps.all { it.status == AgentStepStatus.COMPLETED }) "✅ Done" else "⏳ Running"
                    )
                }
            }

            // Timeline
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                itemsIndexed(steps) { index, step ->
                    TraceStepCard(
                        step = step,
                        isLast = index == steps.lastIndex,
                    )
                }

                if (steps.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = FeatherIcons.Activity,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = TextTertiary,
                                )
                                Text(
                                    text = "No trace data yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextTertiary,
                                )
                                Text(
                                    text = "Send a request to see agent steps",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
        )
    }
}

private fun agentStepIcon(stepNumber: Int): ImageVector = when (stepNumber) {
    1 -> FeatherIcons.Cpu
    2 -> FeatherIcons.Search
    3 -> FeatherIcons.BarChart2
    4 -> FeatherIcons.Calendar
    5 -> FeatherIcons.Bell
    6 -> FeatherIcons.Clock
    else -> FeatherIcons.Activity
}

@Composable
private fun TraceStepCard(
    step: AgentStep,
    isLast: Boolean,
) {
    var isExpanded by remember { mutableStateOf(false) }

    val statusColor = when (step.status) {
        AgentStepStatus.COMPLETED -> StatusGreen
        AgentStepStatus.RUNNING -> StatusOrange
        AgentStepStatus.FAILED -> StatusRed
        AgentStepStatus.PENDING -> TextTertiary
    }

    val icon = agentStepIcon(step.stepNumber)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        // Timeline line + dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp).fillMaxHeight(),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = statusColor,
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(DividerColor),
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Card content
        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
                .border(1.dp, DividerColor.copy(alpha = 0.35f), CardShape),
            shape = CardShape,
            color = SurfaceWhite,
            onClick = { isExpanded = !isExpanded },
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Step ${step.stepNumber}: ${step.agentName}",
                        style = MaterialTheme.typography.titleSmall,
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

                // Tool used
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.Tool,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = TextTertiary,
                    )
                    Text(
                        text = step.toolUsed,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                    )
                }

                // Output summary
                Text(
                    text = step.outputSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )

                // Expandable JSON
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        HorizontalDivider(color = DividerColor)

                        Text(
                            text = "Input",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextTertiary,
                        )
                        Text(
                            text = step.inputSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )

                        Text(
                            text = "Output JSON",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextTertiary,
                        )
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = SurfaceLight,
                        ) {
                            Text(
                                text = if (step.outputJson.isNotBlank()) step.outputJson else "{\n  \"status\": \"completed\",\n  \"data\": \"none\"\n}",
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                ),
                                color = TextPrimary,
                            )
                        }
                    }
                }

                // Expand indicator
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = if (isExpanded) FeatherIcons.ChevronUp else FeatherIcons.ChevronDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.size(16.dp),
                        tint = TextTertiary,
                    )
                }
            }
        }
    }
}
