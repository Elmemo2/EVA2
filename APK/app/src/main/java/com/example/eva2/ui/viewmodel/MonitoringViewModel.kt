package com.example.eva2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eva2.data.model.VehicleData
import com.example.eva2.data.repository.ApiVehicleRepository
import com.example.eva2.data.repository.VehicleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MonitoringUiState {
    object Loading : MonitoringUiState
    data class Success(val vehicleData: VehicleData) : MonitoringUiState
    data class Error(val message: String) : MonitoringUiState
}

class MonitoringViewModel(
    private val vehicleRepository: VehicleRepository = ApiVehicleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MonitoringUiState>(MonitoringUiState.Loading)
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    init {
        startVehicleMonitoring()
    }

    private fun startVehicleMonitoring() {
        viewModelScope.launch {
            while (true) {
                fetchVehicleData()
                delay(2000)
            }
        }
    }

    fun loadVehicleData() {
        viewModelScope.launch {
            fetchVehicleData()
        }
    }

    private suspend fun fetchVehicleData() {
        // Mostrar Loading únicamente si aún no hay datos válidos cargados
        if (_uiState.value !is MonitoringUiState.Success) {
            _uiState.value = MonitoringUiState.Loading
        }

        val result = vehicleRepository.getVehicleData()
        result.onSuccess { data ->
            _uiState.value = MonitoringUiState.Success(data)
        }.onFailure { exception ->
            // Mantiene los últimos datos válidos en pantalla si falla una actualización periódica
            if (_uiState.value !is MonitoringUiState.Success) {
                val errorMessage = exception.message?.takeIf { it.isNotBlank() }
                    ?: "No se pudo conectar con el servidor. Verifica tu conexión WiFi."
                _uiState.value = MonitoringUiState.Error(errorMessage)
            }
        }
    }
}
