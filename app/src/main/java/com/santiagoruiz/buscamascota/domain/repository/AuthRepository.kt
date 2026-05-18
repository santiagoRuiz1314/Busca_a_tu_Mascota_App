package com.santiagoruiz.buscamascota.domain.repository

import com.santiagoruiz.buscamascota.domain.model.AuthState
import com.santiagoruiz.buscamascota.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de autenticación. La implementación (Firebase Auth) vive en la
 * capa data; el dominio no conoce Firebase.
 */
interface AuthRepository {

    /** Emite el estado de sesión y reacciona a login/logout. */
    val authState: Flow<AuthState>

    /** Usuario autenticado actual, o null si no hay sesión. */
    val currentUser: AuthUser?

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun signUp(name: String, email: String, password: String): Result<Unit>

    fun signOut()
}
