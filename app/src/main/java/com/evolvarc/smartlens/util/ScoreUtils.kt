package com.evolvarc.smartlens.util

import androidx.compose.ui.graphics.Color
import com.evolvarc.smartlens.domain.model.ProductCategory
import com.evolvarc.smartlens.ui.theme.*

fun getScoreColor(score: Int): Color {
    return when {
        score >= 80 -> ExcellentGreen
        score >= 60 -> GoodLightGreen
        score >= 40 -> AverageYellow
        score >= 20 -> PoorOrange
        else -> VeryPoorRed
    }
}

fun getScoreLabel(score: Int): String {
    return when {
        score >= 80 -> "Excellent"
        score >= 60 -> "Good"
        score >= 40 -> "Average"
        score >= 20 -> "Poor"
        else -> "Very Poor"
    }
}

fun getCategoryColor(category: ProductCategory): Color {
    return when (category) {
        ProductCategory.FOOD -> FoodOrange
        ProductCategory.BEAUTY -> BeautyPink
        ProductCategory.PERSONAL_CARE -> PersonalCareBlue
        else -> PrimaryBlue
    }
}

fun getCategoryLabel(category: ProductCategory): String {
    return when (category) {
        ProductCategory.FOOD -> "🍕 Food"
        ProductCategory.BEAUTY -> "💄 Beauty"
        ProductCategory.PERSONAL_CARE -> "🧴 Personal Care"
        ProductCategory.GENERAL -> "📦 General"
        ProductCategory.UNKNOWN -> "❓ Unknown"
    }
}
