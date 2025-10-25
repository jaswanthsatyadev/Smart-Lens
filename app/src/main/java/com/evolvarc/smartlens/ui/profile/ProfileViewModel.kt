package com.evolvarc.smartlens.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolvarc.smartlens.data.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    
    private val _allergies = MutableStateFlow<List<String>>(emptyList())
    val allergies: StateFlow<List<String>> = _allergies.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.currentUser ?: return@launch
                
                val userProfileDoc = firestore.collection("userProfiles")
                    .document(currentUser.uid)
                    .get()
                    .await()
                
                if (userProfileDoc.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val allergies = userProfileDoc.get("allergies") as? List<String> ?: emptyList()
                    _allergies.value = allergies
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun saveAllergies(allergies: List<String>) {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.currentUser ?: return@launch
                
                firestore.collection("userProfiles")
                    .document(currentUser.uid)
                    .set(
                        mapOf("allergies" to allergies),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
                    .await()
                
                _allergies.value = allergies
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
