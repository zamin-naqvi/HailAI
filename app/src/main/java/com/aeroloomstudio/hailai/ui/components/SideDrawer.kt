package com.aeroloomstudio.hailai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.ui.theme.*

data class DrawerItem(
    val title: String,
    val isSelected: Boolean = false,
)

@Composable
fun SideDrawerContent(
    recentChats: List<DrawerItem>,
    onChatClick: (Int) -> Unit,
    onNewChat: () -> Unit,
    onBookingsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(SurfaceWhite)
            .padding(top = 48.dp),
    ) {
        // Header
        Text(
            text = "Hail AI",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )

        Spacer(Modifier.height(8.dp))

        // New Chat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNewChat)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = FeatherIcons.Plus,
                contentDescription = "New chat",
                modifier = Modifier.size(20.dp),
                tint = TextPrimary,
            )
            Text(
                text = "New chat",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
            )
        }

        // My Bookings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBookingsClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = FeatherIcons.Calendar,
                contentDescription = "Bookings",
                modifier = Modifier.size(20.dp),
                tint = TextPrimary,
            )
            Text(
                text = "My Bookings",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            color = DividerColor,
        )

        // Recent section
        Text(
            text = "Recent",
            style = MaterialTheme.typography.labelMedium,
            color = TextTertiary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            itemsIndexed(recentChats) { index, chat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChatClick(index) }
                        .background(if (chat.isSelected) HailBlueSoft else SurfaceWhite)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.MessageSquare,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (chat.isSelected) HailBlue else TextTertiary,
                    )
                    Text(
                        text = chat.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (chat.isSelected) HailBlue else TextPrimary,
                        maxLines = 1,
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = DividerColor,
        )

        // Bottom section — User + Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSettingsClick)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(HailBlue),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "S",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextOnBlue,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Syed Zaman",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                )
            }
            Icon(
                imageVector = FeatherIcons.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(20.dp),
                tint = TextSecondary,
            )
        }
    }
}
