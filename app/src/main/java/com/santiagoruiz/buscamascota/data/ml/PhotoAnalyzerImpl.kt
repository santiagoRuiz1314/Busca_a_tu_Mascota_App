package com.santiagoruiz.buscamascota.data.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.santiagoruiz.buscamascota.di.IoDispatcher
import com.santiagoruiz.buscamascota.domain.repository.PhotoAnalyzer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación del puerto [PhotoAnalyzer]: decodifica la foto una vez y
 * delega especie (ML Kit) y embedding (TFLite). La imagen se submuestrea a
 * un tamaño moderado; el extractor la reescala internamente al input del
 * modelo y ML Kit trabaja bien con ese tamaño.
 */
class PhotoAnalyzerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val speciesLabeler: SpeciesLabeler,
    private val embeddingExtractor: EmbeddingExtractor,
) : PhotoAnalyzer {

    override suspend fun detectSpecies(uriString: String): String? {
        val bitmap = decodeBitmap(uriString) ?: return null
        return try {
            speciesLabeler.detect(bitmap)
        } finally {
            bitmap.recycle()
        }
    }

    override suspend fun extractEmbedding(uriString: String): FloatArray? {
        val bitmap = decodeBitmap(uriString) ?: return null
        return try {
            embeddingExtractor.extract(bitmap)
        } finally {
            bitmap.recycle()
        }
    }

    private suspend fun decodeBitmap(uriString: String): Bitmap? =
        withContext(ioDispatcher) {
            runCatching {
                val uri = uriString.toUri()
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, bounds)
                }
                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null

                var sample = 1
                val largest = maxOf(bounds.outWidth, bounds.outHeight)
                while (largest / sample > MAX_DIMENSION * 2) sample *= 2

                val opts = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it, null, opts)
                }
            }.getOrNull()
        }

    private companion object {
        const val MAX_DIMENSION = 512
    }
}
