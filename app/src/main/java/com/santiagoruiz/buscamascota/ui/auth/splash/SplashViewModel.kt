package com.santiagoruiz.buscamascota.ui.auth.splash

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

/** Estado del splash: a dónde derivar según haya sesión o no. */
enum class SplashUiState { Loading, Authenticated, Unauthenticated }

@HiltViewModel
class SplashViewModel @Inject constructor(
    observeAuthState: ObserveAuthStateUseCase,
) : ViewModel() {

    val uiState: StateFlow<SplashUiState> = observeAuthState()
        .map { state ->
            when (state) {
                is AuthState.Authenticated -> SplashUiState.Authenticated
                AuthState.Unauthenticated -> SplashUiState.Unauthenticated
                AuthState.Loading -> SplashUiState.Loading
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SplashUiState.Loading,
        )
}
