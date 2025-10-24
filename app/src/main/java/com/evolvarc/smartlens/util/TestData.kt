package com.evolvarc.smartlens.util

import com.evolvarc.smartlens.domain.model.*

object TestData {
    
    fun getSampleFoodProduct() = Product(
        barcode = "8901058856507",
        name = "Maggi 2-Minute Noodles",
        brands = "Maggi",
        imageUrl = "https://images.openfoodfacts.org/images/products/890/105/885/6507/front_en.3.400.jpg",
        categories = "instant-noodles",
        ingredientsText = "Refined Wheat Flour (Maida), Palm Oil, Salt, Wheat Gluten, Thickeners (508 & 412), Acidity Regulators (501(i) & 500(i)), Humectant (451(i))",
        category = ProductCategory.FOOD,
        nutritionData = NutritionData(
            sugars100g = 5.8,
            salt100g = 2.98,
            saturatedFat100g = 7.2,
            proteins100g = 9.6,
            fiber100g = 2.0,
            energyKcal100g = 400.0,
            nutriScoreGrade = "d",
            novaGroup = 4
        ),
        beautyData = null,
        healthScore = 34,
        warnings = listOf("⚠️ HIGH SUGAR", "⚠️ HIGH SALT", "⚠️ ULTRA-PROCESSED")
    )
    
    fun getSampleBeautyProduct() = Product(
        barcode = "3600523213900",
        name = "Pantene Pro-V Shampoo",
        brands = "Pantene",
        imageUrl = null,
        categories = "shampoos",
        ingredientsText = "Water, Sodium Laureth Sulfate, Sodium Citrate, Cocamidopropyl Betaine, Sodium Xylenesulfonate, Fragrance, Methylparaben",
        category = ProductCategory.PERSONAL_CARE,
        nutritionData = null,
        beautyData = BeautyData(
            harmfulIngredients = listOf("Sodium Laureth Sulfate", "Methylparaben", "Fragrance"),
            allergens = listOf("Fragrance"),
            isVegan = false,
            isCrueltyFree = false,
            isParabenFree = false,
            isSulfateFree = false
        ),
        healthScore = 52,
        warnings = listOf("⚠️ CONTAINS SULFATES", "⚠️ CONTAINS PARABENS", "👃 SYNTHETIC FRAGRANCE")
    )
    
    fun getSampleAlternative() = Alternative(
        product = Product(
            barcode = "8901234567890",
            name = "Yippee Noodles",
            brands = "Yippee",
            imageUrl = null,
            categories = "instant-noodles",
            ingredientsText = "Refined Wheat Flour, Palm Oil, Salt, Spices",
            category = ProductCategory.FOOD,
            nutritionData = NutritionData(
                sugars100g = 3.2,
                salt100g = 1.8,
                saturatedFat100g = 4.5,
                proteins100g = 10.2,
                fiber100g = 2.5,
                energyKcal100g = 380.0,
                nutriScoreGrade = "c",
                novaGroup = 4
            ),
            beautyData = null,
            healthScore = 58,
            warnings = listOf()
        ),
        improvementReason = "45% less sugar, 40% less salt",
        scoreDifference = 24
    )
}
