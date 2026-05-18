package com.santiagoruiz.buscamascota.domain.repository

import com.santiagoruiz.buscamascota.domain.model.Report

/**
 * Contrato de persistencia de reportes. Las consultas de feed/detalle se
 * añaden en la Fase 4.
 */
interface ReportRepository {

    /** Crea el reporte y devuelve el id generado. */
    suspend fun createReport(report: Report): Result<String>
}
