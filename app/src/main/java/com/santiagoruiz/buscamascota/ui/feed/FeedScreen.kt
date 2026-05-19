package com.santiagoruiz.buscamascota.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import com.santiagoruiz.buscamascota.ui.common.components.AppTopBar
import com.santiagoruiz.buscamascota.ui.common.components.FilterChipsRow
import com.santiagoruiz.buscamascota.ui.common.components.FilterOption
import com.santiagoruiz.buscamascota.ui.common.components.ReportListSection
import com.santiagoruiz.buscamascota.ui.common.components.comingSoon

/** Filtros del feed → tipo de reporte. "Enfermo" no está en el modelo. */
internal val FEED_FILTERS = listOf(
    "Todos" to null,
    "Perdidos" to ReportType.LOST,
    "Maltrato" to ReportType.ABUSE,
    "Encontrado" to ReportType.SIGHTING,
)

/** Feed de reportes activos, del más reciente al más antiguo. */
@Composable
fun FeedScreen(
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var selected by rememberSaveable { mutableStateOf("Todos") }

    val activeType = FEED_FILTERS.firstOrNull { it.first == selected }?.second
    val filteredState = remember(state, activeType) {
        if (state is ReportListUiState.Success && activeType != null) {
            val filtered = (state as ReportListUiState.Success)
                .reports.filter { it.type == activeType }
            if (filtered.isEmpty()) ReportListUiState.Empty
            else ReportListUiState.Success(filtered)
        } else {
            state
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppTopBar(title = "BuscaMascota") {
            // Notificaciones sin lógica: campana decorativa.
            IconButton(onClick = {}, modifier = Modifier.comingSoon()) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        FilterChipsRow(
            options = FEED_FILTERS.map { FilterOption(it.first) } +
                FilterOption("Enfermo", enabled = false),
            selected = selected,
            onSelected = { selected = it },
            modifier = Modifier.padding(vertical = 4.dp),
        )
        ReportListSection(
            title = "",
            state = filteredState,
            emptyMessage = "Aún no hay reportes. Sé el primero en publicar uno.",
            onOpenReport = onOpenReport,
            showTitle = false,
        )
    }
}
