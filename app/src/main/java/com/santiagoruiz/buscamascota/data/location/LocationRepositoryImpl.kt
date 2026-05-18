package com.santiagoruiz.buscamascota.data.location

import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val dataSource: LocationDataSource,
) : LocationRepository {

    override suspend fun getCurrentLocation(): GeoPoint? =
        runCatching { dataSource.getCurrentLocation() }.getOrNull()

    override suspend fun describeLocation(point: GeoPoint): String? =
        runCatching {
            dataSource.reverseGeocode(point.latitude, point.longitude)
        }.getOrNull()
}
