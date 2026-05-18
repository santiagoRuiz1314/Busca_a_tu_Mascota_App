package com.santiagoruiz.buscamascota.domain.repository

import com.santiagoruiz.buscamascota.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del perfil de usuario (colección `users` de Firestore). La
 * implementación vive en la capa data; el dominio no conoce Firebase.
 */
interface UserRepository {

    /**
     * Observa el documento de perfil del usuario autenticado. Emite `null`
     * si aún no existe (cuenta creada antes de la Fase 7) o no hay sesión.
     */
    fun observeProfile(): Flow<UserProfile?>

    /**
     * Crea el documento `users/{uid}` a partir de la identidad de sesión si
     * todavía no existe. Idempotente: si ya existe no lo sobrescribe (no
     * pisa el nombre/teléfono/foto que el usuario haya editado).
     */
    suspend fun ensureProfile(): Result<Unit>

    /** Actualiza los campos editables del perfil del usuario actual. */
    suspend fun updateProfile(
        displayName: String,
        phone: String?,
        photoBase64: String?,
    ): Result<Unit>
}
