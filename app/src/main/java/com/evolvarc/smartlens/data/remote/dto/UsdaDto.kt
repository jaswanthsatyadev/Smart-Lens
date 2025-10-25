package com.evolvarc.smartlens.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// USDA FoodData Central API Response Models

@JsonClass(generateAdapter = true)
data class UsdaSearchResponse(
    @Json(name = "totalHits") val totalHits: Int?,
    @Json(name = "currentPage") val currentPage: Int?,
    @Json(name = "totalPages") val totalPages: Int?,
    @Json(name = "foods") val foods: List<UsdaFood>?
)

@JsonClass(generateAdapter = true)
data class UsdaFood(
    @Json(name = "fdcId") val fdcId: Int?,
    @Json(name = "description") val description: String?,
    @Json(name = "dataType") val dataType: String?, // Branded, Foundation, Survey, etc.
    @Json(name = "gtinUpc") val gtinUpc: String?, // Barcode/UPC
    @Json(name = "brandOwner") val brandOwner: String?,
    @Json(name = "brandName") val brandName: String?,
    @Json(name = "ingredients") val ingredients: String?,
    @Json(name = "servingSize") val servingSize: Double?,
    @Json(name = "servingSizeUnit") val servingSizeUnit: String?,
    @Json(name = "householdServingFullText") val householdServingFullText: String?,
    @Json(name = "foodNutrients") val foodNutrients: List<UsdaFoodNutrient>?,
    @Json(name = "foodCategory") val foodCategory: String?,
    @Json(name = "foodCategoryId") val foodCategoryId: Int?,
    @Json(name = "publishedDate") val publishedDate: String?,
    @Json(name = "allHighlightFields") val allHighlightFields: String?,
    @Json(name = "score") val score: Double?
)

@JsonClass(generateAdapter = true)
data class UsdaFoodNutrient(
    @Json(name = "nutrientId") val nutrientId: Int?,
    @Json(name = "nutrientName") val nutrientName: String?,
    @Json(name = "nutrientNumber") val nutrientNumber: String?,
    @Json(name = "unitName") val unitName: String?,
    @Json(name = "value") val value: Double?,
    @Json(name = "derivationCode") val derivationCode: String?,
    @Json(name = "derivationDescription") val derivationDescription: String?
)

@JsonClass(generateAdapter = true)
data class UsdaFoodDetailResponse(
    @Json(name = "fdcId") val fdcId: Int?,
    @Json(name = "description") val description: String?,
    @Json(name = "dataType") val dataType: String?,
    @Json(name = "gtinUpc") val gtinUpc: String?,
    @Json(name = "brandOwner") val brandOwner: String?,
    @Json(name = "brandName") val brandName: String?,
    @Json(name = "ingredients") val ingredients: String?,
    @Json(name = "servingSize") val servingSize: Double?,
    @Json(name = "servingSizeUnit") val servingSizeUnit: String?,
    @Json(name = "householdServingFullText") val householdServingFullText: String?,
    @Json(name = "foodNutrients") val foodNutrients: List<UsdaFoodNutrient>?,
    @Json(name = "foodCategory") val foodCategory: String?,
    @Json(name = "foodCategoryId") val foodCategoryId: Int?,
    @Json(name = "publishedDate") val publishedDate: String?,
    @Json(name = "labelNutrients") val labelNutrients: UsdaLabelNutrients?,
    @Json(name = "foodAttributes") val foodAttributes: List<UsdaFoodAttribute>?
)

@JsonClass(generateAdapter = true)
data class UsdaLabelNutrients(
    @Json(name = "fat") val fat: UsdaLabelValue?,
    @Json(name = "saturatedFat") val saturatedFat: UsdaLabelValue?,
    @Json(name = "transFat") val transFat: UsdaLabelValue?,
    @Json(name = "cholesterol") val cholesterol: UsdaLabelValue?,
    @Json(name = "sodium") val sodium: UsdaLabelValue?,
    @Json(name = "carbohydrates") val carbohydrates: UsdaLabelValue?,
    @Json(name = "fiber") val fiber: UsdaLabelValue?,
    @Json(name = "sugars") val sugars: UsdaLabelValue?,
    @Json(name = "protein") val protein: UsdaLabelValue?,
    @Json(name = "calcium") val calcium: UsdaLabelValue?,
    @Json(name = "iron") val iron: UsdaLabelValue?,
    @Json(name = "potassium") val potassium: UsdaLabelValue?,
    @Json(name = "calories") val calories: UsdaLabelValue?
)

@JsonClass(generateAdapter = true)
data class UsdaLabelValue(
    @Json(name = "value") val value: Double?
)

@JsonClass(generateAdapter = true)
data class UsdaFoodAttribute(
    @Json(name = "id") val id: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "value") val value: String?
)
