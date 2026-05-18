package com.santiagoruiz.buscamascota.data.image

import androidx.core.net.toUri
import com.santiagoruiz.buscamascota.domain.repository.PhotoEncoder
import javax.inject.Inject

class PhotoEncoderImpl @Inject constructor(
    private val imageCompressor: ImageCompressor,
) : PhotoEncoder {

    override suspend fun toBase64(uriString: String): String? =
        runCatching { imageCompressor.compressToBase64(uriString.toUri()) }.getOrNull()
}
