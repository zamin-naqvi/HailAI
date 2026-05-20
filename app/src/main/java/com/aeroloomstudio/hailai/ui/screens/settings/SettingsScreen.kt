package com.aeroloomstudio.hailai.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.ui.theme.*

// ── Bottom sheet shape matching HailBottomSheet ──────────────────────────────
private val SheetShape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showNotificationSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceLight) // Grey background
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
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
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
        }

        // Profile card — white rounded
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(HailBlue),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "S",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextOnBlue,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column {
                    Text(
                        text = "Syed Zaman",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "zaman@aeroloom.studio",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // General settings — white card with dividers
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column {
                SettingsItem(
                    icon = FeatherIcons.Globe,
                    title = "Language",
                    subtitle = "English, Urdu, Roman Urdu",
                    onClick = { showLanguageSheet = true },
                )
                SettingsDivider()
                SettingsItem(
                    icon = FeatherIcons.MapPin,
                    title = "Default Location",
                    subtitle = "Islamabad",
                    onClick = onLocationClick,
                )
                SettingsDivider()
                SettingsItem(
                    icon = FeatherIcons.Bell,
                    title = "Notifications",
                    subtitle = "Push notifications enabled",
                    onClick = { showNotificationSheet = true },
                )
                SettingsDivider()
                SettingsItem(
                    icon = FeatherIcons.FileText,
                    title = "Terms of Service",
                    subtitle = "Rules & guidelines",
                    onClick = onTermsClick,
                )
                SettingsDivider()
                SettingsItem(
                    icon = FeatherIcons.Shield,
                    title = "Privacy Policy",
                    subtitle = "Data & permissions",
                    onClick = onPrivacyClick,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // About section — another white card with dividers
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column {
                SettingsItem(
                    icon = FeatherIcons.Info,
                    title = "About Hail AI",
                    subtitle = "Version 1.0 · AeroLoom Studio",
                    onClick = onAboutClick,
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // ── Language Bottom Sheet ─────────────────────────────────────────────────
    if (showLanguageSheet) {
        LanguageBottomSheet(onDismiss = { showLanguageSheet = false })
    }

    // ── Notification Bottom Sheet ─────────────────────────────────────────────
    if (showNotificationSheet) {
        NotificationBottomSheet(onDismiss = { showNotificationSheet = false })
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Language Bottom Sheet — same styling as HailBottomSheet
// ═════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageBottomSheet(onDismiss: () -> Unit) {
    var selectedLanguage by remember { mutableStateOf("English") }
    val languages = listOf(
        "English" to "Default language",
        "Urdu" to "اردو",
        "Roman Urdu" to "Roman script mein Urdu",
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = SurfaceWhite,
        shape = SheetShape,
        dragHandle = { SheetDragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "App Language",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )

            Text(
                text = "Select your preferred language for the app",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            )

            Spacer(Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = CardShape,
                color = SurfaceWhite,
            ) {
                Column {
                    languages.forEachIndexed { index, (lang, subtitle) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLanguage = lang }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                imageVector = FeatherIcons.Globe,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (lang == selectedLanguage) HailBlue else TextTertiary,
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (lang == selectedLanguage) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (lang == selectedLanguage) HailBlue else TextPrimary,
                                )
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                            RadioButton(
                                selected = lang == selectedLanguage,
                                onClick = { selectedLanguage = lang },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = HailBlue,
                                    unselectedColor = TextTertiary,
                                ),
                            )
                        }
                        if (index < languages.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = DividerColor.copy(alpha = 0.5f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HailBlue,
                    contentColor = TextOnBlue,
                ),
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Notification Bottom Sheet — same styling as HailBottomSheet
// ═════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationBottomSheet(onDismiss: () -> Unit) {
    var pushEnabled by remember { mutableStateOf(true) }
    var bookingReminders by remember { mutableStateOf(true) }
    var promotional by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = SurfaceWhite,
        shape = SheetShape,
        dragHandle = { SheetDragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )

            Text(
                text = "Choose which notifications you'd like to receive",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            )

            Spacer(Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = CardShape,
                color = SurfaceWhite,
            ) {
                Column {
                    NotificationToggle(
                        icon = FeatherIcons.Bell,
                        title = "Push Notifications",
                        subtitle = "Receive real-time booking updates",
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = DividerColor.copy(alpha = 0.5f),
                    )
                    NotificationToggle(
                        icon = FeatherIcons.Clock,
                        title = "Booking Reminders",
                        subtitle = "Get reminded before appointments",
                        checked = bookingReminders,
                        onCheckedChange = { bookingReminders = it },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = DividerColor.copy(alpha = 0.5f),
                    )
                    NotificationToggle(
                        icon = FeatherIcons.Star,
                        title = "Promotions & Offers",
                        subtitle = "Deals from service providers",
                        checked = promotional,
                        onCheckedChange = { promotional = it },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HailBlue,
                    contentColor = TextOnBlue,
                ),
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun NotificationToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
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
            onCheckedChange = onCheckedChange,
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

// ═════════════════════════════════════════════════════════════════════════════
//  Shared Components
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun SheetDragHandle() {
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
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
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
        Icon(
            imageVector = FeatherIcons.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = TextTertiary,
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = DividerColor.copy(alpha = 0.5f),
    )
}
