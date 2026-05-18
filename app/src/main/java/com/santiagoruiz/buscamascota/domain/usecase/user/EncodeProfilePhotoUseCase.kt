package com.santiagoruiz.buscamascota.domain.usecase.user

import com.santiagoruiz.buscamascota.domain.repository.PhotoEncoder
import javax.inject.Inject

/**
 * Codifica la foto de perfil elegida (URI) a JPEG comprimido en base64.
 * Reutiliza el mismo [PhotoEncoder] que los reportes: la foto se guarda en
 * el documento Firestore (plan Spark, sin Storage).
 */
class EncodeProfilePhotoUseCase @Inject constructor(
    private val photoEncoder: PhotoEncoder,
) {
    suspend operator fun invoke(uriString: String): String? =
        photoEncoder.toBase64(uriString)
}
