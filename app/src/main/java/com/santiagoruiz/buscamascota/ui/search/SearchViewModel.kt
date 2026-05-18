package com.santiagoruiz.buscamascota.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.usecase.report.ObserveMyLostReportsUseCase
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Pestaña «Buscar»: lista los reportes de animales perdidos del usuario.
 * Cada uno es el punto de partida del matching visual (Fase 6).
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    observeMyLostReports: ObserveMyLostReportsUseCase,
) : ViewModel() {

    val uiState: StateFlow<ReportListUiState> = observeMyLostReports()
        .map<List<Report>, ReportListUiState> { reports ->
            if (reports.isEmpty()) ReportListUiState.Empty
            else ReportListUiState.Success(reports)
        }
        .catch {
            emit(
                ReportListUiState.Error(
                    it.message ?: "No se pudieron cargar tus reportes.",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReportListUiState.Loading,
        )
}
