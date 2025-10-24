package com.evolvarc.smartlens.domain.usecase

import com.evolvarc.smartlens.data.repository.ProductRepository
import com.evolvarc.smartlens.domain.model.Alternative
import com.evolvarc.smartlens.domain.model.Product
import com.evolvarc.smartlens.domain.model.ProductCategory
import com.evolvarc.smartlens.domain.model.NutritionData
import javax.inject.Inject

class FindAlternativesUseCase @Inject constructor(
    private val calculateHealthScore: CalculateHealthScoreUseCase,
    private val repository: ProductRepository
) {
    suspend operator fun invoke(currentProduct: Product): Result<List<Alternative>> {
        return try {
            val alternatives = when (currentProduct.category) {
                ProductCategory.FOOD -> findFoodAlternatives(currentProduct)
                ProductCategory.BEAUTY, ProductCategory.PERSONAL_CARE -> findBeautyAlternatives(currentProduct)
                else -> emptyList()
            }
            
            Result.success(alternatives)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun findFoodAlternatives(current: Product): List<Alternative> {
        val alternatives = mutableListOf<Alternative>()
        
        // Extract the main category for search
        val mainCategory = current.categories?.split(",")?.firstOrNull()?.trim()?.lowercase()
        
        if (mainCategory == null) {
            return createGenericFoodAlternative(current)
        }
        
        try {
            // Search for similar products in the same category
            val searchResults = repository.searchProducts(mainCategory)
            
            // Filter and convert search results to alternatives
            searchResults
                .filter { it.code != current.barcode } // Exclude current product
                .mapNotNull { searchProduct ->
                    // Create a temporary product from search result
                    val alternativeProduct = Product(
                        barcode = searchProduct.code ?: return@mapNotNull null,
                        name = searchProduct.productName ?: "Unknown Product",
                        brands = searchProduct.brands,
                        imageUrl = searchProduct.imageUrl,
                        categories = searchProduct.categories,
                        ingredientsText = null,
                        category = ProductCategory.FOOD,
                        nutritionData = searchProduct.nutriments?.let {
                            NutritionData(
                                sugars100g = it.sugars100g,
                                salt100g = it.salt100g,
                                saturatedFat100g = it.saturatedFat100g,
                                proteins100g = null,
                                fiber100g = null,
                                energyKcal100g = null,
                                nutriScoreGrade = searchProduct.nutriscoreGrade,
                                novaGroup = searchProduct.novaGroup
                            )
                        }
                    )
                    
                    // Calculate score for the alternative
                    val scoreResult = calculateHealthScore(alternativeProduct)
                    val scoredProduct = alternativeProduct.copy(
                        healthScore = scoreResult.score,
                        dataAvailability = scoreResult.dataAvailability
                    )
                    
                    // Only include if it has a better score
                    if (scoredProduct.healthScore > current.healthScore) {
                        val scoreDiff = scoredProduct.healthScore - current.healthScore
                        
                        // Generate improvement reasons
                        val improvements = mutableListOf<String>()
                        
                        val currentNutrition = current.nutritionData
                        val altNutrition = scoredProduct.nutritionData
                        
                        if (currentNutrition != null && altNutrition != null) {
                            currentNutrition.sugars100g?.let { currentSugar ->
                                altNutrition.sugars100g?.let { altSugar ->
                                    if (altSugar < currentSugar) {
                                        val reduction = ((currentSugar - altSugar) / currentSugar * 100).toInt()
                                        improvements.add("$reduction% less sugar")
                                    }
                                }
                            }
                            
                            currentNutrition.salt100g?.let { currentSalt ->
                                altNutrition.salt100g?.let { altSalt ->
                                    if (altSalt < currentSalt) {
                                        val reduction = ((currentSalt - altSalt) / currentSalt * 100).toInt()
                                        improvements.add("$reduction% less salt")
                                    }
                                }
                            }
                            
                            currentNutrition.novaGroup?.let { currentNova ->
                                altNutrition.novaGroup?.let { altNova ->
                                    if (altNova < currentNova) {
                                        improvements.add("Less processed")
                                    }
                                }
                            }
                        }
                        
                        if (improvements.isEmpty()) {
                            improvements.add("Better overall nutrition")
                        }
                        
                        Alternative(
                            product = scoredProduct,
                            improvementReason = improvements.take(2).joinToString(", "),
                            scoreDifference = scoreDiff
                        )
                    } else null
                }
                .sortedByDescending { it.scoreDifference }
                .take(5) // Limit to top 5 alternatives
                .let { alternatives.addAll(it) }
            
        } catch (e: Exception) {
            // If search fails, return generic alternative
            return createGenericFoodAlternative(current)
        }
        
        // If no alternatives found, provide generic suggestion
        if (alternatives.isEmpty()) {
            return createGenericFoodAlternative(current)
        }
        
        return alternatives
    }
    
    private fun createGenericFoodAlternative(current: Product): List<Alternative> {
        val alternatives = mutableListOf<Alternative>()
        val currentNutrition = current.nutritionData ?: return emptyList()
        
        val improvements = mutableListOf<String>()
        
        currentNutrition.sugars100g?.let { sugar ->
            if (sugar > 5.0) {
                improvements.add("Lower sugar content")
            }
        }
        
        currentNutrition.salt100g?.let { salt ->
            if (salt > 1.0) {
                improvements.add("Reduced sodium")
            }
        }
        
        currentNutrition.novaGroup?.let { nova ->
            if (nova >= 3) {
                improvements.add("Less processed options")
            }
        }
        
        if (improvements.isNotEmpty()) {
            val betterProduct = current.copy(
                name = "Healthier ${current.categories?.split(",")?.firstOrNull() ?: "Alternative"}",
                healthScore = (current.healthScore + 15).coerceAtMost(100),
                warnings = emptyList(),
                brands = "Recommended brands"
            )
            
            alternatives.add(
                Alternative(
                    product = betterProduct,
                    improvementReason = "Look for products with: ${improvements.joinToString(", ")}",
                    scoreDifference = 15
                )
            )
        }
        
        return alternatives
    }
    
    private suspend fun findBeautyAlternatives(current: Product): List<Alternative> {
        val alternatives = mutableListOf<Alternative>()
        val currentBeauty = current.beautyData ?: return emptyList()
        
        val improvements = mutableListOf<String>()
        
        if (currentBeauty.isParabenFree == false) {
            improvements.add("Paraben-free")
        }
        
        if (currentBeauty.isSulfateFree == false) {
            improvements.add("Sulfate-free")
        }
        
        if (currentBeauty.isVegan == false) {
            improvements.add("Vegan")
        }
        
        currentBeauty.harmfulIngredients?.let { harmful ->
            if (harmful.isNotEmpty()) {
                improvements.add("Fewer harmful ingredients")
            }
        }
        
        if (improvements.isNotEmpty()) {
            val betterProduct = current.copy(
                name = "Natural ${current.categories?.split(",")?.firstOrNull() ?: "Alternative"}",
                healthScore = (current.healthScore + 20).coerceAtMost(100),
                warnings = emptyList(),
                brands = "Recommended natural brands"
            )
            
            alternatives.add(
                Alternative(
                    product = betterProduct,
                    improvementReason = "Look for products with: ${improvements.take(3).joinToString(", ")}",
                    scoreDifference = 20
                )
            )
        }
        
        return alternatives
    }
}
