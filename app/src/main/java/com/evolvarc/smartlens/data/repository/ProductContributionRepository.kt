package com.evolvarc.smartlens.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.evolvarc.smartlens.domain.model.UserContributedProduct
import com.evolvarc.smartlens.domain.model.DeviceContributionLimit
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ProductContributionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val CONTRIBUTIONS_COLLECTION = "productContributions"
        private const val DEVICE_LIMITS_COLLECTION = "deviceContributionLimits"
        private const val MAX_CONTRIBUTIONS_PER_DEVICE = 100
    }
    
    suspend fun canContribute(deviceId: String): Result<Boolean> {
        return try {
            val limitDoc = firestore.collection(DEVICE_LIMITS_COLLECTION)
                .document(deviceId)
                .get()
                .await()
            
            val limit = limitDoc.toObject(DeviceContributionLimit::class.java)
            val canAdd = (limit?.contributionCount ?: 0) < MAX_CONTRIBUTIONS_PER_DEVICE
            
            Result.success(canAdd)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRemainingContributions(deviceId: String): Result<Int> {
        return try {
            val limitDoc = firestore.collection(DEVICE_LIMITS_COLLECTION)
                .document(deviceId)
                .get()
                .await()
            
            val limit = limitDoc.toObject(DeviceContributionLimit::class.java)
            val remaining = MAX_CONTRIBUTIONS_PER_DEVICE - (limit?.contributionCount ?: 0)
            
            Result.success(remaining)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadProductImages(
        barcode: String,
        frontImageUri: Uri?,
        backImageUri: Uri?
    ): Result<Pair<String?, String?>> {
        return try {
            var frontUrl: String? = null
            var backUrl: String? = null
            
            // Upload front image
            frontImageUri?.let { uri ->
                val frontRef = storage.reference
                    .child("product_contributions/$barcode/front_${UUID.randomUUID()}.jpg")
                frontRef.putFile(uri).await()
                frontUrl = frontRef.downloadUrl.await().toString()
            }
            
            // Upload back image
            backImageUri?.let { uri ->
                val backRef = storage.reference
                    .child("product_contributions/$barcode/back_${UUID.randomUUID()}.jpg")
                backRef.putFile(uri).await()
                backUrl = backRef.downloadUrl.await().toString()
            }
            
            Result.success(Pair(frontUrl, backUrl))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun contributeProduct(
        barcode: String,
        productName: String,
        brandName: String?,
        category: String,
        frontImageUri: Uri?,
        backImageUri: Uri?,
        deviceId: String
    ): Result<String> {
        return try {
            // Check if device can contribute
            val canAdd = canContribute(deviceId).getOrThrow()
            if (!canAdd) {
                return Result.failure(Exception("Device contribution limit reached (100 products)"))
            }
            
            // Upload images
            val (frontUrl, backUrl) = uploadProductImages(barcode, frontImageUri, backImageUri).getOrThrow()
            
            // Create contribution document
            val userId = auth.currentUser?.uid ?: "guest"
            val contribution = UserContributedProduct(
                barcode = barcode,
                productName = productName,
                brandName = brandName,
                category = category,
                frontImageUrl = frontUrl,
                backImageUrl = backUrl,
                contributedBy = userId,
                deviceId = deviceId,
                contributedAt = System.currentTimeMillis(),
                status = "pending"
            )
            
            // Add to Firestore
            val docRef = firestore.collection(CONTRIBUTIONS_COLLECTION)
                .add(contribution)
                .await()
            
            // Update device contribution count
            val limitDocRef = firestore.collection(DEVICE_LIMITS_COLLECTION)
                .document(deviceId)
            
            firestore.runTransaction { transaction ->
                val limitDoc = transaction.get(limitDocRef)
                val currentLimit = limitDoc.toObject(DeviceContributionLimit::class.java)
                
                val newLimit = DeviceContributionLimit(
                    deviceId = deviceId,
                    contributionCount = (currentLimit?.contributionCount ?: 0) + 1,
                    lastContributedAt = System.currentTimeMillis()
                )
                
                transaction.set(limitDocRef, newLimit)
            }.await()
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
