package com.evolvarc.smartlens.data.mapper

import com.evolvarc.smartlens.data.remote.dto.UsdaFood
import com.evolvarc.smartlens.data.remote.dto.UsdaFoodDetailResponse
import com.evolvarc.smartlens.domain.model.NutritionData
import com.evolvarc.smartlens.domain.model.Product
import com.evolvarc.smartlens.domain.model.ProductCategory

fun UsdaFood.toDomainModel(): Product {
    val nutritionData = extractNutritionData()
    
    return Product(
        barcode = gtinUpc ?: fdcId?.toString() ?: "",
        name = description ?: "Unknown Product",
        brands = brandOwner ?: brandName,
        imageUrl = null, // USDA doesn't provide images
        categories = foodCategory,
        ingredientsText = ingredients,
        category = ProductCategory.FOOD,
        nutritionData = nutritionData,
        beautyData = null,
        allergens = extractAllergensFromIngredients(ingredients)
    )
}

fun UsdaFoodDetailResponse.toDomainModel(): Product {
    val nutritionData = extractDetailedNutritionData()
    
    return Product(
        barcode = gtinUpc ?: fdcId?.toString() ?: "",
        name = description ?: "Unknown Product",
        brands = brandOwner ?: brandName,
        imageUrl = null,
        categories = foodCategory,
        ingredientsText = ingredients,
        category = ProductCategory.FOOD,
        nutritionData = nutritionData,
        beautyData = null,
        allergens = extractAllergensFromIngredients(ingredients)
    )
}

private fun UsdaFood.extractNutritionData(): NutritionData? {
    val nutrients = foodNutrients ?: return null
    
    // Extract key nutrients from the list
    fun getNutrientValue(name: String): Double? {
        return nutrients.find { 
            it.nutrientName?.contains(name, ignoreCase = true) == true 
        }?.value
    }
    
    fun getNutrientValueById(id: Int): Double? {
        return nutrients.find { it.nutrientId == id }?.value
    }
    
    val energyKcal = getNutrientValueById(1008) // Energy
    val protein = getNutrientValueById(1003) // Protein
    val fat = getNutrientValueById(1004) // Total fat
    val saturatedFat = getNutrientValueById(1258) // Saturated fat
    val fiber = getNutrientValueById(1079) // Fiber
    val sugars = getNutrientValueById(2000) // Total sugars
    val sodium = getNutrientValueById(1093) // Sodium
    
    // Convert sodium from mg to g (OpenFoodFacts uses g)
    val saltInGrams = sodium?.let { it / 1000.0 }
    
    // Convert per serving to per 100g if serving size is provided
    val servingSizeInGrams = if (servingSizeUnit?.contains("g", ignoreCase = true) == true) {
        servingSize
    } else {
        100.0 // Default to 100g
    }
    
    val multiplier = servingSizeInGrams?.let { 100.0 / it } ?: 1.0
    
    return NutritionData(
        sugars100g = sugars?.times(multiplier),
        salt100g = saltInGrams?.times(multiplier),
        saturatedFat100g = saturatedFat?.times(multiplier),
        proteins100g = protein?.times(multiplier),
        fiber100g = fiber?.times(multiplier),
        energyKcal100g = energyKcal?.times(multiplier),
        nutriScoreGrade = null, // USDA doesn't provide NutriScore
        novaGroup = null // USDA doesn't provide NOVA group
    )
}

private fun UsdaFoodDetailResponse.extractDetailedNutritionData(): NutritionData? {
    // Try label nutrients first (more accurate for branded foods)
    labelNutrients?.let { label ->
        val energyKcal = label.calories?.value
        val protein = label.protein?.value
        val saturatedFat = label.saturatedFat?.value
        val fiber = label.fiber?.value
        val sugars = label.sugars?.value
        val sodium = label.sodium?.value
        
        // Convert sodium from mg to g
        val saltInGrams = sodium?.let { it / 1000.0 }
        
        // Label nutrients are already per serving, convert to per 100g
        val servingSizeInGrams = if (servingSizeUnit?.contains("g", ignoreCase = true) == true) {
            servingSize
        } else {
            100.0
        }
        
        val multiplier = servingSizeInGrams?.let { 100.0 / it } ?: 1.0
        
        return NutritionData(
            sugars100g = sugars?.times(multiplier),
            salt100g = saltInGrams?.times(multiplier),
            saturatedFat100g = saturatedFat?.times(multiplier),
            proteins100g = protein?.times(multiplier),
            fiber100g = fiber?.times(multiplier),
            energyKcal100g = energyKcal?.times(multiplier),
            nutriScoreGrade = null,
            novaGroup = null
        )
    }
    
    // Fallback to foodNutrients if label nutrients not available
    val nutrients = foodNutrients ?: return null
    
    fun getNutrientValueById(id: Int): Double? {
        return nutrients.find { it.nutrientId == id }?.value
    }
    
    val energyKcal = getNutrientValueById(1008)
    val protein = getNutrientValueById(1003)
    val saturatedFat = getNutrientValueById(1258)
    val fiber = getNutrientValueById(1079)
    val sugars = getNutrientValueById(2000)
    val sodium = getNutrientValueById(1093)
    
    val saltInGrams = sodium?.let { it / 1000.0 }
    
    val servingSizeInGrams = if (servingSizeUnit?.contains("g", ignoreCase = true) == true) {
        servingSize
    } else {
        100.0
    }
    
    val multiplier = servingSizeInGrams?.let { 100.0 / it } ?: 1.0
    
    return NutritionData(
        sugars100g = sugars?.times(multiplier),
        salt100g = saltInGrams?.times(multiplier),
        saturatedFat100g = saturatedFat?.times(multiplier),
        proteins100g = protein?.times(multiplier),
        fiber100g = fiber?.times(multiplier),
        energyKcal100g = energyKcal?.times(multiplier),
        nutriScoreGrade = null,
        novaGroup = null
    )
}

private fun extractAllergensFromIngredients(ingredients: String?): List<String> {
    if (ingredients == null) return emptyList()
    
    val commonAllergens = listOf(
        "milk", "egg", "fish", "shellfish", "tree nut", "peanut", "wheat", "soybean",
        "sesame", "mustard", "celery", "lupin", "sulphite", "mollusc",
        "almond", "cashew", "walnut", "pecan", "pistachio", "hazelnut"
    )
    
    return commonAllergens.filter { allergen ->
        ingredients.contains(allergen, ignoreCase = true)
    }.map { it.replaceFirstChar { char -> char.uppercase() } }
}
