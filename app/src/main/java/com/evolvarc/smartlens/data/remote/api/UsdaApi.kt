package com.evolvarc.smartlens.data.remote.api

import com.evolvarc.smartlens.data.remote.dto.UsdaFoodDetailResponse
import com.evolvarc.smartlens.data.remote.dto.UsdaSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsdaApi {
    
    /**
     * Search foods by query
     * Endpoint: /fdc/v1/foods/search
     */
    @GET("fdc/v1/foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("dataType") dataType: List<String>? = listOf("Branded", "Foundation", "Survey (FNDDS)"),
        @Query("pageSize") pageSize: Int = 25,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("sortBy") sortBy: String? = "dataType.keyword",
        @Query("sortOrder") sortOrder: String? = "asc"
    ): UsdaSearchResponse
    
    /**
     * Get food by FDC ID
     * Endpoint: /fdc/v1/food/{fdcId}
     */
    @GET("fdc/v1/food/{fdcId}")
    suspend fun getFood(
        @Path("fdcId") fdcId: Int,
        @Query("api_key") apiKey: String,
        @Query("format") format: String? = "full",
        @Query("nutrients") nutrients: List<Int>? = null
    ): UsdaFoodDetailResponse
    
    /**
     * Search foods by UPC/barcode
     * Using gtinUpc filter in search
     */
    @GET("fdc/v1/foods/search")
    suspend fun searchByBarcode(
        @Query("query") query: String, // Will be the barcode
        @Query("api_key") apiKey: String,
        @Query("dataType") dataType: List<String> = listOf("Branded"),
        @Query("pageSize") pageSize: Int = 10
    ): UsdaSearchResponse
    
    companion object {
        const val BASE_URL = "https://api.nal.usda.gov/"
        
        // USDA API Key - You need to get your own from: https://fdc.nal.usda.gov/api-key-signup.html
        const val API_KEY = "MX95owZEYs3lhrLcAelmna7jz31LhAYQEw82SsjF" // Replace with actual key
        
        // Common nutrient IDs
        object NutrientIds {
            const val ENERGY_KCAL = 1008
            const val PROTEIN = 1003
            const val FAT_TOTAL = 1004
            const val CARBOHYDRATE = 1005
            const val FIBER = 1079
            const val SUGARS_TOTAL = 2000
            const val CALCIUM = 1087
            const val IRON = 1089
            const val SODIUM = 1093
            const val VITAMIN_C = 1162
            const val SATURATED_FAT = 1258
            const val TRANS_FAT = 1257
        }
    }
}
