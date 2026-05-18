package com.santiagoruiz.buscamascota.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.AuthState
import com.santiagoruiz.buscamascota.domain.usecase.auth.ObserveAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Observa la sesión a nivel de grafo raíz para reaccionar a logout o
 * expiración de sesión (redirección reactiva al flujo de auth).
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    observeAuthState: ObserveAuthStateUseCase,
) : ViewModel() {

    /** `true` con sesión, `false` sin sesión, `null` aún sin resolver. */
    val isAuthenticated: StateFlow<Boolean?> = observeAuthState()
        .map { state ->
            when (state) {
                is AuthState.Authenticated -> true
                AuthState.Unauthenticated -> false
                AuthState.Loading -> null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
}
