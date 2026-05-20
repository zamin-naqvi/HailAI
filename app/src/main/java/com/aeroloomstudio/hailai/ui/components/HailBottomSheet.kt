package com.aeroloomstudio.hailai.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.ui.theme.*

// More rounded bottom sheet shape
private val SheetShape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)
private val ServiceCardShape = RoundedCornerShape(20.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HailBottomSheet(
    onDismiss: () -> Unit,
    onServiceClick: (ServiceChip) -> Unit,
    onSettingsClick: () -> Unit,
    onBookingsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onTraceClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = SheetShape,
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
            // Top row — quick actions with pill shape
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                listOf(
                    Triple("Bookings", FeatherIcons.Calendar, onBookingsClick),
                    Triple("History", FeatherIcons.Clock, onHistoryClick),
                    Triple("Trace", FeatherIcons.Activity, onTraceClick),
                ).forEach { (label, icon, onClick) ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = PillShape,
                        color = SurfaceCard,
                        onClick = {
                            onClick()
                            onDismiss()
                        },
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(24.dp),
                                tint = TextPrimary,
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Service list — inside a white rounded card with subtle outline
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(
                        width = 0.8.dp,
                        color = Color(0xFF000000).copy(alpha = 0.07f),
                        shape = ServiceCardShape,
                    ),
                shape = ServiceCardShape,
                color = Color.White,
            ) {
                Column {
                    defaultServiceChips.forEachIndexed { index, chip ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onServiceClick(chip)
                                    onDismiss()
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(text = chip.emoji, fontSize = 22.sp)
                            Column {
                                Text(
                                    text = chip.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                )
                                Text(
                                    text = "Find ${chip.label.lowercase()} near you",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                        }
                        // Divider between items (not after last)
                        if (index < defaultServiceChips.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = DividerColor.copy(alpha = 0.5f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Settings — inside its own card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = CardShape,
                color = SurfaceWhite,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSettingsClick()
                            onDismiss()
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(22.dp),
                        tint = TextPrimary,
                    )
                    Column {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                        )
                        Text(
                            text = "Preferences & account",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
            }
        }
    }
}
