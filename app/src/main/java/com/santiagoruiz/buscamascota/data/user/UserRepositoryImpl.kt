package com.santiagoruiz.buscamascota.data.user

import com.santiagoruiz.buscamascota.data.common.toFirestoreErrorMessage
import com.santiagoruiz.buscamascota.data.user.dto.UserDto
import com.santiagoruiz.buscamascota.data.user.mapper.UserMapper
import com.santiagoruiz.buscamascota.di.IoDispatcher
import com.santiagoruiz.buscamascota.domain.model.UserProfile
import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import com.santiagoruiz.buscamascota.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación de [UserRepository] sobre Firestore. El uid sale de la
 * sesión ([AuthRepository]): el dominio nunca lo pasa, así un usuario solo
 * puede leer/escribir su propio perfil. Los errores se traducen a español
 * vía [toFirestoreErrorMessage] para no ocultar la causa real (reglas).
 */
class UserRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreUserDataSource,
    private val authRepository: AuthRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    override fun observeProfile(): Flow<UserProfile?> {
        val uid = authRepository.currentUser?.uid
            ?: return flowOf(null)
        return dataSource.observe(uid)
            .map { dto -> dto?.let(UserMapper::toDomain) }
            // El snapshot listener reemite (caché → servidor, metadata);
            // colapsar emisiones idénticas evita recomponer el perfil.
            .distinctUntilChanged()
            .flowOn(ioDispatcher)
            .catch { throw IllegalStateException(it.toFirestoreErrorMessage(), it) }
    }

    override suspend fun ensureProfile(): Result<Unit> =
        withContext(ioDispatcher) {
            val user = authRepository.currentUser
                ?: return@withContext Result.failure(
                    IllegalStateException("Debes iniciar sesión."),
                )
            try {
                if (dataSource.getById(user.uid) == null) {
                    dataSource.create(
                        UserDto(
                            uid = user.uid,
                            displayName = user.displayName,
                            email = user.email,
                        ),
                    )
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(IllegalStateException(e.toFirestoreErrorMessage(), e))
            }
        }

    override suspend fun updateProfile(
        displayName: String,
        phone: String?,
        photoBase64: String?,
    ): Result<Unit> = withContext(ioDispatcher) {
        val uid = authRepository.currentUser?.uid
            ?: return@withContext Result.failure(
                IllegalStateException("Debes iniciar sesión."),
            )
        try {
            dataSource.updateFields(uid, displayName, phone, photoBase64)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(IllegalStateException(e.toFirestoreErrorMessage(), e))
        }
    }
}
