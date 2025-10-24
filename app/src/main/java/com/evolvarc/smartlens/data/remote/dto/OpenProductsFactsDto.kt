package com.evolvarc.smartlens.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenProductsFactsResponse(
    @Json(name = "status") val status: Int,
    @Json(name = "product") val product: OpenProductsFactsProduct?
)

@JsonClass(generateAdapter = true)
data class OpenProductsFactsProduct(
    @Json(name = "code") val code: String?,
    @Json(name = "product_name") val productName: String?,
    @Json(name = "brands") val brands: String?,
    @Json(name = "categories") val categories: String?,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "ingredients_text") val ingredientsText: String?
)
