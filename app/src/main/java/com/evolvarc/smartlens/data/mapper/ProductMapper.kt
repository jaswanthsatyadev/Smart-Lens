package com.evolvarc.smartlens.data.mapper

import com.evolvarc.smartlens.data.local.entity.ProductEntity
import com.evolvarc.smartlens.data.remote.dto.OpenBeautyFactsProduct
import com.evolvarc.smartlens.data.remote.dto.OpenFoodFactsProduct
import com.evolvarc.smartlens.data.remote.dto.OpenProductsFactsProduct
import com.evolvarc.smartlens.domain.model.*

fun OpenFoodFactsProduct.toDomainModel(): Product {
    val nutritionData = nutriments?.let {
        NutritionData(
            sugars100g = it.sugars100g,
            salt100g = it.salt100g,
            saturatedFat100g = it.saturatedFat100g,
            proteins100g = it.proteins100g,
            fiber100g = it.fiber100g,
            energyKcal100g = it.energyKcal100g,
            nutriScoreGrade = nutriscoreGrade,
            novaGroup = novaGroup
        )
    }
    
    return Product(
        barcode = code ?: "",
        name = productName ?: "Unknown Product",
        brands = brands,
        imageUrl = imageUrl,
        categories = categories,
        ingredientsText = ingredientsText,
        category = ProductCategory.FOOD,
        nutritionData = nutritionData,
        beautyData = null
    )
}

fun OpenBeautyFactsProduct.toDomainModel(): Product {
    val harmfulIngredientsList = ingredientsText?.let { text ->
        com.evolvarc.smartlens.util.Constants.HARMFUL_INGREDIENTS
            .filter { harmful -> text.contains(harmful, ignoreCase = true) }
    } ?: emptyList()
    
    val isVegan = ingredientsAnalysisTags?.contains("en:vegan") ?: false
    val isParabenFree = !harmfulIngredientsList.any { it.contains("paraben", ignoreCase = true) }
    val isSulfateFree = !harmfulIngredientsList.any { it.contains("sulfate", ignoreCase = true) }
    
    val beautyData = BeautyData(
        harmfulIngredients = harmfulIngredientsList.ifEmpty { null },
        allergens = allergens?.split(",")?.map { it.trim() },
        isVegan = isVegan,
        isCrueltyFree = null,
        isParabenFree = isParabenFree,
        isSulfateFree = isSulfateFree
    )
    
    val category = categories?.lowercase()?.let {
        when {
            it.contains("shampoo") || it.contains("soap") || it.contains("deodorant") -> ProductCategory.PERSONAL_CARE
            else -> ProductCategory.BEAUTY
        }
    } ?: ProductCategory.BEAUTY
    
    return Product(
        barcode = code ?: "",
        name = productName ?: "Unknown Product",
        brands = brands,
        imageUrl = imageUrl,
        categories = categories,
        ingredientsText = ingredientsText,
        category = category,
        nutritionData = null,
        beautyData = beautyData
    )
}

fun OpenProductsFactsProduct.toDomainModel(): Product {
    return Product(
        barcode = code ?: "",
        name = productName ?: "Unknown Product",
        brands = brands,
        imageUrl = imageUrl,
        categories = categories,
        ingredientsText = ingredientsText,
        category = ProductCategory.GENERAL,
        nutritionData = null,
        beautyData = null
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        barcode = barcode,
        name = name,
        brands = brands,
        imageUrl = imageUrl,
        categories = categories,
        ingredientsText = ingredientsText,
        category = category.name,
        sugars100g = nutritionData?.sugars100g,
        salt100g = nutritionData?.salt100g,
        saturatedFat100g = nutritionData?.saturatedFat100g,
        proteins100g = nutritionData?.proteins100g,
        fiber100g = nutritionData?.fiber100g,
        energyKcal100g = nutritionData?.energyKcal100g,
        nutriScoreGrade = nutritionData?.nutriScoreGrade,
        novaGroup = nutritionData?.novaGroup,
        harmfulIngredients = beautyData?.harmfulIngredients?.joinToString(","),
        allergens = beautyData?.allergens?.joinToString(","),
        isVegan = beautyData?.isVegan,
        isCrueltyFree = beautyData?.isCrueltyFree,
        isParabenFree = beautyData?.isParabenFree,
        isSulfateFree = beautyData?.isSulfateFree,
        healthScore = healthScore,
        warnings = warnings.joinToString("|"),
        scannedAt = scannedAt,
        cachedAt = cachedAt
    )
}

fun ProductEntity.toDomainModel(): Product {
    val nutritionData = if (category == ProductCategory.FOOD.name) {
        NutritionData(
            sugars100g = sugars100g,
            salt100g = salt100g,
            saturatedFat100g = saturatedFat100g,
            proteins100g = proteins100g,
            fiber100g = fiber100g,
            energyKcal100g = energyKcal100g,
            nutriScoreGrade = nutriScoreGrade,
            novaGroup = novaGroup
        )
    } else null
    
    val beautyData = if (category == ProductCategory.BEAUTY.name || category == ProductCategory.PERSONAL_CARE.name) {
        BeautyData(
            harmfulIngredients = harmfulIngredients?.split(",")?.filter { it.isNotBlank() },
            allergens = allergens?.split(",")?.filter { it.isNotBlank() },
            isVegan = isVegan,
            isCrueltyFree = isCrueltyFree,
            isParabenFree = isParabenFree,
            isSulfateFree = isSulfateFree
        )
    } else null
    
    return Product(
        barcode = barcode,
        name = name,
        brands = brands,
        imageUrl = imageUrl,
        categories = categories,
        ingredientsText = ingredientsText,
        category = ProductCategory.valueOf(category),
        nutritionData = nutritionData,
        beautyData = beautyData,
        healthScore = healthScore,
        warnings = warnings.split("|").filter { it.isNotBlank() },
        scannedAt = scannedAt,
        cachedAt = cachedAt
    )
}
