package com.santiagoruiz.buscamascota.domain.model

/**
 * Identidad del usuario autenticado (sesión). El perfil completo del usuario
 * (foto, teléfono, etc.) vive en Firestore y se modela aparte en su fase.
 */
data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String,
    /**
     * `true` si la sesión es anónima (modo invitado: ve reportes pero no
     * puede crearlos hasta registrarse).
     */
    val isAnonymous: Boolean = false,
)
