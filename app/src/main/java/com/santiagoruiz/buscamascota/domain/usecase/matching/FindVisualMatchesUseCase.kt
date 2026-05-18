package com.santiagoruiz.buscamascota.domain.usecase.matching

import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.domain.model.VisualMatch
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import com.santiagoruiz.buscamascota.domain.util.Geo
import com.santiagoruiz.buscamascota.domain.util.Vectors
import javax.inject.Inject

/**
 * Funcionalidad estrella: dado un reporte (típicamente un animal perdido),
 * busca coincidencias visuales entre los reportes del tipo opuesto
 * (avistamientos) activos en la misma zona geográfica.
 *
 * Pipeline 100 % on-device (plan Spark): se acota por zona con distancia
 * Haversine en cliente —el geohash queda para escalar la consulta del lado
 * del servidor en el futuro— y se ordena por similitud coseno de los
 * embeddings (ya L2-normalizados al extraerse).
 */
class FindVisualMatchesUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    suspend operator fun invoke(reportId: String): Result<List<VisualMatch>> {
        val target = reportRepository.getReport(reportId).getOrElse {
            return Result.failure(it)
        }

        val embedding = target.embedding
            ?: return Result.failure(
                IllegalStateException(
                    "Este reporte no tiene análisis visual. Vuelve a crearlo " +
                        "con una foto para poder buscar coincidencias.",
                ),
            )

        val oppositeType = when (target.type) {
            ReportType.LOST -> ReportType.SIGHTING
            ReportType.SIGHTING -> ReportType.LOST
            ReportType.ABUSE -> return Result.failure(
                IllegalStateException(
                    "El matching visual solo aplica a perdidos y avistamientos.",
                ),
            )
        }

        val candidates = reportRepository.getOpenReportsByType(oppositeType).getOrElse {
            return Result.failure(it)
        }

        val matches = candidates.asSequence()
            .filter { it.id != target.id && it.embedding != null }
            .filter { Geo.distanceKm(target.location, it.location) <= ZONE_RADIUS_KM }
            .map { candidate ->
                VisualMatch(
                    report = candidate,
                    similarity = Vectors.cosineSimilarity(embedding, candidate.embedding!!),
                )
            }
            .filter { it.similarity >= MIN_SIMILARITY }
            .sortedByDescending { it.similarity }
            .take(MAX_RESULTS)
            .toList()

        return Result.success(matches)
    }

    private companion object {
        /** Radio de la zona de búsqueda (escala ciudad). */
        const val ZONE_RADIUS_KM = 15.0

        /** Piso de similitud; descarta lo claramente no relacionado. */
        const val MIN_SIMILARITY = 0.35f

        const val MAX_RESULTS = 50
    }
}
