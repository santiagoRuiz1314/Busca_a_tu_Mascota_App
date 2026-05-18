package com.santiagoruiz.buscamascota.ui.common.components

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.nio.ByteBuffer

/**
 * Muestra la foto del reporte. La imagen llega como JPEG comprimido en
 * base64 dentro del documento Firestore (no se usa Storage, plan Spark); se
 * decodifica una vez y se entrega a Coil como [ByteBuffer]. Si falta o es
 * inválida, muestra un marcador de posición.
 */
@Composable
fun Base64Image(
    base64: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val bytes = remember(base64) {
        base64?.takeIf { it.isNotBlank() }?.let {
            runCatching { Base64.decode(it, Base64.DEFAULT) }.getOrNull()
        }
    }

    if (bytes == null) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text("🐾", style = MaterialTheme.typography.headlineMedium)
        }
        return
    }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(ByteBuffer.wrap(bytes))
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
    )
}
