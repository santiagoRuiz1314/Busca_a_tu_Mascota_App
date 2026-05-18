package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import javax.inject.Inject

/** Carga un reporte por id para la pantalla de detalle. */
class GetReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    suspend operator fun invoke(id: String): Result<Report> =
        reportRepository.getReport(id)
}
