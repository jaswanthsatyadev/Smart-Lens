package com.evolvarc.smartlens.data.repository

import com.evolvarc.smartlens.data.local.dao.ProductDao
import com.evolvarc.smartlens.data.mapper.toDomainModel
import com.evolvarc.smartlens.data.mapper.toEntity
import com.evolvarc.smartlens.data.remote.api.OpenBeautyFactsApi
import com.evolvarc.smartlens.data.remote.api.OpenFoodFactsApi
import com.evolvarc.smartlens.data.remote.api.OpenProductsFactsApi
import com.evolvarc.smartlens.data.remote.dto.SearchProductDto
import com.evolvarc.smartlens.domain.model.Product
import com.evolvarc.smartlens.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val foodApi: OpenFoodFactsApi,
    private val beautyApi: OpenBeautyFactsApi,
    private val productsApi: OpenProductsFactsApi,
    private val productDao: ProductDao
) {
    suspend fun getProductByBarcode(barcode: String): Result<Product> {
        return try {
            val cached = productDao.getProductByBarcode(barcode)
            if (cached != null && !isCacheExpired(cached.cachedAt)) {
                return Result.success(cached.toDomainModel())
            }
            
            val foodResponse = foodApi.getProduct(barcode)
            if (foodResponse.status == 1 && foodResponse.product != null) {
                val product = foodResponse.product.toDomainModel()
                return Result.success(product)
            }
            
            val beautyResponse = beautyApi.getProduct(barcode)
            if (beautyResponse.status == 1 && beautyResponse.product != null) {
                val product = beautyResponse.product.toDomainModel()
                return Result.success(product)
            }
            
            val productsResponse = productsApi.getProduct(barcode)
            if (productsResponse.status == 1 && productsResponse.product != null) {
                val product = productsResponse.product.toDomainModel()
                return Result.success(product)
            }
            
            Result.failure(Exception("Product not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveProduct(product: Product) {
        productDao.insertProduct(product.toEntity())
    }
    
    fun getScanHistory(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getProductsByCategory(category: ProductCategory): Flow<List<Product>> {
        return productDao.getProductsByCategory(category.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    suspend fun clearExpiredCache() {
        val expiryTime = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        productDao.deleteExpiredProducts(expiryTime)
    }
    
    suspend fun searchProducts(query: String): List<SearchProductDto> {
        return try {
            val response = foodApi.searchProducts(
                searchTerms = query,
                json = 1,
                pageSize = 20,
                page = 1,
                fields = "code,product_name,brands,image_url,categories,nutriscore_grade,nova_group,nutriments"
            )
            response.products ?: emptyList()
        } catch (e: Exception) {
            throw Exception("Failed to search products: ${e.message}")
        }
    }
    
    private fun isCacheExpired(cachedAt: Long): Boolean {
        val expiryTime = 30 * 24 * 60 * 60 * 1000L
        return System.currentTimeMillis() - cachedAt > expiryTime
    }
}
