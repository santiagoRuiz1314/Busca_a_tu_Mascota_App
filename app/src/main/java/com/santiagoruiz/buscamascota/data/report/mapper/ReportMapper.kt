package com.santiagoruiz.buscamascota.data.report.mapper

import com.santiagoruiz.buscamascota.data.common.GeoHashUtil
import com.santiagoruiz.buscamascota.data.report.dto.ReportDto
import com.santiagoruiz.buscamascota.domain.model.AnimalInfo
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportStatus
import com.santiagoruiz.buscamascota.domain.model.ReportType

/** Conversión DTO (Firestore) ↔ modelo de dominio. */
object ReportMapper {

    /**
     * `createdAt` se deja nulo: lo rellena el servidor (@ServerTimestamp).
     * El geohash se deriva aquí de la ubicación.
     */
    fun toDto(report: Report): ReportDto = ReportDto(
        type = report.type.name,
        status = report.status.name,
        ownerId = report.ownerId,
        species = report.animal.species,
        breed = report.animal.breed,
        color = report.animal.color,
        animalName = report.animal.name,
        description = report.description,
        lat = report.location.latitude,
        lng = report.location.longitude,
        geohash = GeoHashUtil.encode(report.location.latitude, report.location.longitude),
        photoBase64 = report.photoBase64,
        embedding = report.embedding?.map { it.toDouble() },
        createdAt = null,
    )

    fun toDomain(dto: ReportDto): Report = Report(
        id = dto.id,
        type = ReportType.entries.firstOrNull { it.name == dto.type } ?: ReportType.SIGHTING,
        status = ReportStatus.entries.firstOrNull { it.name == dto.status } ?: ReportStatus.OPEN,
        ownerId = dto.ownerId,
        animal = AnimalInfo(
            species = dto.species,
            breed = dto.breed,
            color = dto.color,
            name = dto.animalName,
        ),
        description = dto.description,
        location = GeoPoint(dto.lat, dto.lng),
        photoBase64 = dto.photoBase64,
        embedding = dto.embedding?.map { it.toFloat() }?.toFloatArray(),
        createdAt = dto.createdAt?.time ?: 0L,
    )
}
