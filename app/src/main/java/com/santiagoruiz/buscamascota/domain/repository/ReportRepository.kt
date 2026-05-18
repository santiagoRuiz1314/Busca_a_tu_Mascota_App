package com.santiagoruiz.buscamascota.domain.repository

import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportType
import kotlinx.coroutines.flow.Flow

/** Contrato de persistencia y lectura de reportes. */
interface ReportRepository {

    /** Crea el reporte y devuelve el id generado. */
    suspend fun createReport(report: Report): Result<String>

    /** Reportes activos para el feed, del más reciente al más antiguo. */
    fun observeOpenReports(): Flow<List<Report>>

    /** Reportes activos de los [types] indicados (alertas urgentes). */
    fun observeOpenReportsByTypes(types: List<ReportType>): Flow<List<Report>>

    /** Carga un reporte por id (pantalla de detalle). */
    suspend fun getReport(id: String): Result<Report>
}
