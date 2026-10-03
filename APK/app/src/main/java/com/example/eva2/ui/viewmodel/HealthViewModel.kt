package com.example.eva2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eva2.data.model.HealthResponse
import com.example.eva2.data.repository.ApiHealthRepository
import com.example.eva2.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HealthUiState {
    object Loading : HealthUiState
    data class Success(val health: HealthResponse) : HealthUiState
    data class Error(val message: String) : HealthUiState
}

class HealthViewModel(
    private val healthRepository: HealthRepository = ApiHealthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HealthUiState>(HealthUiState.Loading)
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    init {
        checkHealth()
    }

    fun checkHealth() {
        viewModelScope.launch {
            _uiState.value = HealthUiState.Loading
            healthRepository.checkHealth()
                .onSuccess { healthResponse ->
                    _uiState.value = HealthUiState.Success(healthResponse)
                }
                .onFailure { exception ->
                    _uiState.value = HealthUiState.Error(
                        exception.message ?: "No se pudo conectar con la API."
                    )
                }
        }
    }
}
