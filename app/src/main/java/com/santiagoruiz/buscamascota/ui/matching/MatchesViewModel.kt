package com.santiagoruiz.buscamascota.ui.matching

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.santiagoruiz.buscamascota.domain.usecase.matching.FindVisualMatchesUseCase
import com.santiagoruiz.buscamascota.ui.navigation.MatchesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val findVisualMatches: FindVisualMatchesUseCase,
) : ViewModel() {

    private val reportId = savedStateHandle.toRoute<MatchesRoute>().reportId

    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = MatchesUiState.Loading
        viewModelScope.launch {
            _uiState.value = findVisualMatches(reportId).fold(
                onSuccess = { matches ->
                    if (matches.isEmpty()) MatchesUiState.Empty
                    else MatchesUiState.Success(matches)
                },
                onFailure = {
                    MatchesUiState.Error(
                        it.message ?: "No se pudo buscar coincidencias.",
                    )
                },
            )
        }
    }
}
