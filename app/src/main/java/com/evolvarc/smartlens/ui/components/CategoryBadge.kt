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
import com.evolvarc.smartlens.domain.model.ProductCategory
import com.evolvarc.smartlens.util.getCategoryColor
import com.evolvarc.smartlens.util.getCategoryLabel

@Composable
fun CategoryBadge(
    category: ProductCategory,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(category)
    val categoryLabel = getCategoryLabel(category)
    
    Text(
        text = categoryLabel,
        modifier = modifier
            .background(
                color = categoryColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = categoryColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
}
