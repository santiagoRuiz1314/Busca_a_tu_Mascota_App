package com.santiagoruiz.buscamascota.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.domain.usecase.report.CreateReportInput
import com.santiagoruiz.buscamascota.domain.usecase.report.CreateReportUseCase
import com.santiagoruiz.buscamascota.domain.usecase.report.EncodeReportPhotoUseCase
import com.santiagoruiz.buscamascota.domain.usecase.report.GetCurrentLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val createReport: CreateReportUseCase,
    private val getCurrentLocation: GetCurrentLocationUseCase,
    private val encodePhoto: EncodeReportPhotoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateReportUiState>(CreateReportUiState.Idle)
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    private val _location = MutableStateFlow<GeoPoint?>(null)
    val location: StateFlow<GeoPoint?> = _location.asStateFlow()

    private val _locating = MutableStateFlow(false)
    val locating: StateFlow<Boolean> = _locating.asStateFlow()

    private val _photoBase64 = MutableStateFlow<String?>(null)

    private val _processingPhoto = MutableStateFlow(false)
    val processingPhoto: StateFlow<Boolean> = _processingPhoto.asStateFlow()

    private val _hasPhoto = MutableStateFlow(false)
    val hasPhoto: StateFlow<Boolean> = _hasPhoto.asStateFlow()

    fun requestLocation() {
        if (_locating.value) return
        viewModelScope.launch {
            _locating.value = true
            _location.value = getCurrentLocation()
            _locating.value = false
        }
    }

    fun onPhotoPicked(uriString: String) {
        viewModelScope.launch {
            _processingPhoto.value = true
            _photoBase64.value = encodePhoto(uriString)
            _hasPhoto.value = _photoBase64.value != null
            _processingPhoto.value = false
        }
    }

    fun submit(
        type: ReportType,
        species: String,
        breed: String,
        color: String,
        animalName: String,
        description: String,
    ) {
        if (_uiState.value is CreateReportUiState.Submitting) return
        _uiState.value = CreateReportUiState.Submitting
        viewModelScope.launch {
            val result = createReport(
                CreateReportInput(
                    type = type,
                    species = species,
                    breed = breed,
                    color = color,
                    animalName = if (type == ReportType.LOST) animalName else null,
                    description = description,
                    photoBase64 = _photoBase64.value,
                    location = _location.value,
                ),
            )
            _uiState.value = result.fold(
                onSuccess = { CreateReportUiState.Success },
                onFailure = {
                    CreateReportUiState.Error(it.message ?: "No se pudo crear el reporte.")
                },
            )
        }
    }

    fun clearError() {
        if (_uiState.value is CreateReportUiState.Error) {
            _uiState.value = CreateReportUiState.Idle
        }
    }
}
