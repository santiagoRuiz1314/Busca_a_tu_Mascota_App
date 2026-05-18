package com.santiagoruiz.buscamascota.domain.model

/** Estado del ciclo de vida de un reporte. */
enum class ReportStatus {
    /** Activo, visible en feed y búsquedas. */
    OPEN,

    /** Resuelto (mascota encontrada / caso atendido). */
    RESOLVED,

    /** Archivado, fuera de listados. */
    ARCHIVED,
}
