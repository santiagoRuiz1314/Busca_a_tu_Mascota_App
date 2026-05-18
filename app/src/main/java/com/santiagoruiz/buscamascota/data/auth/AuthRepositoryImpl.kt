package com.santiagoruiz.buscamascota.data.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.santiagoruiz.buscamascota.di.IoDispatcher
import com.santiagoruiz.buscamascota.domain.model.AuthState
import com.santiagoruiz.buscamascota.domain.model.AuthUser
import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación de [AuthRepository] sobre Firebase Auth. Ejecuta las
 * operaciones de red en el dispatcher de IO y traduce las excepciones de
 * Firebase a mensajes en español listos para mostrar.
 */
class AuthRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseAuthDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override val authState: Flow<AuthState> = dataSource.authState

    override val currentUser: AuthUser?
        get() = dataSource.currentUser

    override suspend fun signIn(email: String, password: String): Result<Unit> =
        runAuth { dataSource.signIn(email, password) }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ): Result<Unit> = runAuth { dataSource.signUp(name, email, password) }

    override fun signOut() = dataSource.signOut()

    private suspend inline fun runAuth(crossinline block: suspend () -> Unit): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                block()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(IllegalStateException(e.toSpanishMessage(), e))
            }
        }

    private fun Exception.toSpanishMessage(): String = when (this) {
        is FirebaseAuthWeakPasswordException ->
            "La contraseña es demasiado débil."
        is FirebaseAuthInvalidCredentialsException ->
            "Correo o contraseña incorrectos."
        is FirebaseAuthInvalidUserException ->
            "No existe una cuenta con ese correo."
        is FirebaseAuthUserCollisionException ->
            "Ya existe una cuenta con ese correo."
        is FirebaseNetworkException ->
            "Sin conexión. Revisa tu internet e intenta de nuevo."
        else -> "Ocurrió un error. Intenta de nuevo."
    }
}
