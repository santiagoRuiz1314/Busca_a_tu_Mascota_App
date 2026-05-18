package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.repository.PhotoAnalyzer
import javax.inject.Inject

/** Calcula el embedding visual de la foto para el matching (MobileNet V3). */
class ExtractPhotoEmbeddingUseCase @Inject constructor(
    private val photoAnalyzer: PhotoAnalyzer,
) {
    suspend operator fun invoke(uriString: String): FloatArray? =
        photoAnalyzer.extractEmbedding(uriString)
}
