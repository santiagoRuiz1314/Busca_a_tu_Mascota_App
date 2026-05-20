package com.santiagoruiz.buscamascota.domain.usecase.auth

import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import javax.inject.Inject

/** Inicia una sesión anónima (modo invitado: ver reportes sin registrarse). */
class SignInAnonymouslyUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> = authRepository.signInAnonymously()
}
