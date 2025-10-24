package com.evolvarc.smartlens.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser
    
    fun isUserLoggedIn(): Boolean = currentUser != null
    
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.failure(Exception("User is null"))
            
            // Create or update user profile in Firestore
            createOrUpdateUserProfile(user)
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.failure(Exception("User is null"))
            
            // Create or update user profile in Firestore
            createOrUpdateUserProfile(user)
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun createOrUpdateUserProfile(user: FirebaseUser) {
        try {
            val userDoc = firestore.collection("users").document(user.uid)
            val userExists = userDoc.get().await().exists()
            
            val userData = hashMapOf(
                "email" to (user.email ?: ""),
                "displayName" to (user.displayName ?: ""),
                "phoneNumber" to (user.phoneNumber ?: ""),
                "photoURL" to (user.photoUrl?.toString() ?: ""),
                "lastLoginAt" to com.google.firebase.Timestamp.now()
            )
            
            if (!userExists) {
                userData["createdAt"] = com.google.firebase.Timestamp.now()
                
                // Also create user profile document
                firestore.collection("userProfiles").document(user.uid).set(
                    hashMapOf(
                        "userId" to user.uid,
                        "allergies" to emptyList<String>(),
                        "dietaryPreferences" to emptyList<String>(),
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )
                ).await()
            }
            
            userDoc.set(userData, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            // Log error but don't fail authentication
            e.printStackTrace()
        }
    }
    
    suspend fun signOut() {
        firebaseAuth.signOut()
    }
}
