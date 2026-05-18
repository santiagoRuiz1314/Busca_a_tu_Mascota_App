package com.santiagoruiz.buscamascota.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.usecase.report.ObserveAlertsUseCase
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    observeAlerts: ObserveAlertsUseCase,
) : ViewModel() {

    val uiState: StateFlow<ReportListUiState> = observeAlerts()
        .map<List<Report>, ReportListUiState> { reports ->
            if (reports.isEmpty()) ReportListUiState.Empty
            else ReportListUiState.Success(reports)
        }
        .catch {
            emit(
                ReportListUiState.Error(
                    it.message ?: "No se pudieron cargar las alertas.",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReportListUiState.Loading,
        )
}
