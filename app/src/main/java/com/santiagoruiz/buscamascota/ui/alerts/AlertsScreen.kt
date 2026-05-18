package com.santiagoruiz.buscamascota.ui.alerts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.ui.common.components.ReportListSection

/** Alertas: animales perdidos y casos de maltrato activos. */
@Composable
fun AlertsScreen(
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlertsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    ReportListSection(
        title = "Alertas",
        state = state,
        emptyMessage = "No hay alertas activas por ahora.",
        onOpenReport = onOpenReport,
        modifier = modifier,
    )
}
