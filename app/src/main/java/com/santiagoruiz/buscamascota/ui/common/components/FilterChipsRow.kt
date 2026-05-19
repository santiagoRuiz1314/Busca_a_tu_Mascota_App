package com.santiagoruiz.buscamascota.ui.common.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Una opción de filtro. [enabled] = false → visible pero inerte. */
data class FilterOption(val label: String, val enabled: Boolean = true)

/**
 * Fila horizontal scrolleable de chips de filtro (feed/mapa). El chip
 * seleccionado se rellena con el color de marca; los deshabilitados
 * ("Enfermo", sin datos en el modelo) se muestran atenuados e inertes.
 */
@Composable
fun FilterChipsRow(
    options: List<FilterOption>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option.label == selected,
                onClick = { if (option.enabled) onSelected(option.label) },
                enabled = option.enabled,
                label = {
                    Text(option.label, style = MaterialTheme.typography.labelLarge)
                },
                shape = MaterialTheme.shapes.large,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    }
}
