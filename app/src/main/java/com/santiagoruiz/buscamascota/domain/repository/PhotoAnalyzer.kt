package com.santiagoruiz.buscamascota.domain.repository

/**
 * Análisis de la foto del reporte 100 % on-device (plan Spark). La
 * implementación (ML Kit + TFLite) vive en la capa data; el dominio solo
 * conoce este contrato y trabaja con la imagen referenciada por su URI.
 */
interface PhotoAnalyzer {

    /**
     * Especie detectada y normalizada a una de las opciones de la app
     * ("Perro" / "Gato"), o `null` si no hay una detección confiable.
     */
    suspend fun detectSpecies(uriString: String): String?

    /**
     * Embedding visual L2-normalizado (~1024 dims) para el matching por
     * similitud coseno, o `null` si la imagen no se pudo procesar.
     */
    suspend fun extractEmbedding(uriString: String): FloatArray?
}
