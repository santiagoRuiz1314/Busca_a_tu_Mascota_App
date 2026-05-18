package com.santiagoruiz.buscamascota.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import com.santiagoruiz.buscamascota.ui.common.format.displayName
import com.santiagoruiz.buscamascota.ui.common.format.relativeTime

// Centro por defecto: Bucaramanga (UNAB). La cámara solo se mueve de aquí
// para encuadrar reportes reales (ver más abajo).
private val DEFAULT_CENTER = LatLng(7.1193, -73.1227)

/** Color del marcador por tipo de reporte (coherente con [ReportTypeBadge]). */
private fun ReportType.markerHue(): Float = when (this) {
    ReportType.LOST -> BitmapDescriptorFactory.HUE_RED
    ReportType.SIGHTING -> BitmapDescriptorFactory.HUE_AZURE
    ReportType.ABUSE -> BitmapDescriptorFactory.HUE_ORANGE
}

/**
 * Mapa del feed: los reportes activos como marcadores geolocalizados. Al
 * tocar un marcador se abre su ventana de información; al tocar la ventana
 * se navega al detalle. Con permiso de ubicación se muestra el punto azul
 * y el botón "mi ubicación" para centrarse uno mismo manualmente; la cámara
 * nunca se centra automáticamente en el GPS (en emulador es de prueba).
 */
@Composable
fun MapScreen(
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasLocationPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_CENTER, 12f)
    }

    val reports = (state as? ReportListUiState.Success)?.reports.orEmpty()

    // La cámara solo se mueve para encuadrar reportes reales, y una sola
    // vez. Mientras el feed carga (Loading) o está vacío (Empty) se queda en
    // Bucaramanga: nunca salta a un "sitio raro". El guard evita reencuadrar
    // (y pelear con los gestos del usuario) en refrescos del listener.
    var reportsFramed by remember { mutableStateOf(false) }
    LaunchedEffect(reports) {
        if (reportsFramed || reports.isEmpty()) return@LaunchedEffect
        if (reports.size == 1) {
            val p = reports.first().location
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(LatLng(p.latitude, p.longitude), 15f)
        } else {
            val builder = LatLngBounds.builder()
            reports.forEach {
                builder.include(LatLng(it.location.latitude, it.location.longitude))
            }
            val bounds = builder.build()
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 140),
                )
            } catch (e: IllegalStateException) {
                // El mapa aún no tiene tamaño: centra en el centro del bbox.
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(bounds.center, 12f)
            }
        }
        reportsFramed = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = hasLocationPermission,
            ),
        ) {
            reports.forEach { report ->
                Marker(
                    state = rememberMarkerState(
                        key = report.id,
                        position = LatLng(
                            report.location.latitude,
                            report.location.longitude,
                        ),
                    ),
                    title = report.animal.name?.takeIf { it.isNotBlank() }
                        ?: report.animal.species.replaceFirstChar { it.uppercase() },
                    snippet = "${report.type.displayName()} · ${relativeTime(report.createdAt)}",
                    icon = BitmapDescriptorFactory.defaultMarker(report.type.markerHue()),
                    onInfoWindowClick = { onOpenReport(report.id) },
                )
            }
        }

        StatusBanner(
            state = state,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp),
        )
    }
}

/** Aviso sobre el mapa para los estados que no son de éxito. */
@Composable
private fun StatusBanner(
    state: ReportListUiState,
    modifier: Modifier = Modifier,
) {
    val message = when (state) {
        ReportListUiState.Loading -> "Cargando reportes…"
        ReportListUiState.Empty -> "Aún no hay reportes para mostrar en el mapa."
        is ReportListUiState.Error -> state.message
        is ReportListUiState.Success -> null
    }
    if (message != null) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                if (state is ReportListUiState.Loading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state is ReportListUiState.Error) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }
    }
}
