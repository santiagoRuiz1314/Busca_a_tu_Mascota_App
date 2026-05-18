package com.santiagoruiz.buscamascota.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.santiagoruiz.buscamascota.domain.usecase.report.DescribeLocationUseCase
import com.santiagoruiz.buscamascota.domain.usecase.report.GetReportUseCase
import com.santiagoruiz.buscamascota.ui.navigation.ReportDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getReport: GetReportUseCase,
    private val describeLocation: DescribeLocationUseCase,
) : ViewModel() {

    private val reportId = savedStateHandle.toRoute<ReportDetailRoute>().reportId

    private val _uiState = MutableStateFlow<ReportDetailUiState>(ReportDetailUiState.Loading)
    val uiState: StateFlow<ReportDetailUiState> = _uiState.asStateFlow()

    /** Lugar legible del reporte; `null` mientras se resuelve o si falla. */
    private val _address = MutableStateFlow<String?>(null)
    val address: StateFlow<String?> = _address.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = ReportDetailUiState.Loading
        _address.value = null
        viewModelScope.launch {
            val result = getReport(reportId)
            _uiState.value = result.fold(
                onSuccess = { ReportDetailUiState.Success(it) },
                onFailure = {
                    ReportDetailUiState.Error(
                        it.message ?: "No se pudo cargar el reporte.",
                    )
                },
            )
            result.getOrNull()?.let { report ->
                _address.value = describeLocation(report.location)
            }
        }
    }
}
