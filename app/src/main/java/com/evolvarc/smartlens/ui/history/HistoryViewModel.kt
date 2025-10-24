package com.evolvarc.smartlens.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolvarc.smartlens.data.repository.ProductRepository
import com.evolvarc.smartlens.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    
    val uiState: StateFlow<HistoryUiState> = repository.getScanHistory()
        .map { products ->
            if (products.isEmpty()) {
                HistoryUiState.Empty
            } else {
                HistoryUiState.Success(products)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState.Loading
        )
}

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    object Empty : HistoryUiState()
    data class Success(val products: List<Product>) : HistoryUiState()
}
