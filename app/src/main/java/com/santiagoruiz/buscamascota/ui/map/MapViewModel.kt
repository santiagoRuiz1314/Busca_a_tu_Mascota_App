package com.santiagoruiz.buscamascota.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.usecase.report.GetCurrentLocationUseCase
import com.santiagoruiz.buscamascota.domain.usecase.report.ObserveFeedUseCase
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado del mapa del feed: los mismos reportes activos del feed
 * ([ObserveFeedUseCase]), pero mostrados como marcadores. La ubicación del
 * usuario se obtiene bajo demanda (tras conceder el permiso en la UI) solo
 * para precentrar la cámara; ver [[emulator-location-gotcha]].
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    observeFeed: ObserveFeedUseCase,
    private val getCurrentLocation: GetCurrentLocationUseCase,
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

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation.asStateFlow()

    /** Pide la ubicación actual; el permiso ya se verificó en la UI. */
    fun requestUserLocation() {
        viewModelScope.launch {
            _userLocation.value = getCurrentLocation()
        }
    }
}
