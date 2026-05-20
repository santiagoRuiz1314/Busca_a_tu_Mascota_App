package com.santiagoruiz.buscamascota.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoruiz.buscamascota.domain.model.AuthState
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.UserProfile
import com.santiagoruiz.buscamascota.domain.usecase.auth.ObserveAuthStateUseCase
import com.santiagoruiz.buscamascota.domain.usecase.auth.SignOutUseCase
import com.santiagoruiz.buscamascota.domain.usecase.user.EncodeProfilePhotoUseCase
import com.santiagoruiz.buscamascota.domain.usecase.user.EnsureUserProfileUseCase
import com.santiagoruiz.buscamascota.domain.usecase.user.ObserveMyReportsUseCase
import com.santiagoruiz.buscamascota.domain.usecase.user.ObserveProfileUseCase
import com.santiagoruiz.buscamascota.domain.usecase.user.UpdateProfileUseCase
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeProfile: ObserveProfileUseCase,
    observeMyReports: ObserveMyReportsUseCase,
    observeAuthState: ObserveAuthStateUseCase,
    private val ensureUserProfile: EnsureUserProfileUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val encodeProfilePhoto: EncodeProfilePhotoUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    /** `true` para invitado (anónimo o sin sesión): se muestra una invitación. */
    val isGuest: StateFlow<Boolean> = observeAuthState()
        .map { it !is AuthState.Authenticated || it.user.isAnonymous }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    val uiState: StateFlow<ProfileUiState> = observeProfile()
        .map<UserProfile, ProfileUiState> { ProfileUiState.Success(it) }
        .catch { emit(ProfileUiState.Error(it.message ?: "No se pudo cargar tu perfil.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState.Loading,
        )

    val myReports: StateFlow<ReportListUiState> = observeMyReports()
        .map<List<Report>, ReportListUiState> { reports ->
            if (reports.isEmpty()) ReportListUiState.Empty
            else ReportListUiState.Success(reports)
        }
        .catch {
            emit(ReportListUiState.Error(it.message ?: "No se pudieron cargar tus reportes."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReportListUiState.Loading,
        )

    private val _editState = MutableStateFlow<ProfileEditState?>(null)
    val editState: StateFlow<ProfileEditState?> = _editState.asStateFlow()

    init {
        // Crea el documento users/{uid} de forma perezosa la primera vez
        // que entra una cuenta REAL (no anónima): cubre cuentas previas a la
        // Fase 7 sin forzar reinicio de sesión y evita perfiles basura para
        // invitados. Idempotente; el error real, si lo hay, ya lo surfacea
        // el flujo observado.
        viewModelScope.launch {
            observeAuthState().first {
                it is AuthState.Authenticated && !it.user.isAnonymous
            }
            ensureUserProfile()
        }
    }

    /** Entra en modo edición copiando el perfil actual al formulario. */
    fun startEdit() {
        val current = (uiState.value as? ProfileUiState.Success)?.profile ?: return
        _editState.value = ProfileEditState(
            displayName = current.displayName,
            phone = current.phone.orEmpty(),
            photoBase64 = current.photoBase64,
        )
    }

    fun cancelEdit() {
        _editState.value = null
    }

    fun onNameChange(value: String) = updateForm { it.copy(displayName = value, error = null) }

    fun onPhoneChange(value: String) = updateForm { it.copy(phone = value, error = null) }

    /** Comprime y codifica la foto elegida y la deja lista en el formulario. */
    fun onPhotoPicked(uriString: String) {
        updateForm { it.copy(processingPhoto = true, error = null) }
        viewModelScope.launch {
            val base64 = encodeProfilePhoto(uriString)
            updateForm {
                if (base64 != null) it.copy(photoBase64 = base64, processingPhoto = false)
                else it.copy(
                    processingPhoto = false,
                    error = "No se pudo procesar la imagen.",
                )
            }
        }
    }

    fun save() {
        val form = _editState.value ?: return
        if (form.saving || form.processingPhoto) return
        _editState.value = form.copy(saving = true, error = null)
        viewModelScope.launch {
            updateProfile(form.displayName, form.phone, form.photoBase64).fold(
                onSuccess = { _editState.value = null },
                onFailure = { e ->
                    _editState.value = _editState.value?.copy(
                        saving = false,
                        error = e.message ?: "No se pudo guardar el perfil.",
                    )
                },
            )
        }
    }

    /**
     * Cierra la sesión. La redirección al flujo de auth la maneja de forma
     * reactiva el observador de sesión del grafo raíz.
     */
    fun signOut() = signOutUseCase()

    private inline fun updateForm(transform: (ProfileEditState) -> ProfileEditState) {
        _editState.value = _editState.value?.let(transform)
    }
}
