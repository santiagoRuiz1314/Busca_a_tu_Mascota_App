package com.santiagoruiz.buscamascota.domain.model

/**
 * Estado de sesión observable. `Loading` es el estado inicial mientras se
 * resuelve la primera emisión (lo usa el splash para decidir destino).
 */
sealed interface AuthState {
    data object Loading : AuthState
    data class Authenticated(val user: AuthUser) : AuthState
    data object Unauthenticated : AuthState
}
