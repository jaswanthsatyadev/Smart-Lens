package com.evolvarc.smartlens.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @Json(name = "count") val count: Int?,
    @Json(name = "page") val page: Int?,
    @Json(name = "page_size") val pageSize: Int?,
    @Json(name = "products") val products: List<SearchProductDto>?
)

@JsonClass(generateAdapter = true)
data class SearchProductDto(
    @Json(name = "code") val code: String?,
    @Json(name = "product_name") val productName: String?,
    @Json(name = "brands") val brands: String?,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "categories") val categories: String?,
    @Json(name = "nutriscore_grade") val nutriscoreGrade: String?,
    @Json(name = "nova_group") val novaGroup: Int?,
    @Json(name = "nutriments") val nutriments: NutrimentsDto?
)

@JsonClass(generateAdapter = true)
data class NutrimentsDto(
    @Json(name = "sugars_100g") val sugars100g: Double?,
    @Json(name = "salt_100g") val salt100g: Double?,
    @Json(name = "saturated-fat_100g") val saturatedFat100g: Double?
)
