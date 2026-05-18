package com.santiagoruiz.buscamascota.data.report

import com.google.firebase.firestore.FirebaseFirestoreException
import com.santiagoruiz.buscamascota.data.report.mapper.ReportMapper
import com.santiagoruiz.buscamascota.di.IoDispatcher
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportStatus
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación de [ReportRepository] sobre Firestore. El geohash se
 * calcula en el mapper a partir de la ubicación. Las alertas filtran por
 * tipo en cliente sobre el mismo flujo del feed para no exigir un segundo
 * índice compuesto (plan Spark).
 */
class ReportRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreReportDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ReportRepository {

    override suspend fun createReport(report: Report): Result<String> =
        withContext(ioDispatcher) {
            try {
                val id = dataSource.create(ReportMapper.toDto(report))
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(IllegalStateException(e.toSpanishMessage(), e))
            }
        }

    override fun observeOpenReports(): Flow<List<Report>> =
        dataSource.observeByStatus(ReportStatus.OPEN.name)
            .map { dtos -> dtos.map(ReportMapper::toDomain) }
            .flowOn(ioDispatcher)

    override fun observeOpenReportsByTypes(types: List<ReportType>): Flow<List<Report>> {
        val wanted = types.toSet()
        return observeOpenReports().map { reports ->
            reports.filter { it.type in wanted }
        }
    }

    override suspend fun getReport(id: String): Result<Report> =
        withContext(ioDispatcher) {
            try {
                val dto = dataSource.getById(id)
                    ?: return@withContext Result.failure(
                        IllegalStateException("El reporte ya no está disponible."),
                    )
                Result.success(ReportMapper.toDomain(dto))
            } catch (e: Exception) {
                Result.failure(IllegalStateException(e.toSpanishMessage(), e))
            }
        }

    /**
     * Traduce las excepciones de Firestore a mensajes en español listos para
     * mostrar. PERMISSION_DENIED es lo más común al primer uso si la base se
     * creó en modo producción (reglas que deniegan todo): antes se reportaba
     * como "revisa tu conexión", lo cual confundía.
     */
    private fun Exception.toSpanishMessage(): String =
        if (this is FirebaseFirestoreException) when (code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "No tienes permiso para esta acción. Hay que publicar las " +
                    "reglas de seguridad de Firestore (ver firestore.rules)."
            FirebaseFirestoreException.Code.UNAVAILABLE ->
                "Sin conexión con el servidor. Revisa tu internet e intenta de nuevo."
            FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                "Tu sesión expiró. Vuelve a iniciar sesión."
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED ->
                "Se alcanzó un límite del plan gratuito. Intenta más tarde."
            FirebaseFirestoreException.Code.INVALID_ARGUMENT ->
                "Los datos del reporte no son válidos (¿la foto es muy pesada?)."
            FirebaseFirestoreException.Code.NOT_FOUND ->
                "El reporte ya no está disponible."
            else -> "No se pudo completar la operación. Intenta de nuevo."
        } else {
            "No se pudo completar la operación. Revisa tu conexión."
        }
}
