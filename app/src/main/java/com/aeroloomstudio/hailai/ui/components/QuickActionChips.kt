package com.aeroloomstudio.hailai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeroloomstudio.hailai.ui.theme.*

data class ServiceChip(
    val label: String,
    val category: String,
    val emoji: String,
)

val defaultServiceChips = listOf(
    ServiceChip("AC Tech", "hvac", "❄️"),
    ServiceChip("Plumber", "plumbing", "🔧"),
    ServiceChip("Electrician", "electrical", "⚡"),
    ServiceChip("Cleaner", "cleaning", "🧹"),
    ServiceChip("Tutor", "tutoring", "📚"),
    ServiceChip("Carpenter", "carpentry", "🪚"),
)

private val gridChips = defaultServiceChips.take(6)

/**
 * Bento grid — 2 columns, compact cards, white bg with corner gradients
 */
@Composable
fun QuickActionChips(
    onChipClick: (ServiceChip) -> Unit,
    onSeeMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        gridChips.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { chip ->
                    BentoCard(
                        chip = chip,
                        onClick = { onChipClick(chip) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        // "See More" button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CardShape,
            color = SurfaceWhite,
            onClick = onSeeMoreClick,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                HailBlueSoft,
                                SurfaceWhite,
                                Color(0xFFF3E8FF),
                            ),
                        ),
                        shape = CardShape,
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "See More Services →",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = HailBlue,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun BentoCard(
    chip: ServiceChip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CardShape,
        color = SurfaceWhite,
        onClick = onClick,
    ) {
        Box {
            // Subtle corner gradient
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CardShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                HailBlueSoft.copy(alpha = 0.4f),
                                Color.Transparent,
                            ),
                            radius = 180f,
                        ),
                    )
            )
            // Compact content — no extra space
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = chip.emoji, fontSize = 18.sp)
                Text(
                    text = chip.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                )
            }
        }
    }
}
