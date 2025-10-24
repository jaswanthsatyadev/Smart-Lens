package com.evolvarc.smartlens.data.remote.api

import com.evolvarc.smartlens.data.remote.dto.OpenFoodFactsResponse
import com.evolvarc.smartlens.data.remote.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {
    @GET("api/v2/product/{barcode}")
    suspend fun getProduct(@Path("barcode") barcode: String): OpenFoodFactsResponse
    
    @GET("category/{category}.json?page_size=50")
    suspend fun getProductsByCategory(@Path("category") category: String): OpenFoodFactsResponse
    
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") searchTerms: String,
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1,
        @Query("fields") fields: String = "code,product_name,brands,image_url,categories,nutriscore_grade,nova_group,nutriments"
    ): SearchResponse
}
