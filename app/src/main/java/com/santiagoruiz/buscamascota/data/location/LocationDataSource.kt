package com.santiagoruiz.buscamascota.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

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

    private companion object {
        const val FIX_TIMEOUT_MS = 15_000L
    }
}
