package com.evolvarc.smartlens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolvarc.smartlens.ui.theme.VeryPoorRed

@Composable
fun WarningBadge(
    warning: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = warning,
        modifier = modifier
            .background(
                color = VeryPoorRed.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = VeryPoorRed,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )
}
