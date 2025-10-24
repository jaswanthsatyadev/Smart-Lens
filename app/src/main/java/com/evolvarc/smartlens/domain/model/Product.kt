package com.evolvarc.smartlens.domain.model

import com.evolvarc.smartlens.domain.usecase.CalculateHealthScoreUseCase

data class Product(
    val barcode: String,
    val name: String,
    val brands: String?,
    val imageUrl: String?,
    val categories: String?,
    val ingredientsText: String?,
    val category: ProductCategory,
    
    val nutritionData: NutritionData? = null,
    val beautyData: BeautyData? = null,
    
    val healthScore: Int = 0,
    val dataAvailability: CalculateHealthScoreUseCase.DataAvailability = CalculateHealthScoreUseCase.DataAvailability.INSUFFICIENT,
    val warnings: List<String> = emptyList(),
    val scannedAt: Long = System.currentTimeMillis(),
    val cachedAt: Long = System.currentTimeMillis()
)

data class NutritionData(
    val sugars100g: Double?,
    val salt100g: Double?,
    val saturatedFat100g: Double?,
    val proteins100g: Double?,
    val fiber100g: Double?,
    val energyKcal100g: Double?,
    val nutriScoreGrade: String?,
    val novaGroup: Int?
)

data class BeautyData(
    val harmfulIngredients: List<String>?,
    val allergens: List<String>?,
    val isVegan: Boolean?,
    val isCrueltyFree: Boolean?,
    val isParabenFree: Boolean?,
    val isSulfateFree: Boolean?
)

enum class ProductCategory {
    FOOD,
    BEAUTY,
    PERSONAL_CARE,
    GENERAL,
    UNKNOWN
}
