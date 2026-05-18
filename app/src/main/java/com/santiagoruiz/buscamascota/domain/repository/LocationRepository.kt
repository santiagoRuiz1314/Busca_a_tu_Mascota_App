package com.santiagoruiz.buscamascota.domain.repository

import com.santiagoruiz.buscamascota.domain.model.GeoPoint

/** Provee la ubicación actual. El permiso se gestiona en la capa UI. */
interface LocationRepository {
    suspend fun getCurrentLocation(): GeoPoint?

    /**
     * Describe un punto como lugar legible (ciudad, departamento, país) en
     * vez de coordenadas crudas. `null` si no se pudo resolver.
     */
    suspend fun describeLocation(point: GeoPoint): String?
}
