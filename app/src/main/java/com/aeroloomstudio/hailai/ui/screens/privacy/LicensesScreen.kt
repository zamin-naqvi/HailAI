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
fun LicensesScreen(
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
                text = "Open Source Licenses",
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
                text = "Hail AI uses the following open source libraries:",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            LicenseItem("Jetpack Compose", "Apache License 2.0")
            LicenseItem("Kotlin Standard Library", "Apache License 2.0")
            LicenseItem("Compose Feather Icons", "MIT License")
            LicenseItem("Kotlinx Coroutines", "Apache License 2.0")
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LicenseItem(name: String, license: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = license,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))
    }
}
