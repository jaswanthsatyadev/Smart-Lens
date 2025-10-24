package com.evolvarc.smartlens.data.remote.api

import com.evolvarc.smartlens.data.remote.dto.OpenBeautyFactsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenBeautyFactsApi {
    @GET("api/v2/product/{barcode}")
    suspend fun getProduct(@Path("barcode") barcode: String): OpenBeautyFactsResponse
    
    @GET("category/{category}.json?page_size=50")
    suspend fun getProductsByCategory(@Path("category") category: String): OpenBeautyFactsResponse
}
