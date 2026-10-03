package com.example.eva2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eva2.data.model.VehicleHistory
import com.example.eva2.data.repository.ApiVehicleRepository
import com.example.eva2.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HistoryUiState {
    object Loading : HistoryUiState
    data class Success(val historyList: List<VehicleHistory>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

class HistoryViewModel(
    private val vehicleRepository: VehicleRepository = ApiVehicleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadVehicleHistory()
    }

    fun loadVehicleHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            val result = vehicleRepository.getVehicleHistory()
            result.onSuccess { list ->
                _uiState.value = HistoryUiState.Success(list)
            }.onFailure { exception ->
                val errorMessage = exception.message?.takeIf { it.isNotBlank() }
                    ?: "No se pudo conectar con el servidor. Verifica tu conexión WiFi."
                _uiState.value = HistoryUiState.Error(errorMessage)
            }
        }
    }
}
