package com.santiagoruiz.buscamascota.ui.common

import com.santiagoruiz.buscamascota.domain.model.Report

/**
 * Estado de una pantalla que lista reportes. Compartido por feed y alertas
 * (ambas son la misma vista de lista sobre fuentes distintas).
 */
sealed interface ReportListUiState {
    data object Loading : ReportListUiState
    data object Empty : ReportListUiState
    data class Success(val reports: List<Report>) : ReportListUiState
    data class Error(val message: String) : ReportListUiState
}
