package com.santiagoruiz.buscamascota.ui.common.format

import com.santiagoruiz.buscamascota.domain.model.ReportStatus
import com.santiagoruiz.buscamascota.domain.model.ReportType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Etiquetas y formato de reportes para la UI (en español). */

fun ReportType.displayName(): String = when (this) {
    ReportType.LOST -> "Perdido"
    ReportType.SIGHTING -> "Avistamiento"
    ReportType.ABUSE -> "Maltrato"
}

fun ReportStatus.displayName(): String = when (this) {
    ReportStatus.OPEN -> "Activo"
    ReportStatus.RESOLVED -> "Resuelto"
    ReportStatus.ARCHIVED -> "Archivado"
}

/** Tiempo relativo legible; cae a fecha absoluta tras una semana. */
fun relativeTime(epochMillis: Long, now: Long = System.currentTimeMillis()): String {
    if (epochMillis <= 0L) return "Hace un momento"
    val minutes = (now - epochMillis).coerceAtLeast(0L) / 60_000L
    return when {
        minutes < 1L -> "Hace un momento"
        minutes < 60L -> "Hace $minutes min"
        minutes < 1_440L -> "Hace ${minutes / 60L} h"
        minutes < 10_080L -> "Hace ${minutes / 1_440L} d"
        else -> SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es"))
            .format(Date(epochMillis))
    }
}
