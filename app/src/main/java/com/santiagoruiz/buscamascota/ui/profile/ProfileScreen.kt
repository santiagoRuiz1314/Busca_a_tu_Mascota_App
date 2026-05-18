package com.santiagoruiz.buscamascota.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.domain.model.UserProfile
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import com.santiagoruiz.buscamascota.ui.common.components.Base64Image
import com.santiagoruiz.buscamascota.ui.common.components.ReportCard
import com.santiagoruiz.buscamascota.ui.theme.appColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val editState by viewModel.editState.collectAsState()
    val myReports by viewModel.myReports.collectAsState()

    when (val s = state) {
        ProfileUiState.Loading -> CenteredBox {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        is ProfileUiState.Error -> CenteredBox {
            Text(
                text = s.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        is ProfileUiState.Success -> ProfileContent(
            profile = s.profile,
            editState = editState,
            myReports = myReports,
            onStartEdit = viewModel::startEdit,
            onCancelEdit = viewModel::cancelEdit,
            onNameChange = viewModel::onNameChange,
            onPhoneChange = viewModel::onPhoneChange,
            onPhotoPicked = viewModel::onPhotoPicked,
            onSave = viewModel::save,
            onSignOut = viewModel::signOut,
            onOpenReport = onOpenReport,
            modifier = modifier,
        )
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    editState: ProfileEditState?,
    myReports: ReportListUiState,
    onStartEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPhotoPicked: (String) -> Unit,
    onSave: () -> Unit,
    onSignOut: () -> Unit,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) onPhotoPicked(uri.toString()) }

    val editing = editState != null
    val shownPhoto = if (editing) editState?.photoBase64 else profile.photoBase64

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Avatar + identidad
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(photoBase64 = shownPhoto, fallbackName = profile.displayName)
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.displayName.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                )
                MemberSince(profile.createdAt)
            }
        }

        HorizontalDivider(
            color = MaterialTheme.appColors.divider,
            modifier = Modifier.padding(vertical = 20.dp),
        )

        if (editState != null) {
            EditForm(
                state = editState,
                onNameChange = onNameChange,
                onPhoneChange = onPhoneChange,
                onPickPhoto = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
                onSave = onSave,
                onCancel = onCancelEdit,
            )
        } else {
            InfoRow("Teléfono", profile.phone?.takeIf { it.isNotBlank() } ?: "No registrado")
            OutlinedButton(
                onClick = onStartEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text("Editar perfil")
            }
        }

        HorizontalDivider(
            color = MaterialTheme.appColors.divider,
            modifier = Modifier.padding(vertical = 20.dp),
        )

        Text(
            text = "Mis reportes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        MyReportsList(
            state = myReports,
            onOpenReport = onOpenReport,
            modifier = Modifier.padding(top = 12.dp),
        )

        TextButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        ) {
            Text(
                text = "Cerrar sesión",
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun Avatar(photoBase64: String?, fallbackName: String) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (photoBase64?.isNotBlank() == true) {
            Base64Image(
                base64 = photoBase64,
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = fallbackName.trim().firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun MemberSince(createdAt: Long) {
    if (createdAt <= 0L) return
    val text = remember(createdAt) {
        "Miembro desde " + SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es"))
            .format(Date(createdAt))
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.appColors.textSecondary,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun EditForm(
    state: ProfileEditState,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPickPhoto: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    val busy = state.saving || state.processingPhoto

    OutlinedTextField(
        value = state.displayName,
        onValueChange = onNameChange,
        label = { Text("Nombre") },
        singleLine = true,
        isError = state.error != null,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = state.phone,
        onValueChange = onPhoneChange,
        label = { Text("Teléfono (opcional)") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    )

    OutlinedButton(
        onClick = onPickPhoto,
        enabled = !busy,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        Text(if (state.photoBase64.isNullOrBlank()) "Agregar foto" else "Cambiar foto")
    }
    if (state.processingPhoto) {
        Text(
            text = "Procesando foto…",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }

    if (state.error != null) {
        Text(
            text = state.error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp),
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onSave,
            enabled = !busy,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.appColors.primaryAction,
                contentColor = MaterialTheme.appColors.onPrimaryAction,
            ),
            modifier = Modifier.weight(1f),
        ) {
            if (state.saving) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.appColors.onPrimaryAction,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Text("Guardar")
            }
        }
        TextButton(
            onClick = onCancel,
            enabled = !state.saving,
            modifier = Modifier.weight(1f),
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
private fun MyReportsList(
    state: ReportListUiState,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // El padre ya hace verticalScroll, así que esta lista se renderiza como
    // columna simple (no LazyColumn anidado, que rompería la medición).
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (state) {
            ReportListUiState.Loading -> CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
            )

            ReportListUiState.Empty -> Text(
                text = "Aún no has creado reportes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            is ReportListUiState.Error -> Text(
                text = state.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            is ReportListUiState.Success -> state.reports.forEach { report ->
                ReportCard(report = report, onClick = { onOpenReport(report.id) })
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.textSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
