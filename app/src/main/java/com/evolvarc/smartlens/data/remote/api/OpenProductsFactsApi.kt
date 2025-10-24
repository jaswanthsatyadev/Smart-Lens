package com.evolvarc.smartlens.data.remote.api

import com.evolvarc.smartlens.data.remote.dto.OpenProductsFactsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenProductsFactsApi {
    @GET("api/v2/product/{barcode}")
    suspend fun getProduct(@Path("barcode") barcode: String): OpenProductsFactsResponse
}
