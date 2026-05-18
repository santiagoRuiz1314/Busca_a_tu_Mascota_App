package com.santiagoruiz.buscamascota.domain.model

/**
 * Coincidencia visual entre un reporte objetivo (p. ej. un animal perdido)
 * y un candidato (p. ej. un avistamiento) en la misma zona geográfica.
 * [similarity] es la similitud coseno de los embeddings en [0, 1].
 */
data class VisualMatch(
    val report: Report,
    val similarity: Float,
)
