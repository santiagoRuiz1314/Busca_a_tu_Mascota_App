package com.santiagoruiz.buscamascota.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.usecase.report.ObserveFeedUseCase
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Estado del mapa del feed: los mismos reportes activos del feed
 * ([ObserveFeedUseCase]) mostrados como marcadores. La ubicación propia la
 * resuelve la capa "mi ubicación" del mapa (punto azul / botón), que ya usa
 * FusedLocation internamente; la cámara NUNCA se centra en el GPS del
 * dispositivo (en emulador es de prueba — ver [[emulator-location-gotcha]]).
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    observeFeed: ObserveFeedUseCase,
) : ViewModel() {

    val uiState: StateFlow<ReportListUiState> = observeFeed()
        .map<List<Report>, ReportListUiState> { reports ->
            if (reports.isEmpty()) ReportListUiState.Empty
            else ReportListUiState.Success(reports)
        }
        .catch {
            emit(
                ReportListUiState.Error(
                    it.message ?: "No se pudieron cargar los reportes.",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReportListUiState.Loading,
        )
}
