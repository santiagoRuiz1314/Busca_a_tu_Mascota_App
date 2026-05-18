package com.santiagoruiz.buscamascota.data.report

import com.santiagoruiz.buscamascota.data.report.mapper.ReportMapper
import com.santiagoruiz.buscamascota.di.IoDispatcher
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementación de [ReportRepository] sobre Firestore. El geohash se
 * calcula en el mapper a partir de la ubicación.
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
                Result.failure(
                    IllegalStateException(
                        "No se pudo guardar el reporte. Revisa tu conexión.",
                        e,
                    ),
                )
            }
        }
}
