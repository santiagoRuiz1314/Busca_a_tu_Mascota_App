package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Alertas: reportes activos de los tipos urgentes (animal perdido y
 * maltrato), del más reciente al más antiguo.
 */
class ObserveAlertsUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    operator fun invoke(): Flow<List<Report>> =
        reportRepository.observeOpenReportsByTypes(
            listOf(ReportType.LOST, ReportType.ABUSE),
        )
}
