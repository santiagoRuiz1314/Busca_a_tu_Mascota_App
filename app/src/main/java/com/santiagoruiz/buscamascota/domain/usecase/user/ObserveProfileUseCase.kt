package com.santiagoruiz.buscamascota.domain.usecase.user

import com.santiagoruiz.buscamascota.domain.model.UserProfile
import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import com.santiagoruiz.buscamascota.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Observa el perfil del usuario. Si el documento aún no existe (cuenta
 * previa a la Fase 7, o sin conexión antes del primer guardado) cae a la
 * identidad de sesión, de modo que la pantalla siempre tiene datos que
 * mostrar mientras [EnsureUserProfileUseCase] crea el documento real.
 */
class ObserveProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<UserProfile> =
        userRepository.observeProfile().map { profile ->
            profile ?: authRepository.currentUser?.let {
                UserProfile(uid = it.uid, displayName = it.displayName, email = it.email)
            } ?: UserProfile()
        }
}
