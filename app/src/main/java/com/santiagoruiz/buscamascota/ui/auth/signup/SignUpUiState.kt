package com.santiagoruiz.buscamascota.ui.auth.signup

/** Estado de la pantalla de registro. */
sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Submitting : SignUpUiState
    data class Error(val message: String) : SignUpUiState
    data object Success : SignUpUiState
}
