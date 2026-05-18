package com.santiagoruiz.buscamascota.ui.report

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.santiagoruiz.buscamascota.ui.common.PlaceholderScreen

/**
 * Creación de reporte (perdido / avistamiento / abuso). Placeholder de la
 * Fase 1; flujo real con foto, ubicación e IA en las Fases 3 y 6.
 */
@Composable
fun CreateReportScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Nuevo reporte",
        subtitle = "Aquí se creará un reporte con foto, ubicación y datos del animal.",
        primaryActionLabel = "Cerrar",
        onPrimaryAction = onClose,
        modifier = modifier,
    )
}
