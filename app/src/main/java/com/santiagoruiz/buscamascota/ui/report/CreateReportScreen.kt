package com.santiagoruiz.buscamascota.ui.report

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.ui.common.components.AppTextField
import com.santiagoruiz.buscamascota.ui.common.components.AppTopBar
import com.santiagoruiz.buscamascota.ui.common.components.PrimaryButton
import com.santiagoruiz.buscamascota.ui.common.components.comingSoon
import com.santiagoruiz.buscamascota.ui.theme.appColors

// Especie como opción fija (la autodetección con ML Kit es solo sugerencia).
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
    val suggestedSpecies by viewModel.suggestedSpecies.collectAsState()

    var step by rememberSaveable { mutableStateOf(1) }
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

    // ML Kit sugiere la especie; se prefija solo si el usuario no eligió aún.
    LaunchedEffect(suggestedSpecies) {
        val suggestion = suggestedSpecies
        if (suggestion != null && species.isBlank() && suggestion in speciesOptions) {
            species = suggestion
        }
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
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppTopBar(
            title = "Nuevo Reporte ($step/2)",
            onBack = { if (step == 2) step = 1 else onClose() },
        )
        StepProgress(step = step, total = 2)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            if (step == 1) {
                StepOne(
                    type = type,
                    onTypeChange = { type = it },
                    hasPhoto = hasPhoto,
                    processingPhoto = processingPhoto,
                    onPickPhoto = {
                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly,
                            ),
                        )
                    },
                    species = species,
                    onSpeciesChange = { species = it; viewModel.clearError() },
                    suggestedSpecies = suggestedSpecies,
                    animalName = animalName,
                    onAnimalNameChange = { animalName = it },
                    color = color,
                    onColorChange = { color = it },
                    breed = breed,
                    onBreedChange = { breed = it },
                )
                Spacer(Modifier.height(24.dp))
                PrimaryButton(text = "Siguiente", onClick = { step = 2 })
            } else {
                StepTwo(
                    type = type,
                    species = species,
                    animalName = animalName,
                    breed = breed,
                    hasLocation = location != null,
                    onPickLocation = {
                        onRequestLocation()
                        showPicker = true
                    },
                    description = description,
                    onDescriptionChange = { description = it; viewModel.clearError() },
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
                Spacer(Modifier.height(24.dp))
                PrimaryButton(
                    text = "Publicar Reporte",
                    onClick = {
                        viewModel.submit(
                            type, species, breed, color, animalName, description,
                        )
                    },
                    loading = submitting,
                    enabled = !submitting && !processingPhoto,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepProgress(step: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        if (i < step) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                    ),
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
    )
}

@Composable
private fun StepOne(
    type: ReportType,
    onTypeChange: (ReportType) -> Unit,
    hasPhoto: Boolean,
    processingPhoto: Boolean,
    onPickPhoto: () -> Unit,
    species: String,
    onSpeciesChange: (String) -> Unit,
    suggestedSpecies: String?,
    animalName: String,
    onAnimalNameChange: (String) -> Unit,
    color: String,
    onColorChange: (String) -> Unit,
    breed: String,
    onBreedChange: (String) -> Unit,
) {
    SectionLabel("Tipo de Reporte")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TypeCard(
            label = "Perdido",
            icon = Icons.Filled.Pets,
            tint = MaterialTheme.appColors.statusLost,
            selected = type == ReportType.LOST,
            onClick = { onTypeChange(ReportType.LOST) },
            modifier = Modifier.weight(1f),
        )
        TypeCard(
            label = "Maltrato",
            icon = Icons.Filled.Warning,
            tint = MaterialTheme.appColors.statusAbuse,
            selected = type == ReportType.ABUSE,
            onClick = { onTypeChange(ReportType.ABUSE) },
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // "Enfermo" no está en el modelo de datos: visible pero inerte.
        Box(modifier = Modifier.weight(1f).comingSoon()) {
            TypeCard(
                label = "Enfermo",
                icon = Icons.Filled.Sick,
                tint = MaterialTheme.appColors.statusSick,
                selected = false,
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
        TypeCard(
            label = "Encontrado",
            icon = Icons.Filled.CheckCircle,
            tint = MaterialTheme.appColors.statusSighting,
            selected = type == ReportType.SIGHTING,
            onClick = { onTypeChange(ReportType.SIGHTING) },
            modifier = Modifier.weight(1f),
        )
    }

    SectionLabel("Fotos")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    MaterialTheme.shapes.medium,
                )
                .clickable(enabled = !processingPhoto, onClick = onPickPhoto),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.AddAPhoto,
                    contentDescription = "Agregar foto",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (hasPhoto) "Cambiar" else "Agregar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        Spacer(Modifier.size(12.dp))
        when {
            processingPhoto -> StatusLine("Procesando foto…")
            hasPhoto -> StatusLine("Foto lista ✓")
        }
    }

    SectionLabel("Información de la Mascota")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        speciesOptions.forEach { option ->
            FilterChip(
                selected = species == option,
                onClick = { onSpeciesChange(option) },
                label = { Text(option) },
                shape = MaterialTheme.shapes.large,
            )
        }
    }
    suggestedSpecies?.let {
        StatusLine("Detectado automáticamente: $it")
    }
    Spacer(Modifier.height(12.dp))
    AppTextField(
        value = animalName,
        onValueChange = onAnimalNameChange,
        label = "Nombre (si lo conoces)",
        placeholder = "Ej: Max, Luna…",
    )
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            AppTextField(
                value = color,
                onValueChange = onColorChange,
                label = "Color Principal",
                placeholder = "Ej: Café, Blanco",
            )
        }
        Box(modifier = Modifier.weight(1f).comingSoon()) {
            // "Sexo" no está en el modelo: visible pero inerte.
            AppTextField(
                value = "",
                onValueChange = {},
                label = "Sexo",
                placeholder = "Próximamente",
                enabled = false,
            )
        }
    }
    Spacer(Modifier.height(12.dp))
    AppTextField(
        value = breed,
        onValueChange = onBreedChange,
        label = "Raza (opcional)",
        placeholder = "Ej: Golden, Criollo…",
    )
}

@Composable
private fun StepTwo(
    type: ReportType,
    species: String,
    animalName: String,
    breed: String,
    hasLocation: Boolean,
    onPickLocation: () -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
) {
    // Resumen del paso 1.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Pets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.size(12.dp))
        Column {
            Text(
                text = animalName.ifBlank { species.ifBlank { "Mascota" } },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = listOf(species, breed)
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
                    .ifBlank { typeLabel(type) },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    SectionLabel("Ubicación")
    LocationRow(hasLocation = hasLocation, onClick = onPickLocation)

    // "Dirección / Referencia" no tiene campo en el modelo: inerte.
    Spacer(Modifier.height(12.dp))
    Box(modifier = Modifier.comingSoon()) {
        AppTextField(
            value = "",
            onValueChange = {},
            label = "Dirección / Referencia",
            placeholder = "Próximamente",
            enabled = false,
        )
    }

    SectionLabel("Fecha y Hora")
    // createdAt es automático: estos campos son cosméticos (deshabilitados).
    Box(modifier = Modifier.comingSoon()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Se registra automáticamente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    SectionLabel("Notas Adicionales")
    AppTextField(
        value = description,
        onValueChange = onDescriptionChange,
        placeholder = "Describe cualquier detalle que pueda ayudar…",
        singleLine = false,
        minLines = 4,
    )
}

@Composable
private fun TypeCard(
    label: String,
    icon: ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.large,
            )
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun LocationRow(hasLocation: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = if (hasLocation) "Ubicación lista ✓ · Tocar para cambiar"
            else "Seleccionar en el mapa",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun typeLabel(type: ReportType): String = when (type) {
    ReportType.LOST -> "Perdido"
    ReportType.SIGHTING -> "Encontrado"
    ReportType.ABUSE -> "Maltrato"
}

@Composable
private fun StatusLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
