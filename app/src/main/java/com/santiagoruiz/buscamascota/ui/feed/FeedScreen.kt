package com.santiagoruiz.buscamascota.ui.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.ui.common.components.ReportListSection

/** Feed de reportes activos, del más reciente al más antiguo. */
@Composable
fun FeedScreen(
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    ReportListSection(
        title = "Reportes",
        state = state,
        emptyMessage = "Aún no hay reportes. Sé el primero en publicar uno.",
        onOpenReport = onOpenReport,
        modifier = modifier,
    )
}
