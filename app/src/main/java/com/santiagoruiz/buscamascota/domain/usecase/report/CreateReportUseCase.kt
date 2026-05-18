package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.model.AnimalInfo
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportStatus
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import javax.inject.Inject

/** Datos de entrada para crear un reporte (desde la UI). */
data class CreateReportInput(
    val type: ReportType,
    val species: String,
    val breed: String?,
    val color: String?,
    val animalName: String?,
    val description: String,
    val photoBase64: String?,
    val location: GeoPoint?,
    /** Embedding visual de la foto (Fase 6); se asocia solo a LOST/SIGHTING. */
    val embedding: FloatArray? = null,
)

/**
 * Valida los datos, asocia el reporte al usuario autenticado y lo persiste.
 * Mensajes de error en español (se muestran en la UI).
 */
class CreateReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(input: CreateReportInput): Result<String> {
        val ownerId = authRepository.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Debes iniciar sesión para reportar."))

        if (input.species.isBlank()) {
            return Result.failure(IllegalArgumentException("Indica la especie del animal."))
        }
        if (input.description.isBlank()) {
            return Result.failure(IllegalArgumentException("Agrega una descripción."))
        }
        if (input.photoBase64.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Agrega una foto del animal."))
        }
        val location = input.location
            ?: return Result.failure(IllegalArgumentException("No se pudo obtener la ubicación."))

        val report = Report(
            type = input.type,
            status = ReportStatus.OPEN,
            ownerId = ownerId,
            animal = AnimalInfo(
                species = input.species.trim(),
                breed = input.breed?.trim()?.ifBlank { null },
                color = input.color?.trim()?.ifBlank { null },
                name = input.animalName?.trim()?.ifBlank { null },
            ),
            description = input.description.trim(),
            location = location,
            photoBase64 = input.photoBase64,
            // El matching visual solo aplica a perdidos y avistamientos; en
            // denuncias de maltrato no se persiste el embedding.
            embedding = input.embedding?.takeIf {
                input.type == ReportType.LOST || input.type == ReportType.SIGHTING
            },
            createdAt = System.currentTimeMillis(),
        )
        return reportRepository.createReport(report)
    }
}
