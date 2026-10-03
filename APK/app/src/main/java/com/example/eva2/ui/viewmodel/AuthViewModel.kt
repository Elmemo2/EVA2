package com.example.eva2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eva2.data.repository.ApiAuthRepository
import com.example.eva2.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val message: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository = ApiAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Campos Login
    var loginRut by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set
    var isLoginPasswordVisible by mutableStateOf(false)
        private set

    // Campos Registro
    var registerNombre by mutableStateOf("")
        private set
    var registerRut by mutableStateOf("")
        private set
    var registerPassword by mutableStateOf("")
        private set
    var registerConfirmPassword by mutableStateOf("")
        private set
    var isRegisterPasswordVisible by mutableStateOf(false)
        private set
    var isRegisterConfirmPasswordVisible by mutableStateOf(false)
        private set

    fun onLoginRutChange(value: String) {
        loginRut = value
        resetStateIfError()
    }

    fun onLoginPasswordChange(value: String) {
        loginPassword = value
        resetStateIfError()
    }

    fun toggleLoginPasswordVisibility() {
        isLoginPasswordVisible = !isLoginPasswordVisible
    }

    fun onRegisterNombreChange(value: String) {
        registerNombre = value
        resetStateIfError()
    }

    fun onRegisterRutChange(value: String) {
        registerRut = value
        resetStateIfError()
    }

    fun onRegisterPasswordChange(value: String) {
        registerPassword = value
        resetStateIfError()
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        registerConfirmPassword = value
        resetStateIfError()
    }

    fun toggleRegisterPasswordVisibility() {
        isRegisterPasswordVisible = !isRegisterPasswordVisible
    }

    fun toggleRegisterConfirmPasswordVisibility() {
        isRegisterConfirmPasswordVisible = !isRegisterConfirmPasswordVisible
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun resetStateIfError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    private fun isValidRutFormat(rut: String): Boolean {
        val cleanRut = rut.replace(".", "").replace("-", "").trim()
        return cleanRut.length in 8..9
    }

    fun login(onSuccess: () -> Unit) {
        val rut = loginRut.trim()
        val password = loginPassword.trim()

        if (rut.isEmpty() || password.isEmpty()) {
            _uiState.value = AuthUiState.Error("Por favor, ingrese RUT y contraseña.")
            return
        }

        if (!isValidRutFormat(rut)) {
            _uiState.value = AuthUiState.Error("El RUT ingresado no es válido.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(rut, password)
            result.onSuccess { response ->
                if (response.success) {
                    _uiState.value = AuthUiState.Success(response.message)
                    onSuccess()
                } else {
                    _uiState.value = AuthUiState.Error(response.message)
                }
            }.onFailure {
                _uiState.value = AuthUiState.Error("No se pudo conectar con el servidor. Inténtalo nuevamente.")
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val nombre = registerNombre.trim()
        val rut = registerRut.trim()
        val password = registerPassword.trim()
        val confirmPassword = registerConfirmPassword.trim()

        if (nombre.isEmpty() || rut.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _uiState.value = AuthUiState.Error("Todos los campos son obligatorios.")
            return
        }

        if (!isValidRutFormat(rut)) {
            _uiState.value = AuthUiState.Error("El RUT ingresado no es válido.")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("Las contraseñas no coinciden.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(nombre, rut, password)
            result.onSuccess { response ->
                if (response.success) {
                    _uiState.value = AuthUiState.Success(response.message)
                    // Limpiar formulario de registro
                    registerNombre = ""
                    registerRut = ""
                    registerPassword = ""
                    registerConfirmPassword = ""
                    onSuccess()
                } else {
                    _uiState.value = AuthUiState.Error(response.message)
                }
            }.onFailure {
                _uiState.value = AuthUiState.Error("No se pudo conectar con el servidor. Inténtalo nuevamente.")
            }
        }
    }
}
