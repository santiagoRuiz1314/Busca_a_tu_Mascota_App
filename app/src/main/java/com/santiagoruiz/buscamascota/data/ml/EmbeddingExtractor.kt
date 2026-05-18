package com.santiagoruiz.buscamascota.data.ml

import android.content.Context
import android.graphics.Bitmap
import com.santiagoruiz.buscamascota.di.DefaultDispatcher
import com.santiagoruiz.buscamascota.domain.util.Vectors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extrae el embedding visual de una foto con MobileNet V3 small (TFLite,
 * 100 % on-device). El Interpreter se crea perezosamente una sola vez y se
 * serializa con un [Mutex] (TFLite no es thread-safe). Las formas de los
 * tensores se leen del propio modelo para no asumir 224×224 / 1024.
 */
@Singleton
class EmbeddingExtractor @Inject constructor(
    @ApplicationContext private val context: Context,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {
    private val mutex = Mutex()

    private val interpreter: Interpreter by lazy { Interpreter(loadModel()) }

    private val inputShape: IntArray by lazy { interpreter.getInputTensor(0).shape() }
    private val outputSize: Int by lazy { interpreter.getOutputTensor(0).shape().last() }

    /** Embedding L2-normalizado, o `null` si la inferencia falla. */
    suspend fun extract(bitmap: Bitmap): FloatArray? = withContext(dispatcher) {
        runCatching {
            mutex.withLock {
                // inputShape = [1, alto, ancho, 3] (NHWC).
                val height = inputShape[1]
                val width = inputShape[2]
                val input = ImagePreprocessor.toModelInput(bitmap, width, height)
                val output = Array(1) { FloatArray(outputSize) }
                interpreter.run(input, output)
                Vectors.l2Normalize(output[0])
            }
        }.getOrNull()
    }

    private fun loadModel(): MappedByteBuffer =
        context.assets.openFd(MODEL_ASSET).use { afd ->
            FileInputStream(afd.fileDescriptor).use { fis ->
                fis.channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    afd.startOffset,
                    afd.declaredLength,
                )
            }
        }

    private companion object {
        const val MODEL_ASSET = "mobilenet_v3_small.tflite"
    }
}
