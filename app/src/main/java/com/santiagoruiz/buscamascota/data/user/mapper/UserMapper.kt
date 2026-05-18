package com.santiagoruiz.buscamascota.data.user.mapper

import com.santiagoruiz.buscamascota.data.user.dto.UserDto
import com.santiagoruiz.buscamascota.domain.model.UserProfile

/** Conversión DTO (Firestore) ↔ modelo de dominio del perfil. */
object UserMapper {

    fun toDomain(dto: UserDto): UserProfile = UserProfile(
        uid = dto.uid,
        displayName = dto.displayName,
        email = dto.email,
        phone = dto.phone,
        photoBase64 = dto.photoBase64,
        createdAt = dto.createdAt?.time ?: 0L,
    )
}
