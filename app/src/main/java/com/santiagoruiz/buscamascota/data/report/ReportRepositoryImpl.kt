package com.santiagoruiz.buscamascota.data.report

import com.santiagoruiz.buscamascota.data.report.mapper.ReportMapper
import com.santiagoruiz.buscamascota.di.IoDispatcher
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportStatus
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación de [ReportRepository] sobre Firestore. El geohash se
 * calcula en el mapper a partir de la ubicación. Las alertas filtran por
 * tipo en cliente sobre el mismo flujo del feed para no exigir un segundo
 * índice compuesto (plan Spark). Los errores se traducen a español vía
 * [toReportErrorMessage] para no ocultar la causa real (reglas / índice).
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
                Result.failure(IllegalStateException(e.toReportErrorMessage(), e))
            }
        }

    override fun observeOpenReports(): Flow<List<Report>> =
        dataSource.observeByStatus(ReportStatus.OPEN.name)
            .map { dtos -> dtos.map(ReportMapper::toDomain) }
            .flowOn(ioDispatcher)
            .catch { throw IllegalStateException(it.toReportErrorMessage(), it) }

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
                Result.failure(IllegalStateException(e.toReportErrorMessage(), e))
            }
        }

    override suspend fun getOpenReportsByType(type: ReportType): Result<List<Report>> =
        withContext(ioDispatcher) {
            try {
                val dtos = dataSource.getByStatusAndType(
                    status = ReportStatus.OPEN.name,
                    type = type.name,
                )
                Result.success(dtos.map(ReportMapper::toDomain))
            } catch (e: Exception) {
                Result.failure(IllegalStateException(e.toReportErrorMessage(), e))
            }
        }
}
