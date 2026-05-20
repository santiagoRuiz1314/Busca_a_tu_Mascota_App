package com.santiagoruiz.buscamascota.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.santiagoruiz.buscamascota.domain.model.AuthState
import com.santiagoruiz.buscamascota.domain.model.AuthUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fuente de datos sobre Firebase Authentication. Aísla el SDK de Firebase
 * del resto de la app y traduce sus tipos a modelos de dominio.
 */
@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {

    val currentUser: AuthUser?
        get() = firebaseAuth.currentUser?.toAuthUser()

    /** Emite el estado de sesión y reacciona a cada cambio de auth. */
    val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(
                if (user != null) AuthState.Authenticated(user.toAuthUser())
                else AuthState.Unauthenticated,
            )
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(name: String, email: String, password: String) {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val profileUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        result.user?.updateProfile(profileUpdate)?.await()
    }

    suspend fun signInAnonymously() {
        firebaseAuth.signInAnonymously().await()
    }

    fun signOut() = firebaseAuth.signOut()

    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName.orEmpty(),
        isAnonymous = isAnonymous,
    )
}
