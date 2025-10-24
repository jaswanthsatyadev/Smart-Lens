package com.evolvarc.smartlens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val barcode: String,
    val name: String,
    val brands: String?,
    val imageUrl: String?,
    val categories: String?,
    val ingredientsText: String?,
    val category: String,
    
    val sugars100g: Double?,
    val salt100g: Double?,
    val saturatedFat100g: Double?,
    val proteins100g: Double?,
    val fiber100g: Double?,
    val energyKcal100g: Double?,
    val nutriScoreGrade: String?,
    val novaGroup: Int?,
    
    val harmfulIngredients: String?,
    val allergens: String?,
    val isVegan: Boolean?,
    val isCrueltyFree: Boolean?,
    val isParabenFree: Boolean?,
    val isSulfateFree: Boolean?,
    
    val healthScore: Int,
    val warnings: String,
    val scannedAt: Long,
    val cachedAt: Long = System.currentTimeMillis()
)
