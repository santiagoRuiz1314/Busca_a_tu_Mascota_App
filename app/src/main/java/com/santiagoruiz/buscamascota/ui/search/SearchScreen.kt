package com.santiagoruiz.buscamascota.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.ui.common.components.ReportListSection

/**
 * Pestaña «Buscar»: muestra los reportes de mascotas perdidas del usuario.
 * Tocar uno lanza la búsqueda de coincidencias visuales (avistamientos
 * parecidos en la zona) — la funcionalidad estrella de la app.
 */
@Composable
fun SearchScreen(
    onOpenMatches: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    ReportListSection(
        title = "Buscar coincidencias",
        state = state,
        emptyMessage = "Crea un reporte de mascota perdida para buscar " +
            "avistamientos que se le parezcan.",
        onOpenReport = onOpenMatches,
        modifier = modifier,
    )
}
