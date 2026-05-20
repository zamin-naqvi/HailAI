package com.aeroloomstudio.hailai.ui.screens.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceLight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
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
                text = "Privacy",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Data Collection ──────────────────────────────────────────────────
        Text(
            text = "Data Collection",
            style = MaterialTheme.typography.labelMedium,
            color = TextTertiary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column {
                PrivacyToggleItem(
                    icon = FeatherIcons.BarChart2,
                    title = "Usage Analytics",
                    subtitle = "Help improve Hail AI by sharing anonymous usage data",
                    defaultChecked = true,
                )
                PrivacyDivider()
                PrivacyToggleItem(
                    icon = FeatherIcons.Zap,
                    title = "Crash Reports",
                    subtitle = "Automatically send crash reports to help fix issues",
                    defaultChecked = true,
                )
                PrivacyDivider()
                PrivacyToggleItem(
                    icon = FeatherIcons.MessageSquare,
                    title = "Chat History",
                    subtitle = "Store conversations locally for quick access",
                    defaultChecked = true,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Location & Permissions ───────────────────────────────────────────
        Text(
            text = "Location & Permissions",
            style = MaterialTheme.typography.labelMedium,
            color = TextTertiary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column {
                PrivacyToggleItem(
                    icon = FeatherIcons.MapPin,
                    title = "Location Access",
                    subtitle = "Allow access to your location for nearby provider search",
                    defaultChecked = true,
                )
                PrivacyDivider()
                PrivacyToggleItem(
                    icon = FeatherIcons.Navigation,
                    title = "Background Location",
                    subtitle = "Track location in the background for booking reminders",
                    defaultChecked = false,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Account Data ────────────────────────────────────────────────────
        Text(
            text = "Account Data",
            style = MaterialTheme.typography.labelMedium,
            color = TextTertiary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column {
                PrivacyActionItem(
                    icon = FeatherIcons.Download,
                    title = "Download My Data",
                    subtitle = "Get a copy of all your data",
                )
                PrivacyDivider()
                PrivacyActionItem(
                    icon = FeatherIcons.Trash2,
                    title = "Clear Chat History",
                    subtitle = "Delete all local conversation data",
                    isDestructive = false,
                )
                PrivacyDivider()
                PrivacyActionItem(
                    icon = FeatherIcons.AlertTriangle,
                    title = "Delete Account",
                    subtitle = "Permanently delete your account and all data",
                    isDestructive = true,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column {
                PrivacyActionItem(
                    icon = FeatherIcons.FileText,
                    title = "Privacy Policy",
                    subtitle = "Read our full privacy policy",
                    onClick = onPrivacyPolicyClick,
                )
                PrivacyDivider()
                PrivacyActionItem(
                    icon = FeatherIcons.Shield,
                    title = "Terms of Service",
                    subtitle = "Read our terms and conditions",
                    onClick = onTermsClick,
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PrivacyToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    defaultChecked: Boolean,
) {
    var checked by remember { mutableStateOf(defaultChecked) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(22.dp),
            tint = TextSecondary,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = SurfaceWhite,
                checkedTrackColor = HailBlue,
                uncheckedThumbColor = SurfaceWhite,
                uncheckedTrackColor = SurfaceDim,
                uncheckedBorderColor = SurfaceDim,
            ),
        )
    }
}

@Composable
private fun PrivacyActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(22.dp),
            tint = if (isDestructive) StatusRed else TextSecondary,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) StatusRed else TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDestructive) StatusRed.copy(alpha = 0.7f) else TextSecondary,
            )
        }
        Icon(
            imageVector = FeatherIcons.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isDestructive) StatusRed.copy(alpha = 0.5f) else TextTertiary,
        )
    }
}

@Composable
private fun PrivacyDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = DividerColor.copy(alpha = 0.5f),
    )
}
