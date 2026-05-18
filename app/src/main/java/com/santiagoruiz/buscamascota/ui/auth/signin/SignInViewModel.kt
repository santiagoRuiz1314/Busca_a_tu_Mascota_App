package com.santiagoruiz.buscamascota.ui.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.usecase.auth.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (_uiState.value is SignInUiState.Submitting) return
        _uiState.value = SignInUiState.Submitting
        viewModelScope.launch {
            _uiState.value = signInUseCase(email, password).fold(
                onSuccess = { SignInUiState.Success },
                onFailure = {
                    SignInUiState.Error(it.message ?: "No se pudo iniciar sesión.")
                },
            )
        }
    }

    /** Vuelve a estado editable tras mostrar un error. */
    fun clearError() {
        if (_uiState.value is SignInUiState.Error) _uiState.value = SignInUiState.Idle
    }
}
