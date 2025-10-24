package com.evolvarc.smartlens.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolvarc.smartlens.data.repository.ProductRepository
import com.evolvarc.smartlens.domain.model.Product
import com.evolvarc.smartlens.domain.usecase.CalculateHealthScoreUseCase
import com.evolvarc.smartlens.domain.usecase.GenerateWarningsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val calculateHealthScore: CalculateHealthScoreUseCase,
    private val generateWarnings: GenerateWarningsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Scanning)
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()
    
    fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            _uiState.value = ScannerUiState.Loading
            
            repository.getProductByBarcode(barcode).fold(
                onSuccess = { product ->
                    val scoreResult = calculateHealthScore(product)
                    val warnings = generateWarnings(product)
                    val enrichedProduct = product.copy(
                        healthScore = scoreResult.score,
                        dataAvailability = scoreResult.dataAvailability,
                        warnings = warnings
                    )
                    repository.saveProduct(enrichedProduct)
                    _uiState.value = ScannerUiState.Success(enrichedProduct)
                },
                onFailure = { error ->
                    _uiState.value = ScannerUiState.Error(
                        error.message ?: "Product not found in database"
                    )
                }
            )
        }
    }
    
    fun resetScanner() {
        _uiState.value = ScannerUiState.Scanning
    }
}

sealed class ScannerUiState {
    object Scanning : ScannerUiState()
    object Loading : ScannerUiState()
    data class Success(val product: Product) : ScannerUiState()
    data class Error(val message: String) : ScannerUiState()
}
