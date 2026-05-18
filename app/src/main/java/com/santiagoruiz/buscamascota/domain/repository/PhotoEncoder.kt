package com.santiagoruiz.buscamascota.domain.repository

/**
 * Codifica una imagen (referenciada por su URI como string) a JPEG
 * comprimido en base64, listo para guardarse en el documento Firestore.
 */
interface PhotoEncoder {
    suspend fun toBase64(uriString: String): String?
}
