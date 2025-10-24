package com.evolvarc.smartlens.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenBeautyFactsResponse(
    @Json(name = "status") val status: Int,
    @Json(name = "product") val product: OpenBeautyFactsProduct?
)

@JsonClass(generateAdapter = true)
data class OpenBeautyFactsProduct(
    @Json(name = "code") val code: String?,
    @Json(name = "product_name") val productName: String?,
    @Json(name = "brands") val brands: String?,
    @Json(name = "categories") val categories: String?,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "ingredients_text") val ingredientsText: String?,
    @Json(name = "ingredients_analysis_tags") val ingredientsAnalysisTags: List<String>?,
    @Json(name = "allergens") val allergens: String?
)
