package com.aeroloomstudio.hailai.ui.screens.providers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.data.model.Provider
import com.aeroloomstudio.hailai.data.model.displayCategory
import com.aeroloomstudio.hailai.ui.components.*
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun ProviderSelectionScreen(
    providers: List<Provider>,
    serviceType: String,
    aiReasoning: String,
    onProviderSelect: (Provider) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GradientBackground(state = GradientState.DONE) {
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
                        text = "Top Matches",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "for $serviceType",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }

            // AI Reasoning card
            if (aiReasoning.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = CardShape,
                    color = HailBlueSoft,
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Zap,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = HailBlue,
                        )
                        Column {
                            Text(
                                text = "AI Recommendation",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = HailBlue,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = aiReasoning,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                            )
                        }
                    }
                }
            }

            // Provider list
            if (providers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Search,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = TextTertiary,
                        )
                        Text(
                            text = "No providers found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(providers, key = { it.id }) { provider ->
                        ProviderCard(
                            provider = provider,
                            onClick = { onProviderSelect(provider) },
                            showBookButton = true,
                        )
                    }
                }
            }

            // Bottom action
            if (providers.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceWhite,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                    ) {
                        Button(
                            onClick = { providers.firstOrNull()?.let(onProviderSelect) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HailBlue,
                                contentColor = TextOnBlue,
                            ),
                            contentPadding = PaddingValues(vertical = 14.dp),
                        ) {
                            Icon(
                                imageVector = FeatherIcons.Zap,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Book Top Pick",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}
