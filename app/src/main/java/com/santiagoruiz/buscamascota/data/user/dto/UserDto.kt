package com.santiagoruiz.buscamascota.data.user.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Representación del perfil en Firestore (colección `users`, id = uid).
 * POJO con constructor sin argumentos (valores por defecto) y tipos
 * serializables. Nunca se expone fuera de la capa data.
 */
data class UserDto(
    @DocumentId val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String? = null,
    val photoBase64: String? = null,
    @ServerTimestamp val createdAt: Date? = null,
)
