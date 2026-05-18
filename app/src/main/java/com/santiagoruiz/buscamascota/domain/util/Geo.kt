package com.santiagoruiz.buscamascota.domain.util

import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Cálculos geográficos para acotar el matching a una zona. */
object Geo {

    private const val EARTH_RADIUS_KM = 6_371.0

    /** Distancia Haversine en kilómetros entre dos puntos. */
    fun distanceKm(a: GeoPoint, b: GeoPoint): Double {
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val h = sin(dLat / 2) * sin(dLat / 2) +
            cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2)
        return 2 * EARTH_RADIUS_KM * atan2(sqrt(h), sqrt(1 - h))
    }
}
