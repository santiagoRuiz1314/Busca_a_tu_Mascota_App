package com.santiagoruiz.buscamascota.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.GeoPoint
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.domain.usecase.report.CreateReportInput
import com.santiagoruiz.buscamascota.domain.usecase.report.CreateReportUseCase
import com.santiagoruiz.buscamascota.domain.usecase.report.DetectSpeciesUseCase
import com.santiagoruiz.buscamascota.domain.usecase.report.EncodeReportPhotoUseCase
import com.santiagoruiz.buscamascota.domain.usecase.report.ExtractPhotoEmbeddingUseCase
import com.santiagoruiz.buscamascota.domain.usecase.report.GetCurrentLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
    private val detectSpecies: DetectSpeciesUseCase,
    private val extractEmbedding: ExtractPhotoEmbeddingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateReportUiState>(CreateReportUiState.Idle)
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    private val _location = MutableStateFlow<GeoPoint?>(null)
    val location: StateFlow<GeoPoint?> = _location.asStateFlow()

    private val _locating = MutableStateFlow(false)
    val locating: StateFlow<Boolean> = _locating.asStateFlow()

    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError.asStateFlow()

    private val _photoBase64 = MutableStateFlow<String?>(null)

    private val _embedding = MutableStateFlow<FloatArray?>(null)

    private val _processingPhoto = MutableStateFlow(false)
    val processingPhoto: StateFlow<Boolean> = _processingPhoto.asStateFlow()

    private val _hasPhoto = MutableStateFlow(false)
    val hasPhoto: StateFlow<Boolean> = _hasPhoto.asStateFlow()

    /** Especie sugerida por ML Kit a partir de la foto (la UI la prefija). */
    private val _suggestedSpecies = MutableStateFlow<String?>(null)
    val suggestedSpecies: StateFlow<String?> = _suggestedSpecies.asStateFlow()

    fun requestLocation() {
        if (_locating.value) return
        viewModelScope.launch {
            _locating.value = true
            _locationError.value = null
            val result = getCurrentLocation()
            _location.value = result
            _locationError.value = if (result == null) {
                "No se pudo obtener tu ubicación. Activa la ubicación del " +
                    "dispositivo (en el emulador: Extended Controls › Location) " +
                    "e inténtalo de nuevo."
            } else {
                null
            }
            _locating.value = false
        }
    }

    /** Ubicación elegida por el usuario en el mapa (selección manual). */
    fun setLocation(latitude: Double, longitude: Double) {
        _location.value = GeoPoint(latitude, longitude)
        _locationError.value = null
    }

    fun onPhotoPicked(uriString: String) {
        viewModelScope.launch {
            _processingPhoto.value = true
            _suggestedSpecies.value = null
            _embedding.value = null

            // Compresión, especie (ML Kit) y embedding (TFLite) en paralelo:
            // las tres leen la misma foto y no dependen entre sí.
            val base64 = async { encodePhoto(uriString) }
            val species = async { detectSpecies(uriString) }
            val embedding = async { extractEmbedding(uriString) }

            _photoBase64.value = base64.await()
            _hasPhoto.value = _photoBase64.value != null
            _suggestedSpecies.value = species.await()
            _embedding.value = embedding.await()
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
                    embedding = _embedding.value,
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
