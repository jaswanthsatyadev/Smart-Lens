package com.evolvarc.smartlens.ui.alternatives

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolvarc.smartlens.data.repository.ProductRepository
import com.evolvarc.smartlens.domain.model.Alternative
import com.evolvarc.smartlens.domain.model.Product
import com.evolvarc.smartlens.domain.usecase.CalculateHealthScoreUseCase
import com.evolvarc.smartlens.domain.usecase.FindAlternativesUseCase
import com.evolvarc.smartlens.domain.usecase.GenerateWarningsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlternativesViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val findAlternatives: FindAlternativesUseCase,
    private val calculateHealthScore: CalculateHealthScoreUseCase,
    private val generateWarnings: GenerateWarningsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AlternativesUiState>(AlternativesUiState.Loading)
    val uiState: StateFlow<AlternativesUiState> = _uiState.asStateFlow()
    
    fun loadAlternatives(barcode: String) {
        viewModelScope.launch {
            _uiState.value = AlternativesUiState.Loading
            
            repository.getProductByBarcode(barcode).fold(
                onSuccess = { currentProduct ->
                    findAlternatives(currentProduct).fold(
                        onSuccess = { alternatives ->
                            _uiState.value = AlternativesUiState.Success(
                                currentProduct = currentProduct,
                                alternatives = alternatives
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = AlternativesUiState.Error(
                                error.message ?: "Failed to find alternatives"
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = AlternativesUiState.Error(
                        error.message ?: "Product not found"
                    )
                }
            )
        }
    }
}

sealed class AlternativesUiState {
    object Loading : AlternativesUiState()
    data class Success(
        val currentProduct: Product,
        val alternatives: List<Alternative>
    ) : AlternativesUiState()
    data class Error(val message: String) : AlternativesUiState()
}
