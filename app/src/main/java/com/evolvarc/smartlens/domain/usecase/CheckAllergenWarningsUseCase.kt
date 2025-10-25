package com.evolvarc.smartlens.domain.usecase

import com.evolvarc.smartlens.data.repository.AuthRepository
import com.evolvarc.smartlens.domain.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AllergenWarning(
    val allergen: String,
    val message: String
)

class CheckAllergenWarningsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) {
    suspend operator fun invoke(product: Product): List<AllergenWarning> {
        // Only check for logged-in users
        val currentUser = authRepository.currentUser ?: return emptyList()
        
        try {
            // Fetch user's allergies from Firestore
            val userProfileDoc = firestore.collection("userProfiles")
                .document(currentUser.uid)
                .get()
                .await()
            
            if (!userProfileDoc.exists()) return emptyList()
            
            @Suppress("UNCHECKED_CAST")
            val userAllergies = userProfileDoc.get("allergies") as? List<String> ?: emptyList()
            
            if (userAllergies.isEmpty()) return emptyList()
            
            // Check for matches between user allergies and product allergens
            val warnings = mutableListOf<AllergenWarning>()
            
            // Check product's allergen list
            product.allergens.forEach { productAllergen ->
                userAllergies.forEach { userAllergen ->
                    if (productAllergen.contains(userAllergen, ignoreCase = true) ||
                        userAllergen.contains(productAllergen, ignoreCase = true)) {
                        warnings.add(
                            AllergenWarning(
                                allergen = userAllergen,
                                message = "⚠️ Contains $userAllergen - This product may not be suitable for you"
                            )
                        )
                    }
                }
            }
            
            // Also check ingredients text for additional matches
            product.ingredientsText?.let { ingredients ->
                userAllergies.forEach { userAllergen ->
                    if (ingredients.contains(userAllergen, ignoreCase = true) &&
                        warnings.none { it.allergen.equals(userAllergen, ignoreCase = true) }) {
                        warnings.add(
                            AllergenWarning(
                                allergen = userAllergen,
                                message = "⚠️ May contain $userAllergen - Found in ingredients list"
                            )
                        )
                    }
                }
            }
            
            return warnings.distinctBy { it.allergen.lowercase() }
        } catch (e: Exception) {
            // If Firestore check fails, return empty list (fail gracefully)
            e.printStackTrace()
            return emptyList()
        }
    }
}
