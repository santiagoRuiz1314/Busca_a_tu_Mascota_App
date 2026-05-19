package com.santiagoruiz.buscamascota.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.ui.common.components.AppTextField
import com.santiagoruiz.buscamascota.ui.common.components.AppTopBar
import com.santiagoruiz.buscamascota.ui.common.components.Base64Image
import com.santiagoruiz.buscamascota.ui.common.components.PrimaryButton
import com.santiagoruiz.buscamascota.ui.common.components.comingSoon

/**
 * Edición de perfil (pantalla aparte en el rediseño). Reutiliza el mismo
 * contrato del [ProfileViewModel] (startEdit / onNameChange / onPhoneChange /
 * onPhotoPicked / save). Al guardar (o cancelar) el editState vuelve a null
 * y se regresa atrás.
 */
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val editState by viewModel.editState.collectAsState()

    var initialized by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        if (state is ProfileUiState.Success && !initialized) {
            viewModel.startEdit()
            initialized = true
        }
    }
    // Tras guardar/cancelar el formulario se cierra (editState → null).
    LaunchedEffect(editState, initialized) {
        if (initialized && editState == null) onBack()
    }

    val email = (state as? ProfileUiState.Success)?.profile?.email.orEmpty()
    val form = editState

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) viewModel.onPhotoPicked(uri.toString()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        AppTopBar(
            title = "Editar Perfil",
            centered = true,
            onBack = {
                viewModel.cancelEdit()
                onBack()
            },
        )

        if (form == null) return@Column

        val busy = form.saving || form.processingPhoto

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                if (!form.photoBase64.isNullOrBlank()) {
                    Base64Image(
                        base64 = form.photoBase64,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(enabled = !busy) {
                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly,
                            ),
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "Cambiar foto",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        if (form.processingPhoto) {
            Text(
                text = "Procesando foto…",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppTextField(
                value = form.displayName,
                onValueChange = viewModel::onNameChange,
                label = "Nombre completo",
                leadingIcon = Icons.Filled.Person,
                isError = form.error != null,
            )
            AppTextField(
                value = email,
                onValueChange = {},
                label = "Correo electrónico",
                leadingIcon = Icons.Filled.Email,
                enabled = false,
            )
            AppTextField(
                value = form.phone,
                onValueChange = viewModel::onPhoneChange,
                label = "Teléfono",
                leadingIcon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone,
            )
            // "Ciudad" no está en el modelo de datos: visible pero inerte.
            Box(modifier = Modifier.comingSoon()) {
                AppTextField(
                    value = "",
                    onValueChange = {},
                    label = "Ciudad",
                    placeholder = "Próximamente",
                    leadingIcon = Icons.Filled.LocationOn,
                    enabled = false,
                )
            }

            if (form.error != null) {
                Text(
                    text = form.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            PrimaryButton(
                text = "Guardar Cambios",
                onClick = viewModel::save,
                loading = form.saving,
                enabled = !busy,
                modifier = Modifier.padding(top = 8.dp),
            )

            TextButton(
                onClick = viewModel::signOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(text = "Cerrar sesión", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
