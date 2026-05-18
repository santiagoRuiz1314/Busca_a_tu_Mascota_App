package com.santiagoruiz.buscamascota.ui.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.ui.theme.appColors

// Centro por defecto: Bucaramanga (UNAB), si no hay ubicación automática.
private val DEFAULT_CENTER = LatLng(7.1193, -73.1227)

/**
 * Selección de ubicación en mapa. El usuario mueve el mapa hasta dejar el
 * punto donde ocurre el reporte y confirma; se devuelve el centro de cámara.
 * Funciona sin permiso de ubicación: es justamente el respaldo manual
 * cuando la detección automática no reconoce dónde está.
 */
@Composable
fun LocationPicker(
    initial: GeoPoint?,
    onConfirm: (latitude: Double, longitude: Double) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val start = initial?.let { LatLng(it.latitude, it.longitude) } ?: DEFAULT_CENTER
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(start, 16f)
    }

    // Si la ubicación automática llega después, recentra el mapa una vez.
    LaunchedEffect(initial) {
        if (initial != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(initial.latitude, initial.longitude),
                16f,
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
        )

        // Pin fijo en el centro: la punta apunta al centro exacto de cámara.
        Text(
            text = "📍",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-18).dp),
        )

        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = "Mueve el mapa para ubicar el punto del reporte",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = {
                    val target = cameraPositionState.position.target
                    onConfirm(target.latitude, target.longitude)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.appColors.primaryAction,
                    contentColor = MaterialTheme.appColors.onPrimaryAction,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("CONFIRMAR UBICACIÓN")
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Cancelar")
            }
        }
    }
}
