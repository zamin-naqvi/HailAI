package com.aeroloomstudio.hailai.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onLicensesClick: () -> Unit = {},
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
                text = "About",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── App icon / branding card ─────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // App icon badge with gradient
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    HailBlue,
                                    HailBlueDark,
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "H",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 36.sp,
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Hail AI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Your intelligent assistant for finding and booking local service providers — from plumbers to tutors, in seconds.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Developer info card ──────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column {
                AboutItem(
                    icon = FeatherIcons.Code,
                    title = "Developer",
                    subtitle = "AeroLoom Studio",
                )
                AboutDivider()
                AboutItem(
                    icon = FeatherIcons.Mail,
                    title = "Contact",
                    subtitle = "aeroloomstudio@gmail.com",
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Legal card ───────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = CardShape,
            color = SurfaceWhite,
        ) {
            Column {
                AboutItem(
                    icon = FeatherIcons.FileText,
                    title = "Terms of Service",
                    subtitle = "View terms and conditions",
                    onClick = onTermsClick,
                )
                AboutDivider()
                AboutItem(
                    icon = FeatherIcons.Shield,
                    title = "Privacy Policy",
                    subtitle = "How we handle your data",
                    onClick = onPrivacyClick,
                )
                AboutDivider()
                AboutItem(
                    icon = FeatherIcons.Award,
                    title = "Licenses",
                    subtitle = "Open-source libraries used",
                    onClick = onLicensesClick,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Footer ──────────────────────────────────────────────────────────
        Text(
            text = "Made with ❤\uFE0F in Pakistan",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )
        Text(
            text = "© 2025 AeroLoom Studio. All rights reserved.",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        )
    }
}

@Composable
private fun AboutItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
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
private fun AboutDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = DividerColor.copy(alpha = 0.5f),
    )
}
