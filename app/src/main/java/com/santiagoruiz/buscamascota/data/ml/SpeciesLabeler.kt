package com.santiagoruiz.buscamascota.data.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Detecta la especie del animal con ML Kit Image Labeling (modelo on-device
 * empaquetado, plan Spark). Actúa como pre-filtro: solo sugiere especie
 * cuando la etiqueta es perro o gato con confianza suficiente; en cualquier
 * otro caso devuelve `null` y el usuario elige manualmente.
 */
@Singleton
class SpeciesLabeler @Inject constructor(
    private val labeler: ImageLabeler,
) {
    suspend fun detect(bitmap: Bitmap): String? = runCatching {
        val image = InputImage.fromBitmap(bitmap, 0)
        val labels = labeler.process(image).await()
        labels
            .filter { it.confidence >= MIN_CONFIDENCE }
            .sortedByDescending { it.confidence }
            .firstNotNullOfOrNull { SPECIES_BY_LABEL[it.text.lowercase()] }
    }.getOrNull()

    private companion object {
        const val MIN_CONFIDENCE = 0.6f

        // Etiquetas del modelo por defecto de ML Kit → especies de la app.
        val SPECIES_BY_LABEL = mapOf(
            "dog" to "Perro",
            "cat" to "Gato",
        )
    }
}
