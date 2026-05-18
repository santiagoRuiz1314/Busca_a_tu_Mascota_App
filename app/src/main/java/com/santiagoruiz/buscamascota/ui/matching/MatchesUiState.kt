package com.santiagoruiz.buscamascota.ui.matching

import com.santiagoruiz.buscamascota.domain.model.VisualMatch

/** Estado de la pantalla de coincidencias visuales de un reporte. */
sealed interface MatchesUiState {
    data object Loading : MatchesUiState
    data object Empty : MatchesUiState
    data class Success(val matches: List<VisualMatch>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}
