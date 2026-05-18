package com.santiagoruiz.buscamascota.domain.usecase.user

import com.santiagoruiz.buscamascota.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Garantiza que exista el documento `users/{uid}`. Se invoca al abrir el
 * perfil: crea el documento de forma perezosa para cuentas registradas
 * antes de la Fase 7 sin obligar a volver a iniciar sesión. Idempotente.
 */
class EnsureUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Result<Unit> = userRepository.ensureProfile()
}
