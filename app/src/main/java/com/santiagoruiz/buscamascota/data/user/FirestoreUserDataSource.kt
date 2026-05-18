package com.santiagoruiz.buscamascota.data.user

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.santiagoruiz.buscamascota.data.user.dto.UserDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Acceso a la colección `users` de Firestore (id de documento = uid). */
@Singleton
class FirestoreUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private val users get() = firestore.collection(COLLECTION)

    /** Escucha en tiempo real el documento de perfil; `null` si no existe. */
    fun observe(uid: String): Flow<UserDto?> = callbackFlow {
        val registration = users.document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(UserDto::class.java))
            }
        awaitClose { registration.remove() }
    }

    /** Lee el perfil por uid, o `null` si no existe. */
    suspend fun getById(uid: String): UserDto? =
        users.document(uid).get().await().toObject(UserDto::class.java)

    /** Crea el documento de perfil (`createdAt` lo pone el servidor). */
    suspend fun create(dto: UserDto) {
        users.document(dto.uid).set(dto).await()
    }

    /**
     * Actualiza solo los campos editables con `merge`: conserva `createdAt`
     * y `email`, y crea el documento si por algún motivo faltara.
     */
    suspend fun updateFields(
        uid: String,
        displayName: String,
        phone: String?,
        photoBase64: String?,
    ) {
        val data = mapOf(
            "displayName" to displayName,
            "phone" to phone,
            "photoBase64" to photoBase64,
        )
        users.document(uid).set(data, SetOptions.merge()).await()
    }

    private companion object {
        const val COLLECTION = "users"
    }
}
