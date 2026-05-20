package com.santiagoruiz.buscamascota.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.AuthState
import com.santiagoruiz.buscamascota.domain.usecase.auth.ObserveAuthStateUseCase
import com.santiagoruiz.buscamascota.domain.usecase.auth.SignInAnonymouslyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A dónde derivar al arrancar (lo decide el grafo raíz tras el splash).
 * `Active` cubre tanto sesión real como invitado anónimo.
 */
sealed interface SessionState {
    data object Loading : SessionState
    data object Active : SessionState
    data object ManualAuthRequired : SessionState
}

/**
 * Orquesta la sesión a nivel de grafo raíz. Garantiza que SIEMPRE haya
 * sesión: cuando no hay ninguna (arranque en frío o tras cerrar sesión)
 * inicia una sesión anónima, de modo que el modo invitado puede leer
 * reportes (las reglas de Firestore exigen `request.auth != null`).
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    observeAuthState: ObserveAuthStateUseCase,
    private val signInAnonymously: SignInAnonymouslyUseCase,
) : ViewModel() {

    private val authState = observeAuthState()

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    /** `true` para invitado (anónimo o aún sin resolver); `false` solo con cuenta real. */
    val isGuest: StateFlow<Boolean> = authState
        .map { it !is AuthState.Authenticated || it.user.isAnonymous }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    // Evita disparar varios sign-in anónimos por el mismo episodio sin sesión.
    private var anonymousInFlight = false

    init {
        viewModelScope.launch {
            authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        anonymousInFlight = false
                        _sessionState.value = SessionState.Active
                    }

                    AuthState.Loading ->
                        _sessionState.value = SessionState.Loading

                    AuthState.Unauthenticated -> if (!anonymousInFlight) {
                        anonymousInFlight = true
                        _sessionState.value = SessionState.Loading
                        // Si tiene éxito, Firebase emite Authenticated y la
                        // rama de arriba pasa a Active. Si falla (proveedor
                        // anónimo deshabilitado o sin red), caemos al login.
                        signInAnonymously().onFailure {
                            _sessionState.value = SessionState.ManualAuthRequired
                        }
                    }
                }
            }
        }
    }
}
