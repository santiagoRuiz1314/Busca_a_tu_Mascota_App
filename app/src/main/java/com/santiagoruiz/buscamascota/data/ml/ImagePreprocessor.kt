package com.santiagoruiz.buscamascota.data.ml

import android.graphics.Bitmap
import androidx.core.graphics.scale
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Prepara un [Bitmap] como entrada del modelo MobileNet V3: lo escala a
 * [width] × [height] y normaliza cada canal RGB a [0, 1] en un buffer float
 * directo (orden NHWC, nativo), que es lo que espera el Interpreter TFLite.
 *
 * Lo importante para el matching no es la normalización "ideal" sino que sea
 * idéntica para el embedding guardado y el de consulta: la similitud coseno
 * es consistente mientras el preprocesamiento sea el mismo.
 */
object ImagePreprocessor {

    private const val BYTES_PER_CHANNEL = 4 // float32
    private const val CHANNELS = 3

    fun toModelInput(bitmap: Bitmap, width: Int, height: Int): ByteBuffer {
        val resized = if (bitmap.width == width && bitmap.height == height) {
            bitmap
        } else {
            bitmap.scale(width, height)
        }

        val buffer = ByteBuffer
            .allocateDirect(width * height * CHANNELS * BYTES_PER_CHANNEL)
            .order(ByteOrder.nativeOrder())

        val pixels = IntArray(width * height)
        resized.getPixels(pixels, 0, width, 0, 0, width, height)
        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16) and 0xFF) / 255f) // R
            buffer.putFloat(((pixel shr 8) and 0xFF) / 255f)  // G
            buffer.putFloat((pixel and 0xFF) / 255f)          // B
        }

        if (resized != bitmap) resized.recycle()
        buffer.rewind()
        return buffer
    }
}
