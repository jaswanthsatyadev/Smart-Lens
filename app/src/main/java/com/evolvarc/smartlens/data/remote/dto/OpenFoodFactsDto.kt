package com.evolvarc.smartlens.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenFoodFactsResponse(
    @Json(name = "status") val status: Int,
    @Json(name = "product") val product: OpenFoodFactsProduct?
)

@JsonClass(generateAdapter = true)
data class OpenFoodFactsProduct(
    @Json(name = "code") val code: String?,
    @Json(name = "product_name") val productName: String?,
    @Json(name = "brands") val brands: String?,
    @Json(name = "categories") val categories: String?,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "ingredients_text") val ingredientsText: String?,
    @Json(name = "nutriments") val nutriments: Nutriments?,
    @Json(name = "nutriscore_grade") val nutriscoreGrade: String?,
    @Json(name = "nova_group") val novaGroup: Int?,
    @Json(name = "allergens") val allergens: String?, // Comma-separated allergens
    @Json(name = "allergens_tags") val allergensTags: List<String>? // en:milk, en:eggs, etc.
)

@JsonClass(generateAdapter = true)
data class Nutriments(
    @Json(name = "sugars_100g") val sugars100g: Double?,
    @Json(name = "salt_100g") val salt100g: Double?,
    @Json(name = "saturated-fat_100g") val saturatedFat100g: Double?,
    @Json(name = "proteins_100g") val proteins100g: Double?,
    @Json(name = "fiber_100g") val fiber100g: Double?,
    @Json(name = "energy-kcal_100g") val energyKcal100g: Double?
)
