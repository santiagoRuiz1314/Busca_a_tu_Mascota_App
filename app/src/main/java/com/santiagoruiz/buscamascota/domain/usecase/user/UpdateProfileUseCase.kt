package com.santiagoruiz.buscamascota.domain.usecase.user

import com.santiagoruiz.buscamascota.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Valida y guarda los cambios del perfil. El nombre es obligatorio; el
 * teléfono es opcional (en blanco se guarda como nulo). Mensajes en español
 * (se muestran directamente en la UI).
 */
class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        displayName: String,
        phone: String,
        photoBase64: String?,
    ): Result<Unit> {
        val name = displayName.trim()
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Ingresa tu nombre."))
        }
        return userRepository.updateProfile(
            displayName = name,
            phone = phone.trim().ifBlank { null },
            photoBase64 = photoBase64,
        )
    }
}
