package com.santiagoruiz.buscamascota.domain.usecase.auth

/**
 * Validaciones de entrada compartidas por los casos de uso de auth.
 * Mensajes en español (se muestran directamente en la UI).
 */
internal object AuthValidation {

    private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    const val MIN_PASSWORD_LENGTH = 6

    /** @return mensaje de error, o null si es válido. */
    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Ingresa tu correo."
        !EMAIL_REGEX.matches(email.trim()) -> "El correo no tiene un formato válido."
        else -> null
    }

    fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Ingresa tu contraseña."
        password.length < MIN_PASSWORD_LENGTH ->
            "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres."
        else -> null
    }

    fun validateName(name: String): String? =
        if (name.isBlank()) "Ingresa tu nombre." else null
}
