package com.santiagoruiz.buscamascota.ui.profile

import com.santiagoruiz.buscamascota.domain.model.UserProfile

/** Estado de la pantalla de perfil (vista de solo lectura). */
sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Error(val message: String) : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
}

/**
 * Estado del formulario de edición. El ViewModel lo expone como `null`
 * cuando no se está editando; al editar lleva una copia mutable de los
 * campos para no tocar el perfil observado hasta guardar.
 */
data class ProfileEditState(
    val displayName: String,
    val phone: String,
    val photoBase64: String?,
    val processingPhoto: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null,
)
