package com.evolvarc.smartlens.ui.addproduct

import android.content.Context
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolvarc.smartlens.data.repository.ProductContributionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddProductUiState {
    object Idle : AddProductUiState()
    object Loading : AddProductUiState()
    data class Success(val contributionId: String) : AddProductUiState()
    data class Error(val message: String) : AddProductUiState()
}

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val contributionRepository: ProductContributionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AddProductUiState>(AddProductUiState.Idle)
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()
    
    private val _remainingContributions = MutableStateFlow(100)
    val remainingContributions: StateFlow<Int> = _remainingContributions.asStateFlow()
    
    private val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    
    fun checkContributionLimit() {
        viewModelScope.launch {
            contributionRepository.getRemainingContributions(deviceId)
                .onSuccess { remaining ->
                    _remainingContributions.value = remaining
                }
                .onFailure {
                    _remainingContributions.value = 100 // Default to max on error
                }
        }
    }
    
    fun submitProduct(
        barcode: String,
        productName: String,
        brandName: String?,
        category: String,
        frontImageUri: Uri,
        backImageUri: Uri
    ) {
        viewModelScope.launch {
            _uiState.value = AddProductUiState.Loading
            
            contributionRepository.contributeProduct(
                barcode = barcode,
                productName = productName,
                brandName = brandName,
                category = category,
                frontImageUri = frontImageUri,
                backImageUri = backImageUri,
                deviceId = deviceId
            ).onSuccess { contributionId ->
                _uiState.value = AddProductUiState.Success(contributionId)
                checkContributionLimit() // Refresh remaining count
            }.onFailure { error ->
                _uiState.value = AddProductUiState.Error(
                    error.message ?: "Failed to submit product"
                )
            }
        }
    }
}
