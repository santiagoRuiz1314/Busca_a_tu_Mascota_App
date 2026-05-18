package com.santiagoruiz.buscamascota.domain.usecase.auth

import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import javax.inject.Inject

/** Valida los datos de registro y delega la creación de cuenta al repositorio. */
class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
    ): Result<Unit> {
        AuthValidation.validateName(name)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        AuthValidation.validateEmail(email)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        AuthValidation.validatePassword(password)?.let {
            return Result.failure(IllegalArgumentException(it))
        }
        return authRepository.signUp(name.trim(), email.trim(), password)
    }
}
