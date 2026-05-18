package com.santiagoruiz.buscamascota.domain.usecase.user

import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.repository.AuthRepository
import com.santiagoruiz.buscamascota.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Reportes activos creados por el usuario actual (sección «Mis reportes»
 * del perfil). Filtra en cliente sobre el flujo del feed —mismo patrón que
 * alertas y [com.santiagoruiz.buscamascota.domain.usecase.report.ObserveMyLostReportsUseCase]—,
 * sin exigir un índice extra (plan Spark).
 */
class ObserveMyReportsUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<List<Report>> =
        reportRepository.observeOpenReports().map { reports ->
            val uid = authRepository.currentUser?.uid
            reports.filter { it.ownerId == uid }
        }
}
