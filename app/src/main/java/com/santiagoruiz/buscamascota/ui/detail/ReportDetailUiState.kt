package com.santiagoruiz.buscamascota.ui.detail

import com.santiagoruiz.buscamascota.domain.model.Report

/** Estado de la pantalla de detalle de un reporte. */
sealed interface ReportDetailUiState {
    data object Loading : ReportDetailUiState
    data class Success(val report: Report) : ReportDetailUiState
    data class Error(val message: String) : ReportDetailUiState
}
