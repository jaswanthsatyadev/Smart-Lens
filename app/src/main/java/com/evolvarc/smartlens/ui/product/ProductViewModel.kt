package com.evolvarc.smartlens.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolvarc.smartlens.data.repository.ProductRepository
import com.evolvarc.smartlens.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()
    
    fun loadProduct(barcode: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            
            repository.getProductByBarcode(barcode).fold(
                onSuccess = { product ->
                    _uiState.value = ProductUiState.Success(product)
                },
                onFailure = { error ->
                    _uiState.value = ProductUiState.Error(
                        error.message ?: "Failed to load product"
                    )
                }
            )
        }
    }
}

sealed class ProductUiState {
    object Loading : ProductUiState()
    data class Success(val product: Product) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}
