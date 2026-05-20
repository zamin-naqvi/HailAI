package com.aeroloomstudio.hailai.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val HailShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

// Custom shapes for specific components
val PillShape = RoundedCornerShape(50)
val ChatBubbleUserShape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
val ChatBubbleAiShape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
val CardShape = RoundedCornerShape(20.dp)
val InputBarShape = RoundedCornerShape(28.dp)
val ChipShape = RoundedCornerShape(20.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
