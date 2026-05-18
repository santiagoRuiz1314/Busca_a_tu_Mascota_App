package com.santiagoruiz.buscamascota.domain.usecase.report

import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Reportes de animales perdidos creados por el usuario actual. Son el punto
 * de partida del matching visual: para cada uno se buscan avistamientos
 * parecidos. Filtra en cliente sobre el flujo del feed (mismo patrón que
 * alertas; no exige un índice extra).
 */
class ObserveMyLostReportsUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<List<Report>> =
        reportRepository.observeOpenReports().map { reports ->
            val uid = authRepository.currentUser?.uid
            reports.filter { it.type == ReportType.LOST && it.ownerId == uid }
        }
}
