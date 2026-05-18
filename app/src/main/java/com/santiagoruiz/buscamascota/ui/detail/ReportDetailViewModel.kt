package com.santiagoruiz.buscamascota.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
) : ViewModel() {

    private val reportId = savedStateHandle.toRoute<ReportDetailRoute>().reportId

    private val _uiState = MutableStateFlow<ReportDetailUiState>(ReportDetailUiState.Loading)
    val uiState: StateFlow<ReportDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = ReportDetailUiState.Loading
        viewModelScope.launch {
            _uiState.value = getReport(reportId).fold(
                onSuccess = { ReportDetailUiState.Success(it) },
                onFailure = {
                    ReportDetailUiState.Error(
                        it.message ?: "No se pudo cargar el reporte.",
                    )
                },
            )
        }
    }
}
