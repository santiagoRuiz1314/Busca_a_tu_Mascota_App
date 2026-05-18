package com.santiagoruiz.buscamascota.domain.usecase.auth

import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import javax.inject.Inject

/** Cierra la sesión actual. */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke() = authRepository.signOut()
}
