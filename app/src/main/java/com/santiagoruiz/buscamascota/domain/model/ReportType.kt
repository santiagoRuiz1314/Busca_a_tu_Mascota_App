package com.santiagoruiz.buscamascota.domain.model

/** Tipo de reporte ciudadano. */
enum class ReportType {
    /** Un dueño reporta que su mascota se extravió. */
    LOST,

    /** Alguien reporta haber visto un animal en la calle. */
    SIGHTING,

    /** Denuncia de abuso o maltrato animal. */
    ABUSE,
}
