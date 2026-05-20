package com.aeroloomstudio.hailai.ui.screens.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import com.aeroloomstudio.hailai.ui.theme.SurfaceLight
import com.aeroloomstudio.hailai.ui.theme.TextPrimary
import com.aeroloomstudio.hailai.ui.theme.TextSecondary

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceLight)
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
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Last updated: May 20, 2026",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Text(
                text = "1. Information We Collect",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "We collect information you provide directly to us, such as when you create or modify your account, request on-demand services, contact customer support, or otherwise communicate with us.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Text(
                text = "2. How We Use Your Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "We may use the information we collect about you to provide, maintain, and improve our services, including to facilitate payments, send receipts, and provide customer support.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Text(
                text = "3. Sharing of Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "We may share the information we collect about you with third-party vendors, consultants, and other service providers who need access to such information to carry out work on our behalf.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
