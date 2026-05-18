package com.santiagoruiz.buscamascota.ui.auth.signin

/** Estado de la pantalla de inicio de sesión. */
sealed interface SignInUiState {
    data object Idle : SignInUiState
    data object Submitting : SignInUiState
    data class Error(val message: String) : SignInUiState
    data object Success : SignInUiState
}
