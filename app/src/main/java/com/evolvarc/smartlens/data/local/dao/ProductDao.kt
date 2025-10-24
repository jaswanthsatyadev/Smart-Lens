package com.evolvarc.smartlens.data.local.dao

import androidx.room.*
import com.evolvarc.smartlens.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE barcode = :barcode")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?
    
    @Query("SELECT * FROM products ORDER BY scannedAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE category = :category ORDER BY scannedAt DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    @Query("DELETE FROM products WHERE cachedAt < :expiryTime")
    suspend fun deleteExpiredProducts(expiryTime: Long)
    
    @Query("DELETE FROM products")
    suspend fun clearAll()
}
