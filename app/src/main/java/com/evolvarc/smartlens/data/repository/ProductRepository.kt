package com.evolvarc.smartlens.data.repository

import com.evolvarc.smartlens.data.local.dao.ProductDao
import com.evolvarc.smartlens.data.mapper.toDomainModel
import com.evolvarc.smartlens.data.mapper.toEntity
import com.evolvarc.smartlens.data.remote.api.OpenBeautyFactsApi
import com.evolvarc.smartlens.data.remote.api.OpenFoodFactsApi
import com.evolvarc.smartlens.data.remote.api.OpenProductsFactsApi
import com.evolvarc.smartlens.data.remote.api.UsdaApi
import com.evolvarc.smartlens.data.remote.dto.SearchProductDto
import com.evolvarc.smartlens.domain.model.NutritionData
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
    private val usdaApi: UsdaApi,
    private val productDao: ProductDao
) {
    suspend fun getProductByBarcode(barcode: String): Result<Product> {
        return try {
            // Check cache first
            val cached = productDao.getProductByBarcode(barcode)
            if (cached != null && !isCacheExpired(cached.cachedAt)) {
                return Result.success(cached.toDomainModel())
            }
            
            // Try OpenFoodFacts first (priority for food items)
            var openFoodProduct: Product? = null
            try {
                val foodResponse = foodApi.getProduct(barcode)
                if (foodResponse.status == 1 && foodResponse.product != null) {
                    openFoodProduct = foodResponse.product.toDomainModel()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Try USDA API
            var usdaProduct: Product? = null
            try {
                val usdaResponse = usdaApi.searchByBarcode(
                    query = barcode,
                    apiKey = UsdaApi.API_KEY
                )
                if (!usdaResponse.foods.isNullOrEmpty()) {
                    usdaProduct = usdaResponse.foods.first().toDomainModel()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Combine data from both sources (OpenFoodFacts priority)
            val combinedProduct = when {
                openFoodProduct != null && usdaProduct != null -> {
                    mergeProductData(openFoodProduct, usdaProduct)
                }
                openFoodProduct != null -> openFoodProduct
                usdaProduct != null -> usdaProduct
                else -> null
            }
            
            if (combinedProduct != null) {
                return Result.success(combinedProduct)
            }
            
            // Fallback to beauty/products APIs
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
    
    /**
     * Merge product data from OpenFoodFacts and USDA
     * Priority: OpenFoodFacts > USDA for overlapping data
     */
    private fun mergeProductData(openFood: Product, usda: Product): Product {
        return openFood.copy(
            // Use OpenFoodFacts name if available, otherwise USDA
            name = openFood.name.takeIf { it != "Unknown Product" } ?: usda.name,
            
            // Use OpenFoodFacts brands if available
            brands = openFood.brands ?: usda.brands,
            
            // Keep OpenFoodFacts image (USDA doesn't have images)
            imageUrl = openFood.imageUrl,
            
            // Use OpenFoodFacts categories if available
            categories = openFood.categories ?: usda.categories,
            
            // Merge ingredients (prefer OpenFoodFacts, use USDA as fallback)
            ingredientsText = openFood.ingredientsText ?: usda.ingredientsText,
            
            // Merge nutrition data intelligently
            nutritionData = mergeNutritionData(openFood.nutritionData, usda.nutritionData),
            
            // Combine allergens from both sources
            allergens = (openFood.allergens + usda.allergens).distinct()
        )
    }
    
    /**
     * Merge nutrition data from two sources
     * Priority: OpenFoodFacts > USDA for individual fields
     */
    private fun mergeNutritionData(
        openFood: NutritionData?,
        usda: NutritionData?
    ): NutritionData? {
        if (openFood == null && usda == null) return null
        if (openFood == null) return usda
        if (usda == null) return openFood
        
        return NutritionData(
            sugars100g = openFood.sugars100g ?: usda.sugars100g,
            salt100g = openFood.salt100g ?: usda.salt100g,
            saturatedFat100g = openFood.saturatedFat100g ?: usda.saturatedFat100g,
            proteins100g = openFood.proteins100g ?: usda.proteins100g,
            fiber100g = openFood.fiber100g ?: usda.fiber100g,
            energyKcal100g = openFood.energyKcal100g ?: usda.energyKcal100g,
            nutriScoreGrade = openFood.nutriScoreGrade, // Only OpenFoodFacts has this
            novaGroup = openFood.novaGroup // Only OpenFoodFacts has this
        )
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
            val results = mutableListOf<SearchProductDto>()
            
            // Search OpenFoodFacts (priority)
            try {
                val openFoodResponse = foodApi.searchProducts(
                    searchTerms = query,
                    json = 1,
                    pageSize = 20,
                    page = 1,
                    fields = "code,product_name,brands,image_url,categories,nutriscore_grade,nova_group,nutriments"
                )
                openFoodResponse.products?.let { results.addAll(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Search USDA (additional results)
            try {
                val usdaResponse = usdaApi.searchFoods(
                    query = query,
                    apiKey = UsdaApi.API_KEY,
                    pageSize = 10,
                    pageNumber = 1
                )
                
                // Convert USDA foods to SearchProductDto format
                usdaResponse.foods?.forEach { usdaFood ->
                    val searchDto = SearchProductDto(
                        code = usdaFood.gtinUpc ?: usdaFood.fdcId?.toString(),
                        productName = usdaFood.description,
                        brands = usdaFood.brandOwner ?: usdaFood.brandName,
                        imageUrl = null, // USDA doesn't provide images
                        categories = usdaFood.foodCategory,
                        nutriscoreGrade = null,
                        novaGroup = null,
                        nutriments = null
                    )
                    results.add(searchDto)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Remove duplicates based on barcode/code
            results.distinctBy { it.code }
        } catch (e: Exception) {
            throw Exception("Failed to search products: ${e.message}")
        }
    }
    
    private fun isCacheExpired(cachedAt: Long): Boolean {
        val expiryTime = 30 * 24 * 60 * 60 * 1000L
        return System.currentTimeMillis() - cachedAt > expiryTime
    }
}
