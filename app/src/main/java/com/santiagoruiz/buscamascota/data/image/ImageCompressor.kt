package com.santiagoruiz.buscamascota.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.santiagoruiz.buscamascota.di.DefaultDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Comprime una imagen a JPEG y la codifica en base64 para guardarla dentro
 * del documento Firestore (no se usa Firebase Storage; plan Spark).
 *
 * Objetivo: lado mayor ≤ [MAX_DIMENSION] px, calidad [JPEG_QUALITY],
 * resultando en ~50-200 KB (dentro del límite de 1 MB por documento).
 */
class ImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {

    suspend fun compressToBase64(uri: Uri): String? = withContext(dispatcher) {
        val bitmap = decodeSampled(uri) ?: return@withContext null
        val scaled = scaleDown(bitmap)
        val bytes = ByteArrayOutputStream().use { out ->
            scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            out.toByteArray()
        }
        if (scaled != bitmap) scaled.recycle()
        bitmap.recycle()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun decodeSampled(uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        val largest = maxOf(bounds.outWidth, bounds.outHeight)
        while (largest / sample > MAX_DIMENSION * 2) sample *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        }
    }

    private fun scaleDown(bitmap: Bitmap): Bitmap {
        val largest = maxOf(bitmap.width, bitmap.height)
        if (largest <= MAX_DIMENSION) return bitmap
        val ratio = MAX_DIMENSION.toFloat() / largest
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * ratio).toInt(),
            (bitmap.height * ratio).toInt(),
            true,
        )
    }

    private companion object {
        const val MAX_DIMENSION = 800
        const val JPEG_QUALITY = 60
    }
}
