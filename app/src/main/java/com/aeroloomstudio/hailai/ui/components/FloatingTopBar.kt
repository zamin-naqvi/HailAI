package com.aeroloomstudio.hailai.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun FloatingTopBar(
    onMenuClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onModelClick: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDropdown by remember { mutableStateOf(false) }

    // Very subtle border color
    val subtleBorder = DividerColor.copy(alpha = 0.35f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Hamburger menu — white bg + subtle outline
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(0.8.dp, subtleBorder, CircleShape)
                .clickable(onClick = onMenuClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = FeatherIcons.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(22.dp),
                tint = TextPrimary,
            )
        }

        // App name pill — WHITE bg with subtle outline
        Surface(
            shape = PillShape,
            color = SurfaceWhite,
            modifier = Modifier.border(0.8.dp, subtleBorder, PillShape),
            onClick = onModelClick,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "Hail AI",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Icon(
                    imageVector = FeatherIcons.ChevronDown,
                    contentDescription = "Model selector",
                    modifier = Modifier.size(16.dp),
                    tint = TextSecondary,
                )
            }
        }

        // Action buttons — subtle outlines
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(0.8.dp, subtleBorder, CircleShape)
                    .clickable(onClick = onNewChatClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = FeatherIcons.Edit2,
                    contentDescription = "New chat",
                    modifier = Modifier.size(20.dp),
                    tint = TextPrimary,
                )
            }

            Box {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(0.8.dp, subtleBorder, CircleShape)
                        .clickable { showDropdown = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = FeatherIcons.MoreHorizontal,
                        contentDescription = "More options",
                        modifier = Modifier.size(20.dp),
                        tint = TextPrimary,
                    )
                }

                // Dropdown menu — more rounded
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    offset = DpOffset(0.dp, 4.dp),
                    containerColor = SurfaceWhite,
                    shape = RoundedCornerShape(20.dp),
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(FeatherIcons.Calendar, null, Modifier.size(18.dp), TextPrimary)
                                Text("My Bookings", color = TextPrimary)
                            }
                        },
                        onClick = { showDropdown = false; onNavigateToBookings() },
                    )
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(FeatherIcons.Settings, null, Modifier.size(18.dp), TextPrimary)
                                Text("Settings", color = TextPrimary)
                            }
                        },
                        onClick = { showDropdown = false; onNavigateToSettings() },
                    )
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(FeatherIcons.HelpCircle, null, Modifier.size(18.dp), TextPrimary)
                                Text("About Hail AI", color = TextPrimary)
                            }
                        },
                        onClick = { showDropdown = false; onNavigateToAbout() },
                    )
                }
            }
        }
    }
}
