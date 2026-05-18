package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.repository.PhotoAnalyzer
import javax.inject.Inject

/** Sugiere la especie del animal a partir de la foto (pre-filtro ML Kit). */
class DetectSpeciesUseCase @Inject constructor(
    private val photoAnalyzer: PhotoAnalyzer,
) {
    suspend operator fun invoke(uriString: String): String? =
        photoAnalyzer.detectSpecies(uriString)
}
