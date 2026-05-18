package com.santiagoruiz.buscamascota.ui.report

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.ui.theme.appColors

private fun ReportType.label(): String = when (this) {
    ReportType.LOST -> "Perdido"
    ReportType.SIGHTING -> "Avistamiento"
    ReportType.ABUSE -> "Abuso"
}

// Especie como opción fija (la autodetección con ML Kit llega en Fase 6).
private val speciesOptions = listOf("Perro", "Gato", "Otro")

@Composable
fun CreateReportScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateReportViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val location by viewModel.location.collectAsState()
    val hasPhoto by viewModel.hasPhoto.collectAsState()
    val processingPhoto by viewModel.processingPhoto.collectAsState()

    var type by rememberSaveable { mutableStateOf(ReportType.LOST) }
    var species by rememberSaveable { mutableStateOf("") }
    var breed by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var animalName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var showPicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is CreateReportUiState.Success) onClose()
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) viewModel.onPhotoPicked(uri.toString()) }

    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> if (granted) viewModel.requestLocation() }

    fun onRequestLocation() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) viewModel.requestLocation()
        else locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    if (showPicker) {
        LocationPicker(
            initial = location,
            onConfirm = { lat, lng ->
                viewModel.setLocation(lat, lng)
                showPicker = false
            },
            onCancel = { showPicker = false },
            modifier = modifier,
        )
        return
    }

    val submitting = uiState is CreateReportUiState.Submitting
    val errorMessage = (uiState as? CreateReportUiState.Error)?.message

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = "Nuevo reporte",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Tipo de reporte
        Text(
            text = "Tipo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ReportType.entries.forEach { option ->
                FilterChip(
                    selected = type == option,
                    onClick = { type = option },
                    label = { Text(option.label()) },
                )
            }
        }

        // Foto
        OutlinedButton(
            onClick = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            enabled = !submitting && !processingPhoto,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            Text(if (hasPhoto) "Cambiar foto" else "Agregar foto")
        }
        when {
            processingPhoto -> StatusLine("Procesando foto…")
            hasPhoto -> StatusLine("Foto lista ✓")
        }

        // Ubicación: se elige en el mapa (con autodetección como ayuda
        // para precentrar). No se muestran coordenadas crudas.
        OutlinedButton(
            onClick = {
                onRequestLocation()
                showPicker = true
            },
            enabled = !submitting,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text(if (location != null) "Cambiar ubicación" else "Seleccionar ubicación")
        }
        if (location != null) {
            StatusLine("Ubicación lista ✓")
        }

        // Especie
        Text(
            text = "Especie",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            speciesOptions.forEach { option ->
                FilterChip(
                    selected = species == option,
                    onClick = { species = option; viewModel.clearError() },
                    label = { Text(option) },
                )
            }
        }

        // Datos del animal
        OutlinedTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Raza (opcional)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color (opcional)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
        if (type == ReportType.LOST) {
            OutlinedTextField(
                value = animalName,
                onValueChange = { animalName = it },
                label = { Text("Nombre de la mascota") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }
        OutlinedTextField(
            value = description,
            onValueChange = { description = it; viewModel.clearError() },
            label = { Text("Descripción") },
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }

        Button(
            onClick = {
                viewModel.submit(type, species, breed, color, animalName, description)
            },
            enabled = !submitting && !processingPhoto,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.appColors.primaryAction,
                contentColor = MaterialTheme.appColors.onPrimaryAction,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        ) {
            if (submitting) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.appColors.onPrimaryAction,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Text("ENVIAR REPORTE")
            }
        }

        TextButton(
            onClick = onClose,
            enabled = !submitting,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        ) {
            Text("Cancelar")
        }
    }
}

@Composable
private fun StatusLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
    )
}
