package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.domain.repository.LocationRepository
import javax.inject.Inject

/**
 * Traduce las coordenadas de un reporte a un lugar legible (ciudad, país)
 * para mostrarlo en el detalle en vez de coordenadas crudas.
 */
class DescribeLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(point: GeoPoint): String? =
        locationRepository.describeLocation(point)
}
