package com.santiagoruiz.buscamascota.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Obtiene la ubicación actual del dispositivo. El permiso de ubicación se
 * solicita y verifica en la capa UI antes de invocar esto.
 *
 * Estrategia: primero la última ubicación conocida (instantánea si existe);
 * si no hay, se pide un fix activo con un límite de tiempo para no colgar la
 * UI. Se usa PRIORITY_HIGH_ACCURACY porque el emulador no resuelve la
 * precisión balanceada y devolvía siempre null.
 */
@Singleton
class LocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoPoint? {
        client.lastLocation.await()?.let {
            return GeoPoint(it.latitude, it.longitude)
        }
        val cts = CancellationTokenSource()
        val location = withTimeoutOrNull(FIX_TIMEOUT_MS) {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
        }
        if (location == null) cts.cancel()
        return location?.let { GeoPoint(it.latitude, it.longitude) }
    }

    /**
     * Geocodificación inversa: coordenadas → lugar legible (ej. "Bucaramanga,
     * Santander, Colombia"). Usa el [Geocoder] de Android (gratis, sin API ni
     * facturación; mismo criterio que el buscador del selector). En API 33+
     * la variante asíncrona; por debajo, la bloqueante en IO. Devuelve `null`
     * si no hay geocoder, red o resultados.
     */
    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(context, Locale.getDefault())
        val results: List<Address> = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1,
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
                    geocoder.getFromLocation(latitude, longitude, 1).orEmpty()
                }
            }
        } catch (e: IOException) {
            emptyList()
        } catch (e: IllegalArgumentException) {
            emptyList()
        }

        val address = results.firstOrNull() ?: return null
        // Lugar relevante para el usuario: ciudad, departamento, país.
        // Si falta la localidad se cae a niveles administrativos o, en
        // último caso, a la línea de dirección completa.
        val parts = listOfNotNull(
            address.locality ?: address.subAdminArea,
            address.adminArea,
            address.countryName,
        ).distinct()
        return parts.takeIf { it.isNotEmpty() }?.joinToString(", ")
            ?: address.getAddressLine(0)
    }

    private companion object {
        const val FIX_TIMEOUT_MS = 15_000L
    }
}
