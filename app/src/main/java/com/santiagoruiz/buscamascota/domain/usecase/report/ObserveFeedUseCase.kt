package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Expone el feed de reportes activos como flujo reactivo. */
class ObserveFeedUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    operator fun invoke(): Flow<List<Report>> = reportRepository.observeOpenReports()
}
