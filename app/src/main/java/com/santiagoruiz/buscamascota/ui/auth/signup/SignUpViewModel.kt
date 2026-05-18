package com.santiagoruiz.buscamascota.ui.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun signUp(name: String, email: String, password: String) {
        if (_uiState.value is SignUpUiState.Submitting) return
        _uiState.value = SignUpUiState.Submitting
        viewModelScope.launch {
            _uiState.value = signUpUseCase(name, email, password).fold(
                onSuccess = { SignUpUiState.Success },
                onFailure = {
                    SignUpUiState.Error(it.message ?: "No se pudo crear la cuenta.")
                },
            )
        }
    }

    /** Vuelve a estado editable tras mostrar un error. */
    fun clearError() {
        if (_uiState.value is SignUpUiState.Error) _uiState.value = SignUpUiState.Idle
    }
}
