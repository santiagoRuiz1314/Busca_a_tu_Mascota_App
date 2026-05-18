package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.repository.PhotoEncoder
import javax.inject.Inject

class EncodeReportPhotoUseCase @Inject constructor(
    private val photoEncoder: PhotoEncoder,
) {
    suspend operator fun invoke(uriString: String): String? =
        photoEncoder.toBase64(uriString)
}
