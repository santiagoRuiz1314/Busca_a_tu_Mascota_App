package com.santiagoruiz.buscamascota.domain.model

/**
 * Datos del animal del reporte. `species` lo autodetecta ML Kit en la
 * Fase 6; por ahora se ingresa manualmente. `name` solo aplica a perdidos.
 */
data class AnimalInfo(
    val species: String,
    val breed: String? = null,
    val color: String? = null,
    val name: String? = null,
)
