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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import com.aeroloomstudio.hailai.ui.theme.*

@Composable
fun FloatingInputBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSend: () -> Unit,
    onPlusClick: () -> Unit,
    isProcessing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val hasText = value.text.isNotBlank()

    // Fully rounded white pill with thin border
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .border(1.dp, DividerColor.copy(alpha = 0.5f), PillShape)
            .clip(PillShape)
            .background(SurfaceWhite)
            .padding(start = 8.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Plus button — with spacing from edge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onPlusClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = FeatherIcons.Plus,
                contentDescription = "Add attachment",
                modifier = Modifier.size(20.dp),
                tint = TextSecondary,
            )
        }

        // Text field
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Ask Hail AI",
                    color = TextTertiary,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = HailBlue,
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            singleLine = false,
            maxLines = 4,
        )

        Spacer(Modifier.width(4.dp))

        // Send button — always visible, with spacing from edge
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, TextPrimary, CircleShape)
                    .clickable(onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = FeatherIcons.Square,
                    contentDescription = "Stop",
                    modifier = Modifier.size(14.dp),
                    tint = TextPrimary,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (hasText) HailBlue else SurfaceDim)
                    .clickable(enabled = hasText, onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = FeatherIcons.ArrowUp,
                    contentDescription = "Send",
                    modifier = Modifier.size(18.dp),
                    tint = if (hasText) TextOnBlue else TextTertiary,
                )
            }
        }
    }
}
