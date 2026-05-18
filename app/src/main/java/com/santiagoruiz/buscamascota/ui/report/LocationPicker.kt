package com.santiagoruiz.buscamascota.ui.report

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.ui.theme.appColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

// Centro por defecto: Bucaramanga (UNAB), si no hay ubicación automática.
private val DEFAULT_CENTER = LatLng(7.1193, -73.1227)
private const val MAX_RESULTS = 5

private data class AddressMatch(val label: String, val latitude: Double, val longitude: Double)

/**
 * Selección de ubicación en mapa con buscador de direcciones. El buscador
 * usa el [Geocoder] integrado de Android (gratis, sin API ni facturación,
 * respeta el plan Spark): al escribir aparecen coincidencias y al tocar una
 * el mapa va ahí. El usuario también puede mover el mapa libremente. Se
 * confirma el centro de cámara. Funciona sin permiso de ubicación.
 */
@Composable
fun LocationPicker(
    initial: GeoPoint?,
    onConfirm: (latitude: Double, longitude: Double) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val start = initial?.let { LatLng(it.latitude, it.longitude) } ?: DEFAULT_CENTER
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(start, 16f)
    }

    var query by rememberSaveable { mutableStateOf("") }
    var matches by remember { mutableStateOf<List<AddressMatch>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    // Tras elegir una coincidencia se oculta la lista hasta que se escriba de nuevo.
    var suggestionsDismissed by remember { mutableStateOf(false) }

    // Si la ubicación automática llega después, recentra el mapa una vez.
    LaunchedEffect(initial) {
        if (initial != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(initial.latitude, initial.longitude),
                16f,
            )
        }
    }

    // Búsqueda con debounce: espera a que el usuario deje de escribir.
    LaunchedEffect(query) {
        if (query.trim().length < 3) {
            matches = emptyList()
            searching = false
            return@LaunchedEffect
        }
        searching = true
        kotlinx.coroutines.delay(400)
        matches = geocode(context, query.trim())
        searching = false
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

        // Buscador de direcciones (arriba, sobre el mapa).
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 3.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        suggestionsDismissed = false
                    },
                    label = { Text("Buscar dirección") },
                    placeholder = { Text("Calle, barrio, ciudad…") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            val showSuggestions = !suggestionsDismissed &&
                query.trim().length >= 3 &&
                (searching || matches.isNotEmpty())
            if (showSuggestions) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 3.dp,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 240.dp)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        when {
                            searching -> SuggestionText("Buscando…")
                            matches.isEmpty() -> SuggestionText("Sin resultados")
                            else -> matches.forEachIndexed { index, match ->
                                if (index > 0) HorizontalDivider()
                                Text(
                                    text = match.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            cameraPositionState.position =
                                                CameraPosition.fromLatLngZoom(
                                                    LatLng(match.latitude, match.longitude),
                                                    17f,
                                                )
                                            suggestionsDismissed = true
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "También puedes mover el mapa para ajustar el punto.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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

@Composable
private fun SuggestionText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

/**
 * Geocodifica una dirección con el [Geocoder] de Android. En API 33+ usa la
 * variante asíncrona; por debajo, la bloqueante en un hilo de IO. Devuelve
 * lista vacía ante errores (sin red, dirección inválida, geocoder ausente).
 */
private suspend fun geocode(context: Context, address: String): List<AddressMatch> {
    if (!Geocoder.isPresent()) return emptyList()
    val geocoder = Geocoder(context, Locale.getDefault())
    val results: List<Address> = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { cont ->
                geocoder.getFromLocationName(
                    address,
                    MAX_RESULTS,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            if (cont.isActive) cont.resume(addresses)
                        }

                        override fun onError(errorMessage: String?) {
                            if (cont.isActive) cont.resume(emptyList())
                        }
                    },
                )
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(address, MAX_RESULTS).orEmpty()
            }
        }
    } catch (e: IOException) {
        emptyList()
    } catch (e: IllegalArgumentException) {
        emptyList()
    }

    return results
        .filter { it.hasLatitude() && it.hasLongitude() }
        .map { addr ->
            val label = addr.getAddressLine(0)
                ?: listOfNotNull(
                    addr.featureName,
                    addr.locality,
                    addr.adminArea,
                    addr.countryName,
                ).distinct().joinToString(", ")
            AddressMatch(label, addr.latitude, addr.longitude)
        }
}
