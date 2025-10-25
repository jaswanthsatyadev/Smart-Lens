package com.evolvarc.smartlens.domain.usecase

import com.evolvarc.smartlens.domain.model.Product
import com.evolvarc.smartlens.domain.model.ProductCategory
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class CalculateHealthScoreUseCase @Inject constructor() {
    
    data class ScoreResult(
        val score: Int,
        val dataAvailability: DataAvailability
    )
    
    enum class DataAvailability {
        COMPLETE,      // All key metrics available
        PARTIAL,       // Some metrics available
        INSUFFICIENT   // Very little data available
    }
    
    operator fun invoke(product: Product): ScoreResult {
        return when (product.category) {
            ProductCategory.FOOD -> calculateFoodScore(product)
            ProductCategory.BEAUTY, ProductCategory.PERSONAL_CARE -> calculateBeautyScore(product)
            else -> ScoreResult(50, DataAvailability.INSUFFICIENT)
        }
    }
    
    private fun calculateFoodScore(product: Product): ScoreResult {
        val nutrition = product.nutritionData 
        if (nutrition == null) {
            return ScoreResult(50, DataAvailability.INSUFFICIENT)
        }
        
        var score = 70.0 // Start with neutral base
        var availableFields = 0
        val totalKeyFields = 8 // sugars, fat, salt, calories, protein, fiber, nova, nutriscore
        
        // Use NutriScore if available (official health score)
        nutrition.nutriScoreGrade?.let { grade ->
            availableFields++
            score = when (grade.lowercase()) {
                "a" -> 95.0
                "b" -> 80.0
                "c" -> 60.0
                "d" -> 40.0
                "e" -> 20.0
                else -> score
            }
        }
        
        // Negative factors
        nutrition.sugars100g?.let { sugar ->
            availableFields++
            when {
                sugar > 25 -> score -= 20.0
                sugar > 15 -> score -= 12.0
                sugar > 10 -> score -= 8.0
                sugar > 5 -> score -= 4.0
            }
        }
        
        nutrition.saturatedFat100g?.let { fat ->
            availableFields++
            when {
                fat > 10 -> score -= 25.0
                fat > 5 -> score -= 15.0
                fat > 3 -> score -= 8.0
                fat > 1.5 -> score -= 4.0
            }
        }
        
        nutrition.salt100g?.let { salt ->
            availableFields++
            when {
                salt > 2.0 -> score -= 25.0
                salt > 1.5 -> score -= 18.0
                salt > 1.0 -> score -= 10.0
                salt > 0.5 -> score -= 5.0
            }
        }
        
        nutrition.energyKcal100g?.let { calories ->
            availableFields++
            when {
                calories > 500 -> score -= 15.0
                calories > 400 -> score -= 10.0
                calories > 300 -> score -= 5.0
                calories < 100 -> score += 5.0
            }
        }
        
        // Positive factors
        nutrition.proteins100g?.let { protein ->
            availableFields++
            when {
                protein > 15 -> score += 15.0
                protein > 10 -> score += 10.0
                protein > 5 -> score += 5.0
            }
        }
        
        nutrition.fiber100g?.let { fiber ->
            availableFields++
            when {
                fiber > 8 -> score += 15.0
                fiber > 5 -> score += 10.0
                fiber > 3 -> score += 5.0
            }
        }
        
        nutrition.novaGroup?.let { nova ->
            availableFields++
            when (nova) {
                1 -> score += 15.0 // Unprocessed
                2 -> score += 5.0  // Processed culinary ingredients
                3 -> score -= 10.0 // Processed foods
                4 -> score -= 20.0 // Ultra-processed
            }
        }
        
        val finalScore = max(0, min(100, score.toInt()))
        
        val dataAvailability = when {
            availableFields >= 5 -> DataAvailability.COMPLETE
            availableFields >= 3 -> DataAvailability.PARTIAL
            else -> DataAvailability.INSUFFICIENT
        }
        
        return ScoreResult(finalScore, dataAvailability)
    }
    
    private fun calculateBeautyScore(product: Product): ScoreResult {
        val beauty = product.beautyData
        if (beauty == null) {
            return ScoreResult(50, DataAvailability.INSUFFICIENT)
        }
        
        var score = 70.0 // Start with neutral base
        var availableFields = 0
        val totalKeyFields = 5 // harmful ingredients, vegan, cruelty-free, paraben-free, allergens
        
        beauty.harmfulIngredients?.let { harmful ->
            availableFields++
            when (harmful.size) {
                0 -> score += 15.0
                in 1..2 -> score -= 10.0
                in 3..5 -> score -= 20.0
                else -> score -= 30.0
            }
        }
        
        if (beauty.isVegan != null) {
            availableFields++
            if (beauty.isVegan == true) score += 8.0
        }
        
        if (beauty.isCrueltyFree != null) {
            availableFields++
            if (beauty.isCrueltyFree == true) score += 8.0
        }
        
        if (beauty.isParabenFree != null) {
            availableFields++
            if (beauty.isParabenFree == true) score += 7.0
        }
        
        beauty.allergens?.let { allergens ->
            availableFields++
            when (allergens.size) {
                0 -> score += 5.0
                in 1..2 -> score -= 5.0
                in 3..5 -> score -= 10.0
                else -> score -= 15.0
            }
        }
        
        val finalScore = max(0, min(100, score.toInt()))
        
        val dataAvailability = when {
            availableFields >= 4 -> DataAvailability.COMPLETE
            availableFields >= 2 -> DataAvailability.PARTIAL
            else -> DataAvailability.INSUFFICIENT
        }
        
        return ScoreResult(finalScore, dataAvailability)
    }
}
