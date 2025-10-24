package com.evolvarc.smartlens.domain.usecase

import com.evolvarc.smartlens.domain.model.Product
import com.evolvarc.smartlens.domain.model.ProductCategory
import javax.inject.Inject

class GenerateWarningsUseCase @Inject constructor() {
    operator fun invoke(product: Product): List<String> {
        return when (product.category) {
            ProductCategory.FOOD -> generateFoodWarnings(product)
            ProductCategory.BEAUTY, ProductCategory.PERSONAL_CARE -> generateBeautyWarnings(product)
            else -> emptyList()
        }
    }
    
    private fun generateFoodWarnings(product: Product): List<String> {
        val warnings = mutableListOf<String>()
        val nutrition = product.nutritionData ?: return warnings
        
        nutrition.sugars100g?.let { sugar ->
            if (sugar > 15.0) warnings.add("⚠️ HIGH SUGAR")
        }
        
        nutrition.salt100g?.let { salt ->
            if (salt > 1.5) warnings.add("⚠️ HIGH SALT")
        }
        
        nutrition.saturatedFat100g?.let { fat ->
            if (fat > 5.0) warnings.add("⚠️ HIGH SATURATED FAT")
        }
        
        nutrition.novaGroup?.let { nova ->
            if (nova == 4) warnings.add("⚠️ ULTRA-PROCESSED")
        }
        
        return warnings
    }
    
    private fun generateBeautyWarnings(product: Product): List<String> {
        val warnings = mutableListOf<String>()
        val beauty = product.beautyData ?: return warnings
        
        beauty.harmfulIngredients?.let { harmful ->
            if (harmful.any { it.contains("paraben", ignoreCase = true) }) {
                warnings.add("⚠️ CONTAINS PARABENS")
            }
            if (harmful.any { it.contains("sulfate", ignoreCase = true) }) {
                warnings.add("⚠️ CONTAINS SULFATES")
            }
            if (harmful.any { it.contains("fragrance", ignoreCase = true) || it.contains("parfum", ignoreCase = true) }) {
                warnings.add("👃 SYNTHETIC FRAGRANCE")
            }
        }
        
        if (beauty.isCrueltyFree == false) {
            warnings.add("🐰 NOT CRUELTY-FREE")
        }
        
        return warnings
    }
}
