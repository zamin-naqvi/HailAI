package com.aeroloomstudio.hailai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.aeroloomstudio.hailai.data.model.Provider
import com.aeroloomstudio.hailai.data.model.displayCategory
import com.aeroloomstudio.hailai.data.model.priceRange
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun ProviderCard(
    provider: Provider,
    onClick: () -> Unit = {},
    showBookButton: Boolean = true,
    isBooked: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DividerColor.copy(alpha = 0.35f), CardShape),
        shape = CardShape,
        color = SurfaceWhite,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Header row: name + verified badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Text(
                        text = provider.displayCategory(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }

                if (provider.verified) {
                    Surface(
                        shape = PillShape,
                        color = StatusGreenLight,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = FeatherIcons.CheckCircle,
                                contentDescription = "Verified",
                                modifier = Modifier.size(12.dp),
                                tint = StatusGreen,
                            )
                            Text(
                                text = "Verified",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusGreen,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(14.dp),
                        tint = StarFilled,
                    )
                    Text(
                        text = "${provider.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "(${provider.totalReviews})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                    )
                }

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.MapPin,
                        contentDescription = "Location",
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary,
                    )
                    Text(
                        text = "${provider.area}, ${provider.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            // Price + experience row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.DollarSign,
                        contentDescription = "Price",
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary,
                    )
                    Text(
                        text = provider.priceRange(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FeatherIcons.Award,
                        contentDescription = "Experience",
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary,
                    )
                    Text(
                        text = "${provider.experienceYears} yrs exp",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            // Available slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                provider.availableSlots.take(4).forEach { slot ->
                    Surface(
                        shape = PillShape,
                        color = HailBlueSoft,
                    ) {
                        Text(
                            text = slot,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = HailBlue,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
                if (provider.availableSlots.size > 4) {
                    Surface(
                        shape = PillShape,
                        color = SurfaceCard,
                    ) {
                        Text(
                            text = "+${provider.availableSlots.size - 4}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                    }
                }
            }

            // Book button
            if (showBookButton) {
                if (isBooked) {
                    // Already booked — show disabled green button
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = StatusGreenLight,
                            disabledContentColor = StatusGreen,
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                    ) {
                        Icon(
                            imageVector = FeatherIcons.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Booked",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                } else {
                    Button(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HailBlue,
                            contentColor = TextOnBlue,
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Calendar,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Book Now",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
