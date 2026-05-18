package com.santiagoruiz.buscamascota.domain.usecase.auth

import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import javax.inject.Inject

/** Valida las credenciales y delega el inicio de sesión al repositorio. */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        AuthValidation.validateEmail(email)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        AuthValidation.validatePassword(password)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        return authRepository.signIn(email.trim(), password)
    }
}
