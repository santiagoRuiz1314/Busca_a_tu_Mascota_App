package com.santiagoruiz.buscamascota.ui.report

/** Estado del envío del formulario de nuevo reporte. */
sealed interface CreateReportUiState {
    data object Idle : CreateReportUiState
    data object Submitting : CreateReportUiState
    data class Error(val message: String) : CreateReportUiState
    data object Success : CreateReportUiState
}
