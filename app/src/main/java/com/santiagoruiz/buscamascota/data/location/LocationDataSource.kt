package com.santiagoruiz.buscamascota.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Obtiene la ubicación actual del dispositivo. El permiso de ubicación se
 * solicita y verifica en la capa UI antes de invocar esto.
 */
@Singleton
class LocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoPoint? {
        val cts = CancellationTokenSource()
        val location = client
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .await()
            ?: client.lastLocation.await()
        return location?.let { GeoPoint(it.latitude, it.longitude) }
    }
}
