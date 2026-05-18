package com.santiagoruiz.buscamascota.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.santiagoruiz.buscamascota.ui.common.PlaceholderScreen

/** Búsqueda de reportes. Placeholder de la Fase 1; UI real en fases posteriores. */
@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = "Buscar",
        subtitle = "Aquí podrás buscar reportes y coincidencias.",
        modifier = modifier,
    )
}
