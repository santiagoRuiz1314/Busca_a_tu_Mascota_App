package com.santiagoruiz.buscamascota.domain.model

/**
 * Perfil del usuario tal como vive en Firestore (`users/{uid}`). Es distinto
 * de [AuthUser] (identidad de sesión de Firebase Auth): este modelo guarda
 * los datos editables del perfil.
 *
 * La foto se guarda como JPEG comprimido en base64 dentro del documento
 * (mismo criterio que los reportes: plan Spark, sin Firebase Storage).
 */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String? = null,
    val photoBase64: String? = null,
    val createdAt: Long = 0L,
)
