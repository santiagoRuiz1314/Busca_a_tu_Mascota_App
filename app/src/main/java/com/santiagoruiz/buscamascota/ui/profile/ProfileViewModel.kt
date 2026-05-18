package com.santiagoruiz.buscamascota.ui.profile

import androidx.lifecycle.ViewModel
import com.santiagoruiz.buscamascota.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    /**
     * Cierra la sesión. La redirección al flujo de auth la maneja de forma
     * reactiva el observador de sesión del grafo raíz.
     */
    fun signOut() = signOutUseCase()
}
